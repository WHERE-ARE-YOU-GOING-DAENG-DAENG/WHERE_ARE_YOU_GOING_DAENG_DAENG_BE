package com.daengdaeng_eodiga.project.pet.controller;

import com.daengdaeng_eodiga.project.global.Security.config.CustomOAuth2User;
import com.daengdaeng_eodiga.project.global.dto.ApiResponse;
import com.daengdaeng_eodiga.project.pet.dto.PetDetailResponseDto;
import com.daengdaeng_eodiga.project.pet.dto.PetListResponseDto;
import com.daengdaeng_eodiga.project.pet.dto.PetRegisterDto;
import com.daengdaeng_eodiga.project.pet.dto.PetUpdateDto;
import com.daengdaeng_eodiga.project.pet.service.PetService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pets")
@RequiredArgsConstructor
@Validated
public class PetController {
    private final PetService petService;

    /**
     * 반려동물 등록
	 * 
	 * 반려동물의 이름,이미지,종,성별,크기,생년월일,중성화여부를 등록
     * 
     * @author 하진서
     * @return String
     */	    
    @PostMapping
    public ResponseEntity<ApiResponse<String>> registerPet(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @Valid @RequestBody PetRegisterDto requestDto) {
        int userId = customOAuth2User.getUserDTO().getUserid();
        petService.registerPet(userId, requestDto);
        return ResponseEntity.ok(ApiResponse.success("pet inserted succesfully"));
    }

    /**
     * 반려동물 정보 수정
	 * 
	 * 반려동물의 이름,이미지,종,성별,크기,생년월일,중성화여부를 수정
     * 
     * @author 하진서
     * @return String
     */	    
    @PutMapping("/{petId}")
    public ResponseEntity<ApiResponse<String>> updatePet(
            @PathVariable @Min(value = 1, message = "Pet ID는 1 이상이어야 합니다.") int petId,
            @Valid @RequestBody PetUpdateDto updateDto) {
        petService.updatePet(petId, updateDto);
        return ResponseEntity.ok(ApiResponse.success("pet updated succesfully"));
    }

    /**
     * 반려동물 목록 조회.
	 * 
	 * 유저 페이지에 표시할 반려동물의 목록 조회
     * 
     * @author 하진서
     * @return List<PetListResponseDto>
     */     
    @GetMapping
    public ResponseEntity<ApiResponse<List<PetListResponseDto>>> fetchPetList(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        int userId = customOAuth2User.getUserDTO().getUserid();
        List<PetListResponseDto> response = petService.fetchUserPetListDto(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 반려동물 상세정보 조회.
	 * 
	 * 유저 페이지에 표시할 반려동물의 상세정보 조회.
	 * 반려동물의 ID,이름,이미지,종,성별,크기,생년월일,중성화여부가 포함
     * 
     * @author 하진서
     * @return PetDetailResponseDto
     */     
    @GetMapping("/{petId}")
    public ResponseEntity<ApiResponse<PetDetailResponseDto>> fetchPetDetail(
            @PathVariable @Min(value = 1, message = "Pet ID는 1 이상이어야 합니다.") int petId) {
        PetDetailResponseDto petDetail = petService.fetchPetDetail(petId);
        return ResponseEntity.ok(ApiResponse.success(petDetail));
    }

    /**
     * 반려동물 정보 삭제.
	 * 
	 * 반려동물 DB에서 해당 반려동물 삭제
     * 
     * @author 하진서
     * @return String
     */	    
    @DeleteMapping("/{petId}")
    public ResponseEntity<ApiResponse<String>> deletePet(
            @AuthenticationPrincipal CustomOAuth2User customOAuth2User,
            @PathVariable @Min(value = 1, message = "Pet ID는 1 이상이어야 합니다.") int petId) {
        int userId = customOAuth2User.getUserDTO().getUserid();
        petService.deletePet(userId, petId);
        return ResponseEntity.ok(ApiResponse.success("pet deleted succesfully"));
    }
}
