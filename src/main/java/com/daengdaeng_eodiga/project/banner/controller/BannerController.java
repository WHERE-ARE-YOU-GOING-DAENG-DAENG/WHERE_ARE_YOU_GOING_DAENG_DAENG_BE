package com.daengdaeng_eodiga.project.banner.controller;

import com.daengdaeng_eodiga.project.global.dto.ApiResponse;
import com.daengdaeng_eodiga.project.banner.dto.BannersDto;
import com.daengdaeng_eodiga.project.banner.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
public class BannerController {
    private final BannerService bannerService;

	/**
	 * 배너 목록 조회
	 *
	 * DB에서 유효한 날짜에 해당하는 행사의 배너 목록을 조회
	 *
	 * @author 하진서
	 * @return List<BannersDto> : 배너 관련 정보
	 * */    
    @GetMapping
    public ResponseEntity<ApiResponse<List<BannersDto>>> fetchBanners() {
        List<BannersDto> response = bannerService.fetchBanners();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}