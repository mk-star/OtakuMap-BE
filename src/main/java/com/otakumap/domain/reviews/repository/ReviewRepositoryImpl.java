package com.otakumap.domain.reviews.repository;

import com.otakumap.domain.animation.entity.QAnimation;
import com.otakumap.domain.event.entity.QEvent;
import com.otakumap.domain.event_review.entity.EventReview;
import com.otakumap.domain.event_review.entity.QEventReview;
import com.otakumap.domain.event_review.repository.EventReviewRepository;
import com.otakumap.domain.image.entity.QImage;
import com.otakumap.domain.mapping.QEventAnimation;
import com.otakumap.domain.mapping.QPlaceAnimation;
import com.otakumap.domain.mapping.QPlaceReviewPlace;
import com.otakumap.domain.payment.enums.PaymentStatus;
import com.otakumap.domain.place.entity.QPlace;
import com.otakumap.domain.place_review.entity.PlaceReview;
import com.otakumap.domain.place_review.entity.QPlaceReview;
import com.otakumap.domain.place_review.repository.PlaceReviewRepository;
import com.otakumap.domain.point.entity.Point;
import com.otakumap.domain.point.repository.PointRepository;
import com.otakumap.domain.reviews.converter.ReviewConverter;
import com.otakumap.domain.reviews.dto.ReviewResponseDTO;
import com.otakumap.domain.reviews.enums.ReviewType;
import com.otakumap.domain.transaction.entity.Transaction;
import com.otakumap.domain.transaction.enums.TransactionType;
import com.otakumap.domain.transaction.repository.TransactionRepository;
import com.otakumap.domain.user.entity.User;
import com.otakumap.domain.user.repository.UserRepository;
import com.otakumap.global.apiPayload.code.status.ErrorStatus;
import com.otakumap.global.apiPayload.exception.handler.ReviewHandler;
import com.otakumap.global.apiPayload.exception.handler.SearchHandler;
import com.otakumap.global.apiPayload.exception.handler.TransactionHandler;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EventReviewRepository eventReviewRepository;
    private final PointRepository pointRepository;
    private final PlaceReviewRepository placeReviewRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Override
    public Page<ReviewResponseDTO.SearchedReviewPreViewDTO> getReviewsByKeyword(String keyword, int page, int size, String sort) {
        QEventReview eventReview = QEventReview.eventReview;
        QPlaceReview placeReview = QPlaceReview.placeReview;
        QEventAnimation eventAnimation = QEventAnimation.eventAnimation;
        QPlaceAnimation placeAnimation = QPlaceAnimation.placeAnimation;
        QAnimation animation = QAnimation.animation;
        QPlaceReviewPlace placeReviewPlace = QPlaceReviewPlace.placeReviewPlace;

        // 이벤트 리뷰 검색 : EventReview 제목, 내용, 또는 연관된 애니메이션 이름
        BooleanBuilder eventCondition = createSearchCondition(eventReview.title, eventReview.content, eventAnimation.animation.name, keyword);

        // 장소 리뷰 검색 : PlaceReview 제목, 내용, 또는 연관된 애니메이션 이름
        BooleanBuilder placeCondition = createSearchCondition(placeReview.title, placeReview.content, placeAnimation.animation.name, keyword);

        List<EventReview> eventReviews = queryFactory.selectFrom(eventReview)
                .leftJoin(eventReview.event, QEvent.event)
                .leftJoin(QEvent.event.eventAnimationList, eventAnimation)
                .leftJoin(eventAnimation.animation, animation)
                .where(eventCondition)
                .fetch();

        List<PlaceReview> placeReviews = queryFactory.selectFrom(placeReview)
                .leftJoin(placeReviewPlace).on(placeReviewPlace.placeReview.eq(placeReview))
                .leftJoin(placeReviewPlace.place, QPlace.place)
                .leftJoin(QPlace.place.placeAnimationList, placeAnimation)
                .leftJoin(placeAnimation.animation, animation)
                .where(placeCondition)
                .fetch();

        List<ReviewResponseDTO.SearchedReviewPreViewDTO> searchedReviews = new ArrayList<>();

        for(EventReview review : eventReviews) {
            searchedReviews.add(ReviewConverter.toSearchedEventReviewPreviewDTO(review));
        }

        for(PlaceReview review : placeReviews) {
            searchedReviews.add(ReviewConverter.toSearchedPlaceReviewPreviewDTO(review));
        }

        if (searchedReviews.isEmpty()) {
            throw new SearchHandler(ErrorStatus.REVIEW_SEARCH_NOT_FOUND);
        }

        // 정렬 (최신순, 조회수)
        sortReviews(searchedReviews, sort);

        return paginateReviews(searchedReviews, page, size);
    }

    private BooleanBuilder createSearchCondition(StringPath title, StringPath content, StringPath animationName, String keyword) {
        BooleanBuilder condition = new BooleanBuilder();

        condition.or(title.containsIgnoreCase(keyword))
                .or(content.containsIgnoreCase(keyword))
                .or(animationName.containsIgnoreCase(keyword));

        return condition;
    }

    private void sortReviews(List<ReviewResponseDTO.SearchedReviewPreViewDTO> reviews, String sort) {
        if ("views".equalsIgnoreCase(sort)) {
            reviews.sort(Comparator.comparing(ReviewResponseDTO.SearchedReviewPreViewDTO::getView).reversed()
                    .thenComparing(ReviewResponseDTO.SearchedReviewPreViewDTO::getCreatedAt)); // 조회수가 같으면 최신순을 기준으로
        } else {
            reviews.sort(Comparator.comparing(ReviewResponseDTO.SearchedReviewPreViewDTO::getCreatedAt).reversed());
        }
    }

    private Page<ReviewResponseDTO.SearchedReviewPreViewDTO> paginateReviews(
            List<ReviewResponseDTO.SearchedReviewPreViewDTO> reviews, int page, int size) {
        int start = page * size;
        int end = Math.min(start + size, reviews.size());
        if (start > end) {
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(page, size), reviews.size());
        }

        return new PageImpl<>(reviews.subList(start, end), PageRequest.of(page, size), reviews.size());
    }

    @Override
    public ReviewResponseDTO.Top7ReviewPreViewListDTO getTop7Reviews() {
        QPlaceReview placeReview = QPlaceReview.placeReview;
        QEventReview eventReview = QEventReview.eventReview;

        List<PlaceReview> placeReviews = queryFactory
                .selectFrom(placeReview)
                .leftJoin(placeReview.images, QImage.image).fetchJoin()
                .orderBy(placeReview.view.desc())
                .limit(7)
                .fetch();

        List<EventReview> eventReviews = queryFactory
                .selectFrom(eventReview)
                .leftJoin(eventReview.images, QImage.image).fetchJoin()
                .orderBy(eventReview.view.desc())
                .limit(7)
                .fetch();

        List<ReviewResponseDTO.Top7ReviewPreViewDTO> top7Reviews = Stream.concat(
                placeReviews.stream()
                        .map(ReviewConverter::toTop7PlaceReviewPreViewDTO),
                eventReviews.stream()
                        .map(ReviewConverter::toTop7EventReviewPreViewDTO)
        )
                .sorted(Comparator.comparing(ReviewResponseDTO.Top7ReviewPreViewDTO::getView, Comparator.reverseOrder())
                        .thenComparing(ReviewResponseDTO.Top7ReviewPreViewDTO::getCreatedAt, Comparator.reverseOrder()))
                .limit(7)
                .collect(Collectors.toList());

        return ReviewConverter.top7ReviewPreViewListDTO(top7Reviews);
    }

    @Override
    public ReviewResponseDTO.PurchaseReviewDTO purchaseReview(User user, Long reviewId, ReviewType type) {
        Point buyerPoint = pointRepository.findTopByUserOrderByChargedAtDesc(user);
        // 이벤트 리뷰인 경우
        if(type == ReviewType.EVENT) {
            EventReview review = eventReviewRepository.findById(reviewId)
                    .orElseThrow(() -> new ReviewHandler(ErrorStatus.EVENT_REVIEW_NOT_FOUND));
            if (transactionRepository.existsByPoint_UserAndEventReview(user, review)) {
                throw new TransactionHandler(ErrorStatus.PURCHASE_ALREADY_EXISTS);
            }

            return processReviewPurchase(user, buyerPoint, review, review.getPrice(),
                    eventReviewRepository.findUserById(reviewId), true);
        }
        // 장소 리뷰인 경우
        PlaceReview review = placeReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewHandler(ErrorStatus.PLACE_REVIEW_NOT_FOUND));
        if (transactionRepository.existsByPoint_UserAndPlaceReview(user, review)) {
            throw new TransactionHandler(ErrorStatus.PURCHASE_ALREADY_EXISTS);
        }

        return processReviewPurchase(user, buyerPoint, review, review.getPrice(),
                placeReviewRepository.findUserById(reviewId), false);
    }

    private ReviewResponseDTO.PurchaseReviewDTO processReviewPurchase(
            User user, Point buyerPoint, Object review, Long price, User seller, boolean isEventReview) {
        // 무료 글인 경우
        if(price == 0L) {
            throw new TransactionHandler(ErrorStatus.PURCHASE_FREE_CONTENT);
        }
        // 포인트 부족 확인
        if (!user.isAffordable(price)) {
            throw new TransactionHandler(ErrorStatus.PURCHASE_INSUFFICIENT_POINTS);
        }
        // 글쓴이와 구매자가 다른지 확인
        if (Objects.equals(user.getId(), seller.getId())) {
            throw new TransactionHandler(ErrorStatus.PURCHASE_SELF_CONTENT);
        }

        Point sellerPoint = pointRepository.findTopByUserOrderByChargedAtDesc(seller);
        if (sellerPoint == null) {
            sellerPoint = new Point(0L, LocalDateTime.now(), PaymentStatus.PAID, seller);
        }

        // 포인트 수정 후 업데이트
        seller.addPoint(price);
        Long remainingPoints = user.subPoint(price);
        userRepository.save(seller);
        userRepository.save(user);

        int priceInt = Math.toIntExact(price);
        // 거래 내역 저장(사용한 것과 번 것)
        if (isEventReview) {
            transactionRepository.save(new Transaction(buyerPoint, TransactionType.USAGE, priceInt, (EventReview) review, null));
            transactionRepository.save(new Transaction(sellerPoint, TransactionType.EARNING, priceInt, (EventReview) review, null));
        } else {
            transactionRepository.save(new Transaction(buyerPoint, TransactionType.USAGE, priceInt, null, (PlaceReview) review));
            transactionRepository.save(new Transaction(sellerPoint, TransactionType.EARNING, priceInt, null, (PlaceReview) review));
        }
        // 판매자 후원금 내역에 판매 금액만큼 추가하여 저장
        seller.addEarnings(priceInt);
        userRepository.save(seller);
        return ReviewResponseDTO.PurchaseReviewDTO.builder()
                .remainingPoints(remainingPoints)
                .build();
    }

    @Override
    public Boolean getIsPurchasedReview(User user, Long reviewId, ReviewType type) {
        if(type == ReviewType.EVENT) {
            return transactionRepository.existsByPoint_UserAndEventReview(user, eventReviewRepository.getById(reviewId));
        } else {
            return transactionRepository.existsByPoint_UserAndPlaceReview(user, placeReviewRepository.getById(reviewId));
        }
    }
}
