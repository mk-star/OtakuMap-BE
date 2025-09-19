package com.otakumap.domain.place_review.service;

import com.otakumap.domain.place_review.repository.PlaceReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceReviewCommandServiceImpl implements PlaceReviewCommandService {
    private final PlaceReviewRepository placeReviewRepository;

    @Override
    @Transactional
    public void deleteAllByUserId(Long userId) {
        placeReviewRepository.deleteAllByUserId(userId);
    }
}
