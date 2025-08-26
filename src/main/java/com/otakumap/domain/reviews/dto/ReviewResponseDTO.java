package com.otakumap.domain.reviews.dto;

import com.otakumap.domain.image.dto.ImageResponseDTO;
import com.otakumap.domain.route.dto.RouteResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ReviewResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Top7ReviewPreViewListDTO {
        private List<Top7ReviewPreViewDTO> reviews;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Top7ReviewPreViewDTO {
        private Long id;
        private String title;
        private String reviewImage;
        private Long view;
        private String type;
        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchedReviewPreViewDTO {
        Long reviewId; // 검색된 Review의 id
        String title;
        String content;
        String type; // "event" 또는 "place"
        Long view;
        LocalDateTime createdAt;
        ImageResponseDTO.ImageDTO reviewImage;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewDetailDTO {
        Long reviewId;
        String animationName;
        String title;
        Long view;
        String content;
        Long price;
        List<ImageResponseDTO.ImageDTO> reviewImages;

        String nickname;
        ImageResponseDTO.ImageDTO profileImage;
        LocalDateTime createdAt;

        RouteResponseDTO.RouteDTO route;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatedReviewDTO {
        Long reviewId;
        String title;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseReviewDTO {
        private Long remainingPoints;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IsPurchasedReviewDTO {
        private Boolean isPurchased;
    }
}
