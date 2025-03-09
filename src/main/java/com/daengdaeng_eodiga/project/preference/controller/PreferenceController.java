package com.daengdaeng_eodiga.project.preference.controller;

import com.daengdaeng_eodiga.project.global.Security.config.CustomOAuth2User;
import com.daengdaeng_eodiga.project.global.dto.ApiResponse;
import com.daengdaeng_eodiga.project.preference.dto.PreferenceRequestDto;
import com.daengdaeng_eodiga.project.preference.dto.PreferenceResponseDto;
import com.daengdaeng_eodiga.project.preference.service.PreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/preferences")
public class PreferenceController {
    private final PreferenceService preferenceService;

    /**
     * 선호도 등록.
	 * 
	 * 입력받은 선호장소타입, 선호장소특징을 선호도 DB에 저장
     * 
     * @author 하진서
     * @return PreferenceResponseDto
     */    
    @PostMapping
    public ResponseEntity<ApiResponse<?>> registerPreference(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @Valid @RequestBody PreferenceRequestDto preferenceRequestDto) {
        int userId = customOAuth2User.getUserDTO().getUserid();
        preferenceService.registerPreference(userId, preferenceRequestDto);
        return ResponseEntity.ok(ApiResponse.success("preferences insert succesfully"));
    }

    /**
     * 선호도 수정.
	 * 
	 * 수정된 선호장소타입, 선호장소특징을 선호도 DB에 갱신
     * 
     * @author 하진서
     * @return PreferenceResponseDto
     */      
    @PutMapping
    public ResponseEntity<ApiResponse<?>> updatePreference(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @Valid @RequestBody PreferenceRequestDto preferenceRequestDto) {
        int userId = customOAuth2User.getUserDTO().getUserid();
        preferenceService.updatePreference(userId, preferenceRequestDto);
        return ResponseEntity.ok(ApiResponse.success("preferences update succesfully"));
    }

    /**
     * 선호도 목록 조회.
	 * 
	 * 선호장소타입, 선호장소특징 목록 조회
     * 
     * @author 하진서
     * @return List<PreferenceResponseDto>
     */      
    @GetMapping
    public ResponseEntity<ApiResponse<?>> fetchPreferences(@AuthenticationPrincipal CustomOAuth2User customOAuth2User){
        int userId = customOAuth2User.getUserDTO().getUserid();
        List<PreferenceResponseDto> response =  preferenceService.fetchPreferences(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
