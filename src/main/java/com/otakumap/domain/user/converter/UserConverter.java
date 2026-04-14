package com.otakumap.domain.user.converter;

import com.otakumap.domain.auth.dto.*;
import com.otakumap.domain.event_review.entity.EventReview;
import com.otakumap.domain.place_review.entity.PlaceReview;
import com.otakumap.domain.user.dto.UserResponseDTO;
import com.otakumap.domain.user.entity.User;
import com.otakumap.domain.user.entity.enums.Role;
import com.otakumap.domain.user.entity.enums.UserStatus;
import com.otakumap.global.util.UuidGenerator;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class UserConverter {

    public static User toUser(SignUpRequestDTO request) {
        return User.builder()
                .name(request.name())
                .nickname(UuidGenerator.generateUuid())
                .userId(request.userId())
                .email(request.email())
                .password(request.password())
                .isCommunityActivityNotified(true)
                .isEventBenefitsNotified(true)
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    public static User toUser(Long id, String password) {
        return User.builder()
                .id(id)
                .password(password)
                .build();
    }

    public static SignUpResponseDTO toSignupResultDTO(User user) {
        return new SignUpResponseDTO(
                user.getId(),
                LocalDateTime.now()
        );
    }

    public static AuthResponseDTO.LoginResultDTO toLoginResultDTO(Long userId, String accessToken) {
        return AuthResponseDTO.LoginResultDTO.builder()
                .id(userId)
                .accessToken(accessToken)
                .build();

    }

    public static AuthResponseDTO.CheckIdResultDTO toCheckIdResultDTO(boolean isDuplicated) {
        return AuthResponseDTO.CheckIdResultDTO.builder()
                .isDuplicated(isDuplicated)
                .build();
    }

    public static AuthResponseDTO.CheckEmailResultDTO toCheckEmailResultDTO(boolean isDuplicated) {
        return AuthResponseDTO.CheckEmailResultDTO.builder()
                .isDuplicated(isDuplicated)
                .build();
    }

    public static AuthResponseDTO.VerifyCodeResultDTO toVerifyCodeResultDTO(boolean isVerified) {
        return AuthResponseDTO.VerifyCodeResultDTO.builder()
                .isVerified(isVerified)
                .build();
    }

    public static UserResponseDTO.UserInfoResponseDTO toUserInfoResponseDTO(User user) {
        return UserResponseDTO.UserInfoResponseDTO.builder()
                .profileImageUrl(user.getProfileImage() == null ? null : user.getProfileImage())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .donation(user.getDonation())
                .community_activity(user.getIsCommunityActivityNotified())
                .event_benefits_info(user.getIsEventBenefitsNotified())
                .build();
    }

    public static AuthResponseDTO.FindIdResultDTO toFindIdResultDTO(String userId) {
        return AuthResponseDTO.FindIdResultDTO.builder()
                .userId(userId)
                .build();
    }

    public static UserResponseDTO.UserReviewDTO reviewDTO(PlaceReview review, String imageUrl) {
        return UserResponseDTO.UserReviewDTO.builder()
                .reviewId(review.getId())
                .reviewType("place")
                .title(review.getTitle())
                .content(review.getContent())
                .thumbnail(imageUrl)
                .views(review.getView())
                .createdAt(review.getCreatedAt().toLocalDate())
                .build();
    }

    public static UserResponseDTO.UserReviewDTO reviewDTO(EventReview review, String imageUrl) {
        return UserResponseDTO.UserReviewDTO.builder()
                .reviewId(review.getId())
                .reviewType("event")
                .title(review.getTitle())
                .content(review.getContent())
                .thumbnail(imageUrl)
                .views(review.getView())
                .createdAt(review.getCreatedAt().toLocalDate())
                .build();
    }

    public static UserResponseDTO.UserReviewListDTO reviewListDTO(Page<UserResponseDTO.UserReviewDTO> reviews) {
        List<UserResponseDTO.UserReviewDTO> userReviewDTOS = reviews.getContent();

        return UserResponseDTO.UserReviewListDTO.builder()
                .reviews(userReviewDTOS)
                .listSize(userReviewDTOS.size())
                .totalPages(reviews.getTotalPages())
                .totalElements(reviews.getTotalElements())
                .isFirst(reviews.isFirst())
                .isLast(reviews.isLast())
                .build();
    }
}
