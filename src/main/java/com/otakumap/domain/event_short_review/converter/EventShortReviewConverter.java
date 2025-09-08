package com.otakumap.domain.event_short_review.converter;

import com.otakumap.domain.user.entity.User;
import com.otakumap.domain.event.entity.Event;
import com.otakumap.domain.event_short_review.dto.EventShortReviewRequestDTO;
import com.otakumap.domain.event_short_review.dto.EventShortReviewResponseDTO;
import com.otakumap.domain.event_short_review.entity.EventShortReview;
import com.otakumap.domain.image.converter.ImageConverter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public class EventShortReviewConverter {
    public static EventShortReview toEventShortReview(EventShortReviewRequestDTO.NewEventShortReviewDTO request, Event event, User user) {
        return EventShortReview.builder()
                .event(event)
                .user(user)
                .rating(request.getRating())
                .content(request.getContent())
                .build();
    }

    public static EventShortReviewResponseDTO.NewEventShortReviewDTO toNewEventShortReviewDTO(EventShortReview eventShortReview) {
        return EventShortReviewResponseDTO.NewEventShortReviewDTO.builder()
                .userId(eventShortReview.getUser().getId())
                .userName(eventShortReview.getUser().getName())
                .eventId(eventShortReview.getEvent().getId())
                .content(eventShortReview.getContent())
                .rating(eventShortReview.getRating())
                .profileImage(eventShortReview.getUser().getProfileImage())
                .build();
    }

    public static EventShortReviewResponseDTO.EventShortReviewDTO toEventShortReviewDTO(EventShortReview eventShortReview) {
        return EventShortReviewResponseDTO.EventShortReviewDTO.builder()
                .id(eventShortReview.getId())
                .user(EventShortReviewConverter.toEventShortReviewUserDTO(eventShortReview.getUser()))
                .content(eventShortReview.getContent())
                .rating(eventShortReview.getRating())
                .profileImage(eventShortReview.getUser().getProfileImage())
                .build();
    }

    public static EventShortReviewResponseDTO.EventShortReviewListDTO toEventShortReviewListDTO(Page<EventShortReview> reviewList) {
        List<EventShortReviewResponseDTO.EventShortReviewDTO> reviewDTOList = reviewList.stream()
                .map(EventShortReviewConverter::toEventShortReviewDTO).collect(Collectors.toList());

        return EventShortReviewResponseDTO.EventShortReviewListDTO.builder()
                .eventShortReviewList(reviewDTOList)
                .currentPage(reviewList.getNumber())
                .totalPages(reviewList.getTotalPages())
                .build();
    }

    public static EventShortReviewResponseDTO.EventShortReviewUserDTO toEventShortReviewUserDTO(User user) {
      return EventShortReviewResponseDTO.EventShortReviewUserDTO.builder()
              .userId(user.getId())
              .nickname(user.getNickname())
              .profileImage(user.getProfileImage())
              .build();
    }
}
