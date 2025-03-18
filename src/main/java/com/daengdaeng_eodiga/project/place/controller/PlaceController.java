package com.daengdaeng_eodiga.project.place.controller;

import com.daengdaeng_eodiga.project.global.Geo.Service.GeoService;
import com.daengdaeng_eodiga.project.global.Redis.Dto.RedisPlaceDto;
import com.daengdaeng_eodiga.project.global.Redis.Repository.RedisLocationRepository;
import com.daengdaeng_eodiga.project.global.Security.config.CustomOAuth2User;
import com.daengdaeng_eodiga.project.global.dto.ApiResponse;
import com.daengdaeng_eodiga.project.global.exception.NotFoundException;
import com.daengdaeng_eodiga.project.global.exception.UserNotFoundException;
import com.daengdaeng_eodiga.project.place.dto.*;
import com.daengdaeng_eodiga.project.place.entity.ReviewSummary;
import com.daengdaeng_eodiga.project.place.service.OpenAiService;
import com.daengdaeng_eodiga.project.place.service.PlaceService;

import com.daengdaeng_eodiga.project.review.repository.ReviewSummaryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/places")
@Validated
public class PlaceController {

    private final PlaceService placeService;
    private final GeoService geoService;
    private final OpenAiService openAiService;
    private final ReviewSummaryRepository reviewSummaryRepository;
    private final RedisLocationRepository redisLocationRepository;


    @PostMapping("/filter")
    public ResponseEntity<ApiResponse<List<PlaceDto>>> filterPlaces(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @Valid @RequestBody FilterRequest request) {
        Integer userId = customOAuth2User != null ? customOAuth2User.getUserDTO().getUserid() : null;
        List<PlaceDto> places = placeService.filterPlaces(
                request.getCity(),
                request.getCityDetail(),
                request.getPlaceType(),
                request.getLatitude(),
                request.getLongitude(),
                userId
        );
        return ResponseEntity.ok(ApiResponse.success(places));
    }

    @GetMapping("/autocomplete")
    public ResponseEntity<ApiResponse<List<String>>> getAutocompleteSuggestions(@RequestParam String keyword) {
        List<String> suggestions = placeService.getAutocompleteSuggestions(keyword);
        return ResponseEntity.ok(ApiResponse.success(suggestions));
    }

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<PlaceDto>>> searchPlaces(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @Valid @RequestBody SearchRequest request) {
        Integer userId = customOAuth2User != null ? customOAuth2User.getUserDTO().getUserid() : null;
        List<PlaceDto> places = placeService.searchPlaces(
                request.getKeyword(),
                request.getLatitude(),
                request.getLongitude(),
                userId
        );
        return ResponseEntity.ok(ApiResponse.success(places));
    }

    @GetMapping("/{placeId}")
    public ResponseEntity<ApiResponse<PlaceDto>> getPlaceDetails(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable int placeId) {
        Integer userId = customOAuth2User != null ? customOAuth2User.getUserDTO().getUserid() : null;
        PlaceDto placeDetails = placeService.getPlaceDetails(placeId, userId);
        return ResponseEntity.ok(ApiResponse.success(placeDetails));
    }

    @GetMapping("/topfavorites")
    public ResponseEntity<ApiResponse<List<PlaceDto>>> getTopFavoritePlaces(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Integer userId = customOAuth2User != null ? customOAuth2User.getUserDTO().getUserid() : null;
        List<PlaceDto> topFavoritePlaces = placeService.getTopFavoritePlaces(userId);
        return ResponseEntity.ok(ApiResponse.success(topFavoritePlaces));
    }

    @PostMapping("/topscore")
    public ResponseEntity<ApiResponse<List<PlaceDto>>> getTopScoredPlacesWithinRadius(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @Valid @RequestBody TopScoreRequest request) {
        Integer userId = customOAuth2User != null ? customOAuth2User.getUserDTO().getUserid() : null;
        List<PlaceDto> places = placeService.getTopScoredPlacesWithinRadius(
                request.getLatitude(), request.getLongitude(), userId);
        return ResponseEntity.ok(ApiResponse.success(places));
    }

    @PostMapping("/nearest")
    public ResponseEntity<ApiResponse<List<NearestPlaceDto>>> getNearestPlaces(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @Valid @RequestBody NearestRequest request) {
        Integer userId = customOAuth2User != null ? customOAuth2User.getUserDTO().getUserid() : null;
        List<NearestPlaceDto> places = placeService.getNearestPlaces(
                request.getLatitude(), request.getLongitude(), userId);
        return ResponseEntity.ok(ApiResponse.success(places));
    }
    /**
     * 사용자의 위치에 따른 추천 장소를 제공하는 메소드입니다.
     *
     * 이 메소드는 사용자의 위도와 경도를 바탕으로 추천 장소를 반환합니다.
     * 만약 사용자가 이전에 요청한 위치가 캐시되어 있으면, 캐시된 데이터를 활용하여
     * 위치가 5km 이내일 경우 기존 장소 정보를 반환하고, 그렇지 않으면 새로운 장소를 추천합니다.
     * 위도와 경도가 0.0일 경우, 사용자가 장소 정보 동의를 하지 않았을때 처리하는 로직도 포함됩니다.
     *
     * @author 김승환
     * @param customOAuth2User 현재 인증된 사용자의 정보.
     * @param request 사용자로부터 받은 위치 요청 정보.
     * @return 추천된 장소 리스트.
     * @exception UserNotFoundException 사용자가 인증되지 않았을 경우 발생합니다.
     */
    @PostMapping("/recommend")
    public ResponseEntity<ApiResponse<List<PlaceWithScore>>> recommendPlaces(
             @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @Valid @RequestBody NearestRequest request) {
        if(customOAuth2User==null)
        {
            throw new UserNotFoundException();
        }
        Integer userId =customOAuth2User.getUserDTO().getUserid();
        double latitude = request.getLatitude();
        double longitude = request.getLongitude();

        RedisPlaceDto cachedLocation = redisLocationRepository.getLocation(userId);

        if (cachedLocation != null) {
            double oldLatitude = cachedLocation.getLatitude();
            double oldLongitude = cachedLocation.getLongitude();
            double distance = geoService.calculateDistance(oldLatitude, oldLongitude, latitude, longitude);

            if (distance <= 5.0) {
                return ResponseEntity.ok(ApiResponse.success(cachedLocation.getCashingPlaces()));
            }

            if (latitude == 0.0 && longitude == 0.0) {
                return ResponseEntity.ok(ApiResponse.success(cachedLocation.getCashingPlaces()));
            }
        }
        String myplace;
        if (latitude == 0.0 && longitude == 0.0) {
            NoGeoUserInfoDto NoAgreeUserGeoInfo = geoService.getNotAgreeInfo(userId);

            latitude =  NoAgreeUserGeoInfo.getLatitude();
            longitude = NoAgreeUserGeoInfo.getLongitude();
            myplace = NoAgreeUserGeoInfo.getPlaceName();
        } else {
            myplace = geoService.getRegionInfo(latitude, longitude);
        }

        List<PlaceWithScore> places = placeService.RecommendPlaces(myplace, latitude, longitude, userId);
        return ResponseEntity.ok(ApiResponse.success(places));
    }


    @PostMapping("/{placeId}/reviews/summary")
    public ResponseEntity<ApiResponse<String>> createReviewSummary(@PathVariable int placeId) {
        placeService.generateReviewSummary(placeId);
        return ResponseEntity.ok(ApiResponse.success("리뷰 요약이 성공적으로 생성되었습니다!"));
    }

    @GetMapping("/{placeId}/reviews/summary")
    public ResponseEntity<ReviewSummaryDto> getReviewSummary(@PathVariable Integer placeId) {
        ReviewSummary reviewSummary = reviewSummaryRepository.findById(placeId)
                .orElseThrow(() -> new NotFoundException("Review Summary", "Place ID: " + placeId));

        ReviewSummaryDto response = new ReviewSummaryDto(
                reviewSummary.getPlaceId(),
                reviewSummary.getGoodSummary(),
                reviewSummary.getBadSummary(),
                reviewSummary.getUpdateDate()
        );
        return ResponseEntity.ok(response);
    }
}
