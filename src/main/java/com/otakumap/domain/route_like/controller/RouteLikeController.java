package com.otakumap.domain.route_like.controller;

import com.otakumap.global.security.jwt.annotation.CurrentUser;
import com.otakumap.domain.route_like.converter.RouteLikeConverter;
import com.otakumap.domain.route_like.dto.RouteLikeRequestDTO;
import com.otakumap.domain.route_like.dto.RouteLikeResponseDTO;
import com.otakumap.domain.route_like.service.RouteLikeCommandService;
import com.otakumap.domain.route_like.service.RouteLikeQueryService;
import com.otakumap.domain.user.entity.User;
import com.otakumap.global.apiPayload.ApiResponse;
import com.otakumap.global.validation.annotation.ExistRouteLike;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/route-likes")
@RequiredArgsConstructor
@Validated
public class RouteLikeController {

    private final RouteLikeCommandService routeLikeCommandService;
    private final RouteLikeQueryService routeLikeQueryService;

    @Operation(summary = "루트 저장", description = "루트를 저장합니다.")
    @PostMapping("/{routeId}")
    @Parameters({
            @Parameter(name = "routeId", description = "루트 Id")
    })
    public ApiResponse<String> saveRouteLike(@PathVariable Long routeId, @CurrentUser User user) {

        routeLikeCommandService.saveRouteLike(user, routeId);

        return ApiResponse.onSuccess("루트가 성공적으로 저장되었습니다.");
    }

    @Operation(summary = "저장된 루트 삭제", description = "저장된 루트를 삭제합니다.")
    @DeleteMapping("")
    @Parameters({
            @Parameter(name = "routeIds", description = "저장된 루트 id List"),
    })
    public ApiResponse<String> deleteSavedRoute(@RequestParam(required = false) @ExistRouteLike List<Long> routeIds) {
        routeLikeCommandService.deleteRouteLike(routeIds);
        return ApiResponse.onSuccess("저장된 루트가 성공적으로 삭제되었습니다.");
    }

    @Operation(summary = "저장된 루트 즐겨찾기/즐겨찾기 취소", description = "저장된 루트를 즐겨찾기 또는 취소합니다.")
    @PatchMapping("/{routeLikeId}/favorites")
    public ApiResponse<RouteLikeResponseDTO.FavoriteResultDTO> favoriteRouteLike(@PathVariable Long routeLikeId, @RequestBody @Valid RouteLikeRequestDTO.FavoriteDTO request) {
        return ApiResponse.onSuccess(RouteLikeConverter.toFavoriteResultDTO(routeLikeCommandService.favoriteRouteLike(routeLikeId, request)));
    }

    @Operation(summary = "커스텀 루트 저장", description = "기존 루트에서(다른 유저의 루트) 일부를 수정/삭제하여 새로운 커스텀 루트를 저장합니다.")
    @PostMapping("/custom")
    public ApiResponse<RouteLikeResponseDTO.CustomRouteSaveResultDTO> saveCustomRouteLike(@RequestBody @Valid RouteLikeRequestDTO.SaveCustomRouteLikeDTO request, @CurrentUser User user) {
        return ApiResponse.onSuccess(RouteLikeConverter.toCustomRouteSaveResultDTO(routeLikeCommandService.saveCustomRouteLike(request, user)));
    }

    @Operation(summary = "저장된 루트 수정", description = "저장된 루트에서 일부를 수정/삭제합니다.")
    @PatchMapping("")
    public ApiResponse<RouteLikeResponseDTO.RouteUpdateResultDTO> updateRouteLike(@RequestBody @Valid RouteLikeRequestDTO.UpdateRouteLikeDTO request, @CurrentUser User user) {
        return ApiResponse.onSuccess(RouteLikeConverter.toRouteUpdateResultDTO(routeLikeCommandService.updateRouteLike(request, user)));
    }

    @Operation(summary = "저장된 루트 목록 조회(+ 즐겨찾기 목록 조회)", description = "저장된 루트 목록을 불러옵니다.")
    @GetMapping("")
    @Parameters({
            @Parameter(name = "isFavorite", description = "즐겨찾기 여부(필수 X) -> true: 즐겨찾기 목록 조회"),
            @Parameter(name = "lastId", description = "마지막으로 조회된 저장된 루트 id, 처음 가져올 때 -> 0"),
            @Parameter(name = "limit", description = "한 번에 조회할 최대 루트 수. 기본값은 10입니다.")
    })
    public ApiResponse<RouteLikeResponseDTO.RouteLikePreViewListDTO> getRouteLikeList(@CurrentUser User user, @RequestParam(required = false) Boolean isFavorite, @RequestParam(defaultValue = "0") Long lastId, @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.onSuccess(routeLikeQueryService.getRouteLikeList(user, isFavorite, lastId, limit));
    }
}
