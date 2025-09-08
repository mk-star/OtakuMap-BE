package com.otakumap.domain.event_short_review_reaction.controller;

import com.otakumap.global.security.jwt.annotation.CurrentUser;
import com.otakumap.domain.event_short_review_reaction.dto.EventShortReviewReactionResponseDTO;
import com.otakumap.domain.event_short_review_reaction.converter.EventShortReviewReactionConverter;
import com.otakumap.domain.event_short_review_reaction.entity.EventShortReviewReaction;
import com.otakumap.domain.event_short_review_reaction.service.EventShortReviewReactionCommandService;
import com.otakumap.domain.user.entity.User;
import com.otakumap.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events/short-reviews/{reviewId}/reaction")
public class EventShortReviewReactionController {
    private final EventShortReviewReactionCommandService eventReactionCommandService;

    @PostMapping
    @Operation(summary = "이벤트 한줄 리뷰에 좋아요/싫어요 남기기 및 취소하기", description = "0을 요청하면 dislike, 1을 요청하면 like이며, 이미 존재하는 반응을 요청하면 취소됩니다.")
    public ApiResponse<EventShortReviewReactionResponseDTO.ReactionResponseDTO> reactToReview(
            @CurrentUser User user, @PathVariable Long reviewId, @Valid @RequestBody int reactionType) {
        EventShortReviewReaction eventShortReviewReaction = eventReactionCommandService.reactToReview(user, reviewId, reactionType);
        return ApiResponse.onSuccess(EventShortReviewReactionConverter.toReactionResponseDTO(eventShortReviewReaction.getEventShortReview(), eventShortReviewReaction));
    }
}
