package com.otakumap.domain.map.controller;

import com.otakumap.global.security.jwt.annotation.CurrentUser;
import com.otakumap.domain.map.dto.MapResponseDTO;
import com.otakumap.domain.map.service.MapCustomService;
import com.otakumap.domain.user.entity.User;
import com.otakumap.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
@Validated
public class MapController {

    private final MapCustomService mapCustomService;

    @GetMapping("/details")
    @Operation(summary = "지도에서 장소 및 이벤트 정보 보기", description = "장소의 Latitude, Longitude 수령해 해당 장소의 명소와 이벤트를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    @Parameters({
            @Parameter(name = "latitude"),
            @Parameter(name = "longitude"),
    })
    public ApiResponse<MapResponseDTO.MapDetailDTO> getSearchedPlaceInfoList(
                                                                @CurrentUser User user,
                                                                @RequestParam Double latitude,
                                                                @RequestParam Double longitude) {
        return ApiResponse.onSuccess(mapCustomService.findAllMapDetails(user, latitude, longitude));
    }
}
