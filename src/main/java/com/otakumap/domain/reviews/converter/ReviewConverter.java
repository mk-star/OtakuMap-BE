package com.otakumap.domain.reviews.converter;

import com.otakumap.domain.animation.entity.Animation;
import com.otakumap.domain.event_review.entity.EventReview;
import com.otakumap.domain.image.converter.ImageConverter;
import com.otakumap.domain.image.dto.ImageResponseDTO;
import com.otakumap.domain.image.entity.Image;
import com.otakumap.domain.mapping.EventReviewPlace;
import com.otakumap.domain.mapping.PlaceAnimation;
import com.otakumap.domain.mapping.PlaceReviewPlace;
import com.otakumap.domain.place.entity.Place;
import com.otakumap.domain.place_review.entity.PlaceReview;
import com.otakumap.domain.reviews.dto.ReviewRequestDTO;
import com.otakumap.domain.reviews.dto.ReviewResponseDTO;
import com.otakumap.domain.route.converter.RouteConverter;
import com.otakumap.domain.route.entity.Route;
import com.otakumap.domain.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReviewConverter {

    public static ReviewResponseDTO.Top7ReviewPreViewDTO toTop7EventReviewPreViewDTO(EventReview eventReview) {
        return ReviewResponseDTO.Top7ReviewPreViewDTO.builder()
                .id(eventReview.getId())
                .title(eventReview.getTitle())
                .reviewImage(eventReview.getImages().get(0).getFileUrl())
                .type("event")
                .build();
    }

    public static ReviewResponseDTO.Top7ReviewPreViewDTO toTop7PlaceReviewPreViewDTO(PlaceReview eventReview) {
        return ReviewResponseDTO.Top7ReviewPreViewDTO.builder()
                .id(eventReview.getId())
                .title(eventReview.getTitle())
                .reviewImage(eventReview.getImages().get(0).getFileUrl())
                .type("place")
                .build();
    }

    public static ReviewResponseDTO.Top7ReviewPreViewListDTO top7ReviewPreViewListDTO(List<ReviewResponseDTO.Top7ReviewPreViewDTO> reviews) {
        return ReviewResponseDTO.Top7ReviewPreViewListDTO.builder()
                .reviews(reviews)
                .build();
    }

    public static ReviewResponseDTO.SearchedReviewPreViewDTO toSearchedEventReviewPreviewDTO(EventReview eventReview) {
        return ReviewResponseDTO.SearchedReviewPreViewDTO.builder()
                .reviewId(eventReview.getId())
                .title(eventReview.getTitle())
                .content(eventReview.getContent())
                .reviewImage(ImageConverter.toImageDTO(!eventReview.getImages().isEmpty() ? eventReview.getImages().get(0) : null))
                .view(eventReview.getView())
                .createdAt(eventReview.getCreatedAt())
                .type("event")
                .build();
    }

    public static ReviewResponseDTO.SearchedReviewPreViewDTO toSearchedPlaceReviewPreviewDTO(PlaceReview placeReview) {

        return ReviewResponseDTO.SearchedReviewPreViewDTO.builder()
                .reviewId(placeReview.getId())
                .title(placeReview.getTitle())
                .content(placeReview.getContent())
                .reviewImage(ImageConverter.toImageDTO(!placeReview.getImages().isEmpty() ? placeReview.getImages().get(0) : null))
                .view(placeReview.getView())
                .createdAt(placeReview.getCreatedAt())
                .type("place")
                .build();
    }

    public static ReviewResponseDTO.ReviewDetailDTO toPlaceReviewDetailDTO(PlaceReview placeReview) {
        return ReviewResponseDTO.ReviewDetailDTO.builder()
                .reviewId(placeReview.getId())
                .animationName(placeReview.getAnimation().getName() != null ? placeReview.getAnimation().getName() : null)
                .title(placeReview.getTitle())
                .view(placeReview.getView())
                .content(placeReview.getContent())
                .price(placeReview.getPrice())
                .reviewImages(placeReview.getImages().stream()
                        .filter(Objects::nonNull)
                        .map(ImageConverter::toImageDTO)
                        .toList())
                .nickname(placeReview.getUser().getNickname())
                .profileImage(placeReview.getUser().getProfileImage())
                .createdAt(placeReview.getCreatedAt())
                .route(placeReview.getRoutes().isEmpty() ? null : RouteConverter.toRouteDTO(placeReview.getRoutes().get(0)))
                .build();
    }

    public static ReviewResponseDTO.ReviewDetailDTO toEventReviewDetailDTO(EventReview eventReview) {
        return ReviewResponseDTO.ReviewDetailDTO.builder()
                .reviewId(eventReview.getId())
                .animationName(eventReview.getAnimation().getName() != null ? eventReview.getAnimation().getName() : null)
                .title(eventReview.getTitle())
                .view(eventReview.getView())
                .content(eventReview.getContent())
                .price(eventReview.getPrice())
                .reviewImages(eventReview.getImages().stream()
                        .filter(Objects::nonNull)
                        .map(ImageConverter::toImageDTO)
                        .toList())
                .nickname(eventReview.getUser().getNickname())
                .profileImage(eventReview.getUser().getProfileImage())
                .createdAt(eventReview.getCreatedAt())
                .route(eventReview.getRoutes().isEmpty() ? null : RouteConverter.toRouteDTO(eventReview.getRoutes().get(0)))
                .build();
    }

    public static ReviewResponseDTO.CreatedReviewDTO toCreatedReviewDTO(Long reviewId, String title) {
        return ReviewResponseDTO.CreatedReviewDTO.builder()
                .reviewId(reviewId)
                .title(title)
                .createdAt(LocalDateTime.now())
                .build();
    }


    public static EventReview toEventReview(ReviewRequestDTO.CreateDTO request, User user, Route route, Long price) {
        EventReview eventReview = EventReview.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .view(0L)
                .user(user)
                .price(price)
                .build();

        // route를 리스트에 추가
        route.setEventReview(eventReview);
        return eventReview;
    }


    public static PlaceReview toPlaceReview(ReviewRequestDTO.CreateDTO request, User user, Route route, Long price) {
        PlaceReview placeReview = PlaceReview.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .view(0L)
                .user(user)
                .price(price)
                .build();

        // route를 리스트에 추가
        route.setPlaceReview(placeReview);
        return placeReview;
    }

    public static List<PlaceReviewPlace> toPlaceReviewPlaceList(List<Place> places, PlaceReview placeReview) {
        return places.stream()
                .map(place -> PlaceReviewPlace.builder()
                        .placeReview(placeReview)
                        .place(place)
                        .build())
                .collect(Collectors.toList());
    }

    public static List<EventReviewPlace> toEventReviewPlaceList(List<Place> places, EventReview eventReview) {
        return places.stream()
                .map(place -> EventReviewPlace.builder()
                        .eventReview(eventReview)
                        .place(place)
                        .build())
                .collect(Collectors.toList());
    }

    public static Place toPlace(ReviewRequestDTO.RouteDTO routeDTO) {
        return Place.builder()
                .name(routeDTO.getName())
                .lat(routeDTO.getLat())
                .lng(routeDTO.getLng())
                .detail(routeDTO.getDetail())
                .isFavorite(false)
                .build();
    }

    public static PlaceAnimation toPlaceAnimation(Place place, Animation animation) {
        return PlaceAnimation.builder()
                .place(place)
                .animation(animation)
                .build();
    }

    public static ReviewResponseDTO.IsPurchasedReviewDTO toIsPurchasedReviewDTO(Boolean isPurchased) {
        return ReviewResponseDTO.IsPurchasedReviewDTO.builder()
                .isPurchased(isPurchased)
                .build();
    }
}