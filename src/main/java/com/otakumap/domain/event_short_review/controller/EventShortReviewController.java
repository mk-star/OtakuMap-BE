package com.otakumap.domain.event_short_review.controller;

import com.otakumap.global.security.jwt.annotation.CurrentUser;
import com.otakumap.domain.event_short_review.converter.EventShortReviewConverter;
import com.otakumap.domain.event_short_review.dto.EventShortReviewRequestDTO;
import com.otakumap.domain.event_short_review.dto.EventShortReviewResponseDTO;
import com.otakumap.domain.event_short_review.entity.EventShortReview;
import com.otakumap.domain.event_short_review.service.EventShortReviewCommandService;
import com.otakumap.domain.user.entity.User;
import com.otakumap.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class EventShortReviewController {
    private final EventShortReviewCommandService eventShortReviewCommandService;

    @Operation(summary = "이벤트 한 줄 리뷰 작성", description = "이벤트에 한 줄 리뷰를 작성합니다.")
    @PostMapping("/events/{eventId}/short-reviews")
    @Parameters({
            @Parameter(name = "eventId", description = "특정 이벤트의 Id")
    })
    public ApiResponse<EventShortReviewResponseDTO.NewEventShortReviewDTO> createEventShortReview(@PathVariable Long eventId,
                                                                                                  @CurrentUser User user,
                                                                                                  @RequestBody EventShortReviewRequestDTO.NewEventShortReviewDTO request) {
        EventShortReview eventShortReview = eventShortReviewCommandService.createEventShortReview(eventId, user, request);
        return ApiResponse.onSuccess(EventShortReviewConverter.toNewEventShortReviewDTO(eventShortReview));
    }

    @Operation(summary = "이벤트 한 줄 리뷰 목록 조회", description = "특정 이벤트의 한 줄 리뷰 목록을 불러옵니다.")
    @GetMapping("/events/{eventId}/short-reviews")
    @Parameters({
            @Parameter(name = "eventId", description = "특정 이벤트의 아이디입니다."),
            @Parameter(name = "page", description = "페이지 번호입니다. 0부터 시작합니다.", example = "0")
    })
    public ApiResponse<EventShortReviewResponseDTO.EventShortReviewListDTO> getEventShortReviewList(@PathVariable(name = "eventId") Long eventId, @RequestParam(name = "page")Integer page) {
        return ApiResponse.onSuccess(EventShortReviewConverter.toEventShortReviewListDTO(eventShortReviewCommandService.getEventShortReviewsByEventId(eventId, page)));
    }

    @Operation(summary = "이벤트 한 줄 리뷰 수정", description = "이벤트 한 줄 리뷰의 별점과 내용을 수정합니다.")
    @PatchMapping("/events/short-reviews/{eventShortReviewId}")
    @Parameters({
            @Parameter(name = "eventShortReviewId", description = "특정 한 줄 리뷰의 Id")
    })
    public ApiResponse<String> updateEventShortReview(@PathVariable Long eventShortReviewId, @RequestBody @Valid EventShortReviewRequestDTO.UpdateEventShortReviewDTO request) {
        eventShortReviewCommandService.updateEventShortReview(eventShortReviewId, request);
        return ApiResponse.onSuccess("한 줄 리뷰가 성공적으로 수정되었습니다.");
    }

    @Operation(summary = "이벤트 한 줄 리뷰 삭제", description = "이벤트 한 줄 리뷰를 삭제합니다.")
    @DeleteMapping("/events/short-reviews/{eventShortReviewId}")
    @Parameters({
            @Parameter(name = "eventShortReviewId", description = "특정 한 줄 리뷰의 Id")
    })
    public ApiResponse<String> deleteEventShortReview(@PathVariable Long eventShortReviewId) {
        eventShortReviewCommandService.deleteEventShortReview(eventShortReviewId);
        return ApiResponse.onSuccess("한 줄 리뷰가 성공적으로 삭제되었습니다.");
    }
}
