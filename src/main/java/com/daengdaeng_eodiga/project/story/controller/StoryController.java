package com.daengdaeng_eodiga.project.story.controller;

import com.daengdaeng_eodiga.project.global.Security.config.CustomOAuth2User;
import com.daengdaeng_eodiga.project.global.dto.ApiResponse;
import com.daengdaeng_eodiga.project.story.dto.*;
import com.daengdaeng_eodiga.project.story.service.StoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/story")
public class StoryController {
    private final StoryService storyService;

    /**
     * 스토리 업로드.
     * 
     * 땅 정보, 미디어 경로가 포함
     * 
     * @author 하진서
     * @return String
     */     
    @PostMapping
    public ResponseEntity<ApiResponse<String>> registerStory(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @Valid @RequestBody StoryRequestDto storyRequestDto
    ){
        int userId = customOAuth2User.getUserDTO().getUserid();
        storyService.registerStory(userId,storyRequestDto);
        return ResponseEntity.ok(ApiResponse.success("story inserted successfully"));
    }

    /**
     * 모든 땅 주인들의 유효 스토리 목록 조회.
     * 
     * 회원인 경우, 본인 제외한 전체 유저 스토리 목록 조회.
     * 비회원인 경우, 모든 유저의 스토리 목록 조회
     * 
     * @author 하진서
     * @return List<?>
     */    
    @GetMapping
    public ResponseEntity<ApiResponse<List<?>>> fetchGroupedUserStories(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ){
        Integer userId = customOAuth2User == null ? null : customOAuth2User.getUserDTO().getUserid();
        List<?> response;
        response = (userId == null) ? storyService.fetchGroupedUserStoriesForNotUser() : storyService.fetchGroupedUserStories(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 내 스토리 목록 조회
     * 
     * @author 하진서
     * @return MyStoriesDto
     */    
    @GetMapping("/mystory")
    public ResponseEntity<ApiResponse<MyStoriesDto>> fetchMyStories(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ) {
        int userId = customOAuth2User.getUserDTO().getUserid();
        MyStoriesDto response = storyService.fetchMyStories(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 유저별 스토리 상세목록 조회
     * 
     * @author 하진서
     * @return IndividualUserStoriesDto
     */    
    @GetMapping("/detail/{landOwnerId}")
    public ResponseEntity<ApiResponse<IndividualUserStoriesDto>> fetchIndividualUserStories(
            @PathVariable int landOwnerId,
            @RequestParam("city") String city,
            @RequestParam("cityDetail") String cityDetail
    ){
        IndividualUserStoriesDto response = storyService.fetchIndividualUserStories(landOwnerId, city, cityDetail);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 스토리 확인
     * 
     * @author 하진서
     * @return String
     */    
    @PutMapping("/{storyId}/viewed")
    public ResponseEntity<ApiResponse<String>> viewStory(
            @Min (1) @PathVariable("storyId") int storyId,
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User
    ){
        int userId = customOAuth2User.getUserDTO().getUserid();
        storyService.viewStory(storyId,userId);
        return ResponseEntity.ok(ApiResponse.success("story viewed successfully"));
    }

    /**
     * 스토리 삭제
     * 
     * @author 하진서
     * @return String
     */    
    @DeleteMapping("/{storyId}")
    public ResponseEntity<ApiResponse<String>> deleteStory(
            @Min (1) @PathVariable("storyId") int storyId
    ){
        storyService.deleteStory(storyId);
        return ResponseEntity.ok(ApiResponse.success("story deleted successfully"));
    }
}
