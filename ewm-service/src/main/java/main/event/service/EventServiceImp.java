package main.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import constants.FormatConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.CustomPageRequest;
import main.category.model.Category;
import main.category.repository.CategoryRepository;
import main.event.QEvent;
import main.event.State;
import main.event.dto.EventDto;
import main.event.dto.EventInputDto;
import main.event.dto.EventUpdateDto;
import main.event.dto.GetEventsParamsDto;
import main.event.mapper.EventMapper;
import main.event.model.Event;
import main.event.repository.EventRepository;
import main.exceptions.ObjectNotFoundException;
import main.exceptions.ValidationException;
import main.user.model.User;
import main.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImp implements EventService{

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    @Override
    @Transactional
    public EventDto createEvent(EventInputDto eventInputDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new ObjectNotFoundException("User not found")
        );
        Category category = categoryRepository.findById(eventInputDto.getCategory()).orElseThrow(
                ()-> new ObjectNotFoundException("Category not found")
        );
        Event event = eventRepository.save(makeEvent(eventInputDto,user,category));
        log.info("Event has been created {}",event);
        return eventMapper.convertToDto(event);
    }

    @Override
    public List<EventDto> getEvents(GetEventsParamsDto paramsDto) {

        BooleanExpression users = QEvent.event.users.in(Set.copyOf(paramsDto.getUsers()));

        BooleanExpression categories = QEvent.event.categories.in(Set.copyOf(paramsDto.getCategories()));

        BooleanExpression states = QEvent.event.states.in(Set.copyOf(paramsDto.getStates()));

        Timestamp start = Timestamp.valueOf(LocalDateTime.parse(
                paramsDto.getRangeStart(), FormatConstants.DATE_TIME_FORMATTER));
        Timestamp end = Timestamp.valueOf(LocalDateTime.parse(
                paramsDto.getRangeEnd(), FormatConstants.DATE_TIME_FORMATTER));
        BooleanExpression created = QEvent.event.created.between(start,end);

        Pageable page = new CustomPageRequest(paramsDto.getFrom(),paramsDto.getSize());
        Page<Event> eventPage = eventRepository.findAll(users.and(categories).and(states).and(created),page);
        List<EventDto> eventDtos = eventPage.getContent().stream()
                .map(eventMapper::convertToDto).collect(Collectors.toList());
        log.info("Page of events has been returned {}",eventDtos);
        return eventDtos;
    }

    @Override
    public EventDto updateEvent(Long eventId, EventUpdateDto eventUpdateDto) {

        Event event = eventRepository.findById(eventId).orElseThrow(
                ()-> new ObjectNotFoundException("Event not found")
        );
        long hoursBefore = 1L;
        if(event.getPublished().isBefore(ZonedDateTime.now(ZoneId.systemDefault()).plusHours(hoursBefore))){
            throw new ValidationException("Cannot change event less then "+hoursBefore+" hour before event publishing");
        }
        updateEvent(event,eventUpdateDto);
        event=eventRepository.save(event);
        log.info("Event has been updated {}",event);
        return eventMapper.convertToDto(event);
    }

    private void updateEvent(Event event, EventUpdateDto eventUpdateDto){
        if(eventUpdateDto.getAnnotation()!=null){
            event.setAnnotation(eventUpdateDto.getAnnotation());
        }
        if(eventUpdateDto.getCategory()!=null){
            Category category = categoryRepository.findById(eventUpdateDto.getCategory()).orElseThrow(
                    ()-> new ObjectNotFoundException("Category not found")
            );
            event.setCategory(category);
        }
        if(eventUpdateDto.getDescription()!=null){
            event.setDescription(eventUpdateDto.getDescription());
        }
        if(eventUpdateDto.getEventDate()!=null){
            ZonedDateTime date = ZonedDateTime.parse(eventUpdateDto.getEventDate(),FormatConstants.DATE_TIME_FORMATTER);
            event.setEventDate(date);
        }
        if(eventUpdateDto.getLocation()!=null){
            event.setLocation(eventUpdateDto.getLocation());
        }
        if(eventUpdateDto.getPaid()!=null){
            event.setPaid(eventUpdateDto.getPaid());
        }
        if(eventUpdateDto.getParticipantLimit()!=null){
            event.setParticipantLimit(eventUpdateDto.getParticipantLimit());
        }
        if(eventUpdateDto.getRequestModeration()!=null){
            event.setRequestModeration(eventUpdateDto.getRequestModeration());
        }
        if(eventUpdateDto.getTitle()!=null){
            event.setTitle(eventUpdateDto.getTitle());
        }
        if(eventUpdateDto.getStateAction().equals(State.PUBLISH_EVENT) && event.getState().equals(State.WAITING)){
            event.setState(State.PUBLISHED);
        }
    };

    private Event makeEvent(EventInputDto eventInputDto, User initiator, Category category){
        Event event = eventMapper.convertToCategory(eventInputDto);
        event.setConfirmedRequests(0);
        event.setCreated(ZonedDateTime.now(ZoneId.systemDefault()));
        event.setInitiator(initiator);
        event.setState(State.PUBLISHED);
        event.setViews(0);
        event.setCategory(category);
        return event;
    }
}
