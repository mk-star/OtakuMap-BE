package com.otakumap.domain.event_like.controller;

import com.otakumap.global.security.jwt.annotation.CurrentUser;
import com.otakumap.domain.event_like.converter.EventLikeConverter;
import com.otakumap.domain.event_like.dto.EventLikeRequestDTO;
import com.otakumap.domain.event_like.dto.EventLikeResponseDTO;
import com.otakumap.domain.event_like.service.EventLikeCommandService;
import com.otakumap.domain.event_like.service.EventLikeQueryService;
import com.otakumap.domain.user.entity.User;
import com.otakumap.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event-likes")
@RequiredArgsConstructor
@Validated
public class EventLikeController {
    private final EventLikeQueryService eventLikeQueryService;
    private final EventLikeCommandService eventLikeCommandService;

    @Operation(summary = "이벤트 저장(찜하기)", description = "이벤트를 저장(찜)합니다.")
    @PostMapping("/{eventId}")
    @Parameters({
            @Parameter(name = "eventId", description = "이벤트 ID")
    })
    public ApiResponse<String> postPlaceLike(@CurrentUser User user, @PathVariable Long eventId) {
        eventLikeCommandService.addEventLike(user, eventId);
        return ApiResponse.onSuccess("이벤트가 성공적으로 저장되었습니다.");
    }

    @Operation(summary = "저장된 이벤트 목록 조회(+ 즐겨찾기 목록 조회)", description = "저장된 이벤트 목록을 불러옵니다.")
    @GetMapping( "")
    @Parameters({
            @Parameter(name = "type", description = "이벤트 타입 -> 1: 팝업 스토어, 2: 전시회, 3: 콜라보 카페"),
            @Parameter(name = "isFavorite", description = "즐겨찾기 여부(필수 X) -> true: 즐겨찾기 목록 조회"),
            @Parameter(name = "lastId", description = "마지막으로 조회된 저장된 이벤트 id, 처음 가져올 때 -> 0"),
            @Parameter(name = "limit", description = "한 번에 조회할 최대 이벤트 수. 기본값은 10입니다.")
    })
    public ApiResponse<EventLikeResponseDTO.EventLikePreViewListDTO> getEventLikeList(@CurrentUser User user, @RequestParam(required = false) Integer type, @RequestParam(required = false) Boolean isFavorite, @RequestParam(defaultValue = "0") Long lastId, @RequestParam(defaultValue = "10") int limit) {
        return ApiResponse.onSuccess(eventLikeQueryService.getEventLikeList(user, type, isFavorite, lastId, limit));
    }

    @Operation(summary = "저장된 이벤트 삭제(이벤트 찜하기 취소)", description = "저장된 이벤트를 삭제합니다.")
    @DeleteMapping("")
    @Parameters({
            @Parameter(name = "eventIds", description = "이벤트 ID List"),
    })
    public ApiResponse<String> deleteEventLike(@RequestParam(required = false) List<Long> eventIds, @CurrentUser User user) {
        eventLikeCommandService.deleteEventLike(eventIds, user);
        return ApiResponse.onSuccess("저장된 이벤트가 성공적으로 삭제되었습니다");
    }

    @Operation(summary = "저장된 이벤트 즐겨찾기/즐겨찾기 취소", description = "저장된 이벤트를 즐겨찾기 또는 취소합니다.")
    @PatchMapping("/{eventLikeId}/favorites")
    public ApiResponse<EventLikeResponseDTO.FavoriteResultDTO> favoriteEventLike(@PathVariable Long eventLikeId, @RequestBody @Valid EventLikeRequestDTO.FavoriteDTO request) {
        return ApiResponse.onSuccess(EventLikeConverter.toFavoriteResultDTO(eventLikeCommandService.favoriteEventLike(eventLikeId, request)));
    }
}
