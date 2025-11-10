package com.otakumap.domain.reviews.service;

import com.otakumap.domain.event_review.entity.EventReview;
import com.otakumap.domain.event_review.repository.EventReviewRepository;
import com.otakumap.domain.place_review.entity.PlaceReview;
import com.otakumap.domain.place_review.repository.PlaceReviewRepository;
import com.otakumap.domain.reviews.converter.ReviewConverter;
import com.otakumap.domain.reviews.dto.ReviewResponseDTO;
import com.otakumap.domain.reviews.enums.ReviewType;
import com.otakumap.domain.reviews.repository.ReviewRepositoryCustom;
import com.otakumap.domain.user.entity.User;
import com.otakumap.global.apiPayload.code.status.ErrorStatus;
import com.otakumap.global.apiPayload.exception.handler.EventHandler;
import com.otakumap.global.apiPayload.exception.handler.PlaceHandler;
import com.otakumap.global.apiPayload.exception.handler.ReviewHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewQueryServiceImpl implements ReviewQueryService {
    private final ReviewRepositoryCustom reviewRepositoryCustom;
    private final EventReviewRepository eventReviewRepository;
    private final PlaceReviewRepository placeReviewRepository;

    @Override
    public Page<ReviewResponseDTO.SearchedReviewPreViewDTO> searchReviewsByKeyword(String keyword, int page, int size, String sort) {
        return reviewRepositoryCustom.getReviewsByKeyword(keyword, page, size, sort);
    }

    @Override
    @Cacheable(cacheNames = "views", key = "'top7Views'")
    public ReviewResponseDTO.Top7ReviewPreViewListDTO getTop7Reviews() {
        return reviewRepositoryCustom.getTop7Reviews();
    }

    @Override
    public ReviewResponseDTO.ReviewDetailDTO getReviewDetail(Long reviewId, ReviewType type) {
        if (type == ReviewType.EVENT) {
            EventReview eventReview = eventReviewRepository.findById(reviewId)
                    .orElseThrow(() -> new EventHandler(ErrorStatus.EVENT_REVIEW_NOT_FOUND));
            return ReviewConverter.toEventReviewDetailDTO(eventReview);
        } else if (type == ReviewType.PLACE) {
            PlaceReview placeReview = placeReviewRepository.findById(reviewId)
                    .orElseThrow(() -> new PlaceHandler(ErrorStatus.PLACE_REVIEW_NOT_FOUND));
            return ReviewConverter.toPlaceReviewDetailDTO(placeReview);
        } else {
            throw new ReviewHandler(ErrorStatus.INVALID_REVIEW_TYPE);
        }
    }

    @Override
    public ReviewResponseDTO.IsPurchasedReviewDTO getIsPurchasedReview(User user, Long reviewId, ReviewType type) {
        return ReviewConverter.toIsPurchasedReviewDTO(reviewRepositoryCustom.getIsPurchasedReview(user, reviewId, type));
    }
}
