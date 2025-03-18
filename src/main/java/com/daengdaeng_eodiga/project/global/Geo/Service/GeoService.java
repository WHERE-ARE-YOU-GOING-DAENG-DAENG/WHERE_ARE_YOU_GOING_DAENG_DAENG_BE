package com.daengdaeng_eodiga.project.global.Geo.Service;
import com.daengdaeng_eodiga.project.global.Geo.dto.KakaoApiProperties;
import com.daengdaeng_eodiga.project.global.Geo.dto.KakaoApiResponseDto;
import com.daengdaeng_eodiga.project.global.Geo.dto.KakaoGeoApiDto;
import com.daengdaeng_eodiga.project.global.exception.UserNotFoundException;
import com.daengdaeng_eodiga.project.global.exception.UserNotFoundGeoAsAddress;
import com.daengdaeng_eodiga.project.global.exception.UserNotFoundGeoInfo;
import com.daengdaeng_eodiga.project.place.dto.NoGeoUserInfoDto;
import com.daengdaeng_eodiga.project.user.entity.User;
import com.daengdaeng_eodiga.project.user.repository.UserRepository;
import com.daengdaeng_eodiga.project.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.util.*;

@RequiredArgsConstructor
@Service
public class GeoService {


    private final KakaoApiProperties kakaoApiProperties;
    private final UserService   userService;
    /**
     *
     * 주어진 위도와 경도를 바탕으로 카카오 API를 사용해 지역 정보를 조회합니다.
     *  @author 김승환
     * @param latitude 위도
     * @param longitude 경도
     * @return 지역 정보 문자열 (주소) 또는 null (주소를 찾을 수 없는 경우)
     */
    public String getRegionInfo(double latitude, double longitude)  {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", kakaoApiProperties.getKey());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        String url = kakaoApiProperties.getUrl() + "?x=" + longitude + "&y=" + latitude;
        ResponseEntity<KakaoGeoApiDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, KakaoGeoApiDto.class);
        if (response.getBody() != null) {
            return getMaps(response);
        }

        return null;
    }
    /**
     * 카카오 API의 응답에서 지역 정보를 추출하여 반환합니다.
     * @author 김승환
     * @param response 카카오 API의 응답 객체
     * @return 주소 정보 문자열
     */
    private static String getMaps(ResponseEntity<KakaoGeoApiDto> response) {
        List<KakaoGeoApiDto.Document> addressInfoList = Objects.requireNonNull(response.getBody()).getDocuments();
        if(addressInfoList.isEmpty())
            return "";
        return addressInfoList.get(0).getAddress() != null? addressInfoList.get(0).getAddress().toString() : "";
    }
    /**
     * 위도 경도의 정보가 없는 사용자의 지역 정보를 바탕으로 카카오 API를 사용해 지역 정보를 조회합니다.
     *@author 김승환
     * @param userId 사용자의 ID
     * @return 사용자의 지역 정보를 담고 있는 DTO 객체
     * @throws UserNotFoundException 사용자가 존재하지 않으면 발생
     * @throws UserNotFoundGeoAsAddress 사용자의 주소를 찾을 수 없는 경우 발생
     * @throws UserNotFoundGeoInfo 지리적 정보 처리가 실패한 경우 발생
     */
    public NoGeoUserInfoDto getNotAgreeInfo(Integer userId)  {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", kakaoApiProperties.getKey());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        User user = userService.findUser(userId);
        if (user==null)
            throw new UserNotFoundException();


        String nourl = kakaoApiProperties.getNopeurl() + user.getCity() + " " + user.getCityDetail();
        ResponseEntity<String> response = restTemplate.exchange(nourl, HttpMethod.GET, entity, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoApiResponseDto apiResponseDto = null;
        try {
            apiResponseDto = objectMapper.readValue(response.getBody(), KakaoApiResponseDto.class);
            if (apiResponseDto != null && apiResponseDto.getDocuments() != null && !apiResponseDto.getDocuments().isEmpty()) {
                return getObjects(apiResponseDto);
            }
        } catch (JsonProcessingException e) {
            throw new UserNotFoundGeoAsAddress();
        }

        return null;
    }
    /**
     * 카카오 API 응답에서 사용자의 지리적 정보를 추출하여 DTO 객체로 반환합니다.
     *
     * @param apiResponseDto 카카오 API 응답 객체
     * @return 사용자의 지리적 정보를 담은 DTO 객체
     * @throws UserNotFoundGeoInfo 지리적 정보 처리 시 오류 발생 시 예외
     * @throws UserNotFoundGeoAsAddress 주소 정보 처리 시 오류 발생 시 예외
     */
    private static NoGeoUserInfoDto getObjects(KakaoApiResponseDto apiResponseDto) {
        NoGeoUserInfoDto Ret = new NoGeoUserInfoDto();
        try {
            KakaoApiResponseDto.Document document = apiResponseDto.getDocuments().get(0);
            Double latitude = Double.parseDouble(document.getY());
            Double longitude = Double.parseDouble(document.getX());
            String address = apiResponseDto.getDocuments().get(0).getAddress().getRegion1DepthName() + " " +
                    apiResponseDto.getDocuments().get(0).getAddress().getRegion2DepthName();
            Ret.setLatitude(latitude);
            Ret.setLongitude(longitude);
            Ret.setPlaceName(address);

        } catch (NumberFormatException e) {
            throw new UserNotFoundGeoInfo();
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            throw new UserNotFoundGeoAsAddress();
        }

        return Ret;
    }
    /**
     * 두 지점 간의 거리를 계산하는 메소드 (Haversine 공식을 사용).
     *
     * @param oldLatitude 첫 번째 지점의 위도
     * @param oldLongitude 첫 번째 지점의 경도
     * @param newLatitude 두 번째 지점의 위도
     * @param newLongitude 두 번째 지점의 경도
     * @return 두 지점 간의 거리를 킬로미터 단위로 반환
     */
    public  double calculateDistance(double oldLatitude, double oldLongitude, double newLatitude, double newLongitude) {

        final int R = 6371;

        double lat1 = Math.toRadians(oldLatitude);
        double lon1 = Math.toRadians(oldLongitude);
        double lat2 = Math.toRadians(newLatitude);
        double lon2 = Math.toRadians(newLongitude);


        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;


        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = R * c;

        return distance;
    }


}
