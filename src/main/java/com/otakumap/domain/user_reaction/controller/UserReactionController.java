package com.otakumap.domain.user_reaction.controller;

import com.otakumap.global.security.jwt.annotation.CurrentUser;
import com.otakumap.domain.user.entity.User;
import com.otakumap.domain.user_reaction.DTO.UserReactionResponseDTO;
import com.otakumap.domain.user_reaction.converter.UserReactionConverter;
import com.otakumap.domain.user_reaction.entity.UserReaction;
import com.otakumap.domain.user_reaction.service.UserReactionCommandService;
import com.otakumap.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserReactionController {
    private final UserReactionCommandService userReactionCommandService;

    @PostMapping("/places/{placeId}/short-reviews/{reviewId}/reaction")
    @Operation(summary = "명소 한줄 리뷰에 좋아요/싫어요 남기기 및 취소하기", description = "0을 요청하면 dislike, 1을 요청하면 like이며, 이미 존재하는 반응을 요청하면 취소됩니다.")
    public ApiResponse<UserReactionResponseDTO.ReactionResponseDTO> reactToReview(
            @CurrentUser User user, @PathVariable Long placeId, @PathVariable Long reviewId, @Valid @RequestBody int reactionType) {
        UserReaction userReaction = userReactionCommandService.reactToReview(user, reviewId, reactionType);
        return ApiResponse.onSuccess(UserReactionConverter.toReactionResponseDTO(userReaction.getPlaceShortReview(), userReaction));
    }
}
