package com.otakumap.domain.event.service;

import com.otakumap.domain.event.converter.EventConverter;
import com.otakumap.domain.event.dto.EventResponseDTO;
import com.otakumap.domain.event.entity.Event;
import com.otakumap.domain.event.repository.EventRepository;
import com.otakumap.domain.search.repository.SearchRepositoryCustom;
import com.otakumap.global.apiPayload.code.status.ErrorStatus;
import com.otakumap.global.apiPayload.exception.handler.EventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventQueryServiceImpl implements EventQueryService{

    private final EventRepository eventRepository;
    private final SearchRepositoryCustom searchRepository;

    @Override
    public EventResponseDTO.EventDetailDTO getEventDetail(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventHandler(ErrorStatus.EVENT_NOT_FOUND));
        String animationName = event.getEventAnimationList().stream()
                .map(ea -> ea.getAnimation().getName())
                .collect(Collectors.joining(", "));

        return EventConverter.toEventDetailDTO(event, animationName);
    }

    @Override
    public EventResponseDTO.EventSearchResultDTO searchEventByKeyword(String keyword, Integer page, Integer size) {

        Page<EventResponseDTO.EventDTO> pagedEventDTOs = searchRepository.searchAllEventsByKeyword(keyword, PageRequest.of(page, size));

        return EventConverter.toEventSearchResultDTO(pagedEventDTOs);
    }
}
