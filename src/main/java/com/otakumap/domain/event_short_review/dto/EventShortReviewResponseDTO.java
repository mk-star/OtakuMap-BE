package com.otakumap.domain.event_short_review.dto;

import com.otakumap.domain.image.dto.ImageResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class EventShortReviewResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewEventShortReviewDTO {
        Long userId;
        Long eventId;
        String content;
        Float rating;
        String userName;
        String profileImage;
        int likes;
        int dislikes;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventShortReviewListDTO {
        List<EventShortReviewDTO> eventShortReviewList;
        Integer currentPage;
        Integer totalPages;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventShortReviewDTO {
        private Long id;
        private EventShortReviewUserDTO user;
        private String content;
        private Float rating;
        private String profileImage;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventShortReviewUserDTO {
        private Long userId;
        private String nickname;
        private String profileImage;
    }
}
