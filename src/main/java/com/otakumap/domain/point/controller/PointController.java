package com.otakumap.domain.point.controller;

import com.otakumap.global.security.jwt.annotation.CurrentUser;
import com.otakumap.domain.payment.service.PaymentCommandService;
import com.otakumap.domain.point.converter.PointConverter;
import com.otakumap.domain.point.dto.PointResponseDTO;
import com.otakumap.domain.point.service.PointQueryservice;
import com.otakumap.domain.user.entity.User;
import com.otakumap.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PointController {
    private final PaymentCommandService paymentCommandService;
    private final PointQueryservice pointQueryservice;

    @Operation(summary = "포인트 충전", description = "사용자가 포인트를 충전합니다.")
    @PostMapping("/charge")
    public ApiResponse<String> processOrder(@RequestBody PointResponseDTO.PointSaveResponseDTO pointResponseDTO, @CurrentUser User user) {
        return ApiResponse.onSuccess(paymentCommandService.savePoint(pointResponseDTO, user));
    }

    @Operation(summary = "포인트 충전 내역 확인", description = "포인트 충전 내역을 확인합니다. page는 1부터 시작합니다.")
        @PostMapping("/transactions/charges")
        public ApiResponse<PointResponseDTO.PointPreViewListDTO> getChargePointList(@CurrentUser User user, @RequestParam(name = "page") Integer page) {
            return ApiResponse.onSuccess(PointConverter.pointPreViewListDTO(pointQueryservice.getChargePointList(user, page)));
    }

    @Operation(summary = "현재 포인트 조회", description = "현재 포인트를 조회합니다.")
    @GetMapping("/balance")
    public ApiResponse<PointResponseDTO.CurrentPointDTO> getCurrentPointBalance(@CurrentUser User user) {
        return ApiResponse.onSuccess(pointQueryservice.getCurrentPoint(user));
    }
}