package com.daengdaeng_eodiga.project.banner.service;

import com.daengdaeng_eodiga.project.banner.dto.BannersDto;
import com.daengdaeng_eodiga.project.event.entity.Event;
import com.daengdaeng_eodiga.project.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerService {

    private final EventRepository eventRepository;

	/**
	 * 배너 목록 조회
	 *
	 * DB에서 유효한 날짜에 해당하는 행사의 배너 목록을 조회
	 *
	 * @author 하진서
	 * @return List<BannersDto> : 배너 관련 정보
	 * */    
    public List<BannersDto> fetchBanners() {
        List<Event> activeEvents = eventRepository.findActiveEvents(LocalDate.now());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return activeEvents.stream()
                .map(event -> BannersDto.builder()
                                .eventId(event.getEventId())
                                .eventName(event.getEventName())
                                .eventImage(event.getEventImage())
                                .eventDescription(event.getEventDescription())
                                .placeName(event.getPlaceName())
                                .placeAddress(event.getPlaceAddress())
                                .startDate(event.getStartDate().format(formatter))
                                .endDate(event.getEndDate().format(formatter))
                                .build())
                .collect(Collectors.toList());
    }
}