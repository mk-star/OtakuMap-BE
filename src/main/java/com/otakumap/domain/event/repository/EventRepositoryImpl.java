package com.otakumap.domain.event.repository;

import com.otakumap.domain.event.converter.EventConverter;
import com.otakumap.domain.event.dto.EventResponseDTO;
import com.otakumap.domain.event.entity.Event;
import com.otakumap.domain.event.entity.QEvent;
import com.otakumap.domain.event.entity.enums.EventType;
import com.otakumap.domain.event.entity.enums.Genre;
import com.otakumap.domain.event_like.repository.EventLikeRepository;
import com.otakumap.domain.image.converter.ImageConverter;
import com.otakumap.domain.image.dto.ImageResponseDTO;
import com.otakumap.domain.user.entity.User;
import com.otakumap.global.apiPayload.code.status.ErrorStatus;
import com.otakumap.global.apiPayload.exception.handler.EventHandler;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventRepositoryImpl implements EventRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final EventLikeRepository eventLikeRepository;


    @Override
    public List<EventResponseDTO.EventWithLikeDTO> getPopularEvents(User user) {
        QEvent event = QEvent.event;

        long maxRandId = 100_000_000L;
        long randomValue = (long) (Math.random() * maxRandId);

        List<Event> events = queryFactory.selectFrom(event)
                .where(event.endDate.goe(LocalDate.now())
                        .and(event.startDate.loe(LocalDate.now()))
                        .and(event.randId.gt(randomValue)))
                .orderBy(event.randId.asc()) // 인덱스 활용
                .limit(8)
                .fetch();

        return events.stream()
                .map(eve -> {
                    Boolean isLiked = (user != null) ? eventLikeRepository.existsByUserAndEvent(user, eve) : false;
                    return EventConverter.toEventWithLikeDTO(eve, isLiked);
                }).collect(Collectors.toList());
    }

    @Override
    public ImageResponseDTO.ImageDTO getEventBanner() {
        QEvent event = QEvent.event;

        List<Event> targetEvents = queryFactory.selectFrom(event)
                .leftJoin(event.thumbnailImage).fetchJoin()
                .where(event.endDate.goe(LocalDate.now())
                        .and(event.startDate.loe(LocalDate.now()))
                        .and(event.thumbnailImage.isNotNull()))
                .fetch();

        if(targetEvents.isEmpty()) {
            return ImageResponseDTO.ImageDTO.builder()
                .fileUrl("default_banner_url")
                .build();
        } else if(targetEvents.size() > 1) {
            Collections.shuffle(targetEvents);
        }

        return ImageConverter.toImageDTO((targetEvents.get(0))
                .getThumbnailImage());
    }

    @Override
    public Page<EventResponseDTO.EventDTO> getEventByGenre(String genre, Integer page, Integer size) {
        QEvent event = QEvent.event;
        BooleanBuilder searchCondition = new BooleanBuilder();

        switch (genre) {
            case "ALL":
                break;
            case "ROMANCE":
                searchCondition.and(event.genre.eq(Genre.ROMANCE));
                break;
            case "ACTION":
                searchCondition.and(event.genre.eq(Genre.ACTION));
                break;
            case "FANTASY":
                searchCondition.and(event.genre.eq(Genre.FANTASY));
                break;
            case "THRILLER":
                searchCondition.and(event.genre.eq(Genre.THRILLER));
                break;
            case "SPORTS":
                searchCondition.and(event.genre.eq(Genre.SPORTS));
                break;
            default:
                throw new EventHandler(ErrorStatus.EVENT_GENRE_NOT_FOUND);
        }

        return paginateSearchEventResults(page, size, event, searchCondition);
    }

    @Override
    public Page<EventResponseDTO.EventDTO> getEventByStatusAndType(String status, String type, Integer page, Integer size) {
        QEvent event = QEvent.event;
        BooleanBuilder searchCondition = new BooleanBuilder();


        if(type != null && !type.isBlank()) {
            switch (type) {
                case "ALL":
                    break;
                case "POPUP_STORE":
                    searchCondition.and(event.type.eq(EventType.POPUP_STORE));
                    break;
                case "EXHIBITION":
                    searchCondition.and(event.type.eq(EventType.EXHIBITION));
                    break;
                case "COLLABORATION_CAFE":
                    searchCondition.and(event.type.eq(EventType.COLLABORATION_CAFE));
                    break;
                default:
                    throw new EventHandler(ErrorStatus.EVENT_TYPE_NOT_FOUND);
            }
        }

        if(status != null && !status.isBlank()) {
            switch (status) {
                case "IN_PROCESS":
                    searchCondition.and(event.startDate.loe(LocalDate.now()))
                            .and(event.endDate.goe(LocalDate.now()));
                    break;
                case "NOT_STARTED":
                    searchCondition.and(event.startDate.goe(LocalDate.now())
                            .and(event.endDate.goe(LocalDate.now())));
                    break;
                default:
                    throw new EventHandler(ErrorStatus.EVENT_STATUS_NOT_FOUND);
            }
        }

        return paginateSearchEventResults(page, size, event, searchCondition);
    }

    private Page<EventResponseDTO.EventDTO> paginateSearchEventResults(Integer page, Integer size, QEvent event, BooleanBuilder searchCondition) {
        List<Event> events = queryFactory.selectFrom(event)
                    .where(searchCondition)
                    .fetch();
        events.sort(Comparator.comparing(Event::getEndDate).reversed()
                .thenComparing(Event::getTitle));


        int start = page * size;
        int end = Math.min(start + size, events.size());
        if(start > end) {
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(page, size), events.size());
        }

        return new PageImpl<>(events.stream().map(EventConverter::toEventDTO).collect(Collectors.toList()), PageRequest.of(page, size), events.size());
    }
}
