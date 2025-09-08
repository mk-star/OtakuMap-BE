package com.otakumap.domain.event.controller;

import com.otakumap.global.security.jwt.annotation.CurrentUser;
import com.otakumap.domain.event.converter.EventConverter;
import com.otakumap.domain.event.dto.EventResponseDTO;
import com.otakumap.domain.event.service.EventCustomService;
import com.otakumap.domain.event.service.EventQueryService;
import com.otakumap.domain.image.dto.ImageResponseDTO;
import com.otakumap.domain.user.entity.User;
import com.otakumap.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class EventController {

    private final EventQueryService eventQueryService;
    private final EventCustomService eventCustomService;

    @Operation(summary = "진행 중인 인기 이벤트 조회", description = "진행 중인 인기 이벤트의 목록(8개)를 불러옵니다.")
    @GetMapping("/events/popular")
    public ApiResponse<List<EventResponseDTO.EventWithLikeDTO>> getEventDetail(@CurrentUser User user) throws InterruptedException {
        return ApiResponse.onSuccess(eventCustomService.getPopularEvents(user));
    }

    @Operation(summary = "이벤트 상세 정보 조회", description = "특정 이벤트의 상세 정보를 불러옵니다.")
    @GetMapping("/events/{eventId}/details")
    @Parameter(name = "eventId", description = "이벤트 Id")
    public ApiResponse<EventResponseDTO.EventDetailDTO> getEventDetail(@PathVariable Long eventId) {
        return ApiResponse.onSuccess(eventQueryService.getEventDetail(eventId));
    }

    @Operation(summary = "홈 화면 이벤트 배너 조회", description = "홈 화면에 띄울 배너 이미지를 불러옵니다.")
    @GetMapping("/events/banner")
    public ApiResponse<ImageResponseDTO.ImageDTO> getBanner() {
        return ApiResponse.onSuccess(eventCustomService.getEventBanner());
    }

    @Operation(summary = "카테고리별로 이벤트 검색", description = "카테고리별로 이벤트를 검색합니다.")
    @GetMapping("/events/category")
    public ApiResponse<EventResponseDTO.EventSearchResultDTO> searchEventByCategory(
            @Parameter(description = "애니메이션 장르 (ALL, ROMANCE, ACTION, FANTASY, THRILLER, SPORTS)", example = "ALL") @RequestParam(required = false) String genre,
            @Parameter(description = "이벤트 상태 (IN_PROCESS, NOT_STARTED)") @RequestParam(required = false) String status,
            @Parameter(description = "이벤트 종류 (ALL, POPUP_STORE, EXHIBITION, COLLABORATION_CAFE)") @RequestParam(required = false) String type,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam Integer page,
            @RequestParam Integer size) {
        return ApiResponse.onSuccess(EventConverter.toEventSearchResultDTO(eventCustomService.searchEventByCategory(genre, status, type, page, size)));
    }

    @Operation(summary = "이벤트 이름/작품명으로 이벤트 검색", description = "이벤트 이름과 작품명으로 이벤트를 검색합니다.")
    @GetMapping("/events/search")
    public ApiResponse<EventResponseDTO.EventSearchResultDTO> searchEventByKeyword(
            @Parameter(description = "검색어") @RequestParam String keyword,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam Integer page,
            @Parameter(description = "한 페이지에 가져올 이벤트 개수", example = "10") @RequestParam Integer size) {

        return ApiResponse.onSuccess(eventQueryService.searchEventByKeyword(keyword, page, size));
    }
}
