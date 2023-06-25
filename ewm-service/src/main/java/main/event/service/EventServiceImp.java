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
import org.springframework.data.domain.Sort;
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
        Category category;
        if(eventInputDto.getCategory()==null){
            category = categoryRepository.findAll().get(0);
        }else{
            category = categoryRepository.findById(eventInputDto.getCategory()).orElseThrow(
                    ()-> new ObjectNotFoundException("Category not found")
            );
        }


        long hoursBefore = 2L;
        if(LocalDateTime.parse(eventInputDto.getEventDate(),
                        FormatConstants.DATE_TIME_FORMATTER).atZone(ZoneId.systemDefault())
                .isBefore(ZonedDateTime.now(ZoneId.systemDefault()).plusHours(hoursBefore))){
            throw new ValidationException("Cannot create event 2 hours before start");
        }
        Event event = eventRepository.save(makeEvent(eventInputDto,user,category));
        log.info("Event has been created {}",event);
        return eventMapper.convertToDto(event);
    }

    @Override
    public List<EventDto> getEvents(GetEventsParamsDto paramsDto) {

        BooleanExpression query = QEvent.event.initiator.isNotNull();
        if(paramsDto.getUsers()!=null && paramsDto.getUsers().size()!=0){
            query = query.and(QEvent.event.initiator.in(Set.copyOf(paramsDto.getUsers())));
        }
        if(paramsDto.getCategories()!=null && paramsDto.getCategories().size()!=0){
            query = query.and(QEvent.event.category.in(Set.copyOf(paramsDto.getCategories())));
        }
        if(paramsDto.getStates()!=null && paramsDto.getStates().size()!=0){
            query = query.and(QEvent.event.state.in(
                    paramsDto.getStates().stream().map(State::valueOf).collect(Collectors.toSet())));
        }
        if(paramsDto.getRangeStart()!=null && paramsDto.getRangeEnd()!=null){
            ZonedDateTime start = LocalDateTime.parse(paramsDto.getRangeStart(),FormatConstants.DATE_TIME_FORMATTER)
                    .atZone(ZoneId.systemDefault());
            ZonedDateTime end = LocalDateTime.parse(paramsDto.getRangeEnd(),FormatConstants.DATE_TIME_FORMATTER)
                    .atZone(ZoneId.systemDefault());
            query = query.and(QEvent.event.created.between(start,end));
        }

        Pageable page = new CustomPageRequest(paramsDto.getFrom(),paramsDto.getSize());
        Page<Event> eventPage = eventRepository.findAll(query,page);
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
        switch (eventUpdateDto.getAccess()){
            case ADMIN:
                updateEventAdmin(event,eventUpdateDto);
                break;
            case PRIVATE:
                updateEventPrivate(event,eventUpdateDto);
                break;
        }
        event=eventRepository.save(event);
        log.info("Event has been updated {}",event);
        return eventMapper.convertToDto(event);
    }

    @Override
    public EventDto getEventById(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(
                ()-> new ObjectNotFoundException("User not found")
        );
        Event event = eventRepository.findById(eventId).orElseThrow(
                ()-> new ObjectNotFoundException("Event not found")
        );
        if(!event.getInitiator().getId().equals(userId)){
            throw new ValidationException("User is not the initiator of the event");
        }
        log.info("Event has been returned {}",event);
        return eventMapper.convertToDto(event);
    }

    @Override
    public List<EventDto> getEventsPublic(GetEventsParamsDto paramsDto) {
        BooleanExpression query = QEvent.event.state.eq(State.PUBLISHED);
        if(paramsDto.getSearchText()!=null){
            query = query.and(QEvent.event.annotation.containsIgnoreCase(paramsDto.getSearchText())
                    .or(QEvent.event.description.containsIgnoreCase(paramsDto.getSearchText())));
        }
        if(paramsDto.getCategories()!=null && paramsDto.getCategories().size()!=0){
            query = query.and(QEvent.event.category.in(Set.copyOf(paramsDto.getCategories())));
        }
        if(paramsDto.getPaid()!=null){
            query = query.and(QEvent.event.paid.eq(paramsDto.getPaid()));
        }
        if(paramsDto.getRangeStart()!=null && paramsDto.getRangeEnd()!=null){
            ZonedDateTime start = LocalDateTime.parse(paramsDto.getRangeStart(),FormatConstants.DATE_TIME_FORMATTER)
                    .atZone(ZoneId.systemDefault());
            ZonedDateTime end = LocalDateTime.parse(paramsDto.getRangeEnd(),FormatConstants.DATE_TIME_FORMATTER)
                    .atZone(ZoneId.systemDefault());
            query = query.and(QEvent.event.created.between(start,end));
        }else{
            ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
            query = query.and(QEvent.event.created.after(now));
        }
        if(paramsDto.getOnlyAvailable()!=null && paramsDto.getOnlyAvailable()){
            query = query.and(QEvent.event.participantLimit.gt(QEvent.event.confirmedRequests));
        }

        Sort sort;
        switch (paramsDto.getSort()){
            case EVENT_DATE:
                sort = Sort.by(Sort.Direction.ASC,"created");
                break;
            default:
                sort = Sort.by(Sort.Direction.ASC,"views");
                break;
        }

        Pageable page = new CustomPageRequest(paramsDto.getFrom(),paramsDto.getSize(),sort);
        Page<Event> eventPage = eventRepository.findAll(query,page);
        List<EventDto> eventDtos = eventPage.getContent().stream()
                .map(eventMapper::convertToDto).collect(Collectors.toList());
        log.info("Page of events has been returned {}",eventDtos);
        return eventDtos;
    }

    private void updateEventAdmin(Event event, EventUpdateDto eventUpdateDto){
        long hoursBefore = 1L;
        if(event.getState().equals(State.PUBLISHED) && event.getPublished().isBefore(ZonedDateTime.now(ZoneId.systemDefault()).plusHours(hoursBefore))){
            throw new ValidationException("Cannot change event less then "+hoursBefore+" hour before event publishing");
        }
        updateEventParams(event, eventUpdateDto);
        updateEventStateAdmin(event, eventUpdateDto.getStateAction());
    }

    private void updateEventPrivate(Event event, EventUpdateDto eventUpdateDto){
        long hoursBefore = 2L;
        userRepository.findById(eventUpdateDto.getUserId()).orElseThrow(
                ()-> new ObjectNotFoundException("User not found")
        );
        if(!event.getInitiator().getId().equals(eventUpdateDto.getUserId())){
            throw new ValidationException("User is not the initiator of the event");
        }
        if(event.getPublished().isBefore(ZonedDateTime.now(ZoneId.systemDefault()).plusHours(hoursBefore))){
            throw new ValidationException("Cannot change event less then "+hoursBefore+" hour before event publishing");
        }
        if(!(event.getState().equals(State.WAITING) || event.getState().equals(State.CANCELED))){
            throw new ValidationException("Unable to patch event");
        }
        updateEventParams(event, eventUpdateDto);
        updateEventStatePrivate(event, eventUpdateDto.getStateAction());
    }

    private void updateEventParams(Event event, EventUpdateDto eventUpdateDto){
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
    }

    private void updateEventStateAdmin(Event event, State state){
        switch (state){
            case PUBLISH_EVENT:
                if(event.getState().equals(State.WAITING)){
                event.setState(State.PUBLISHED);
                }
                break;
            case CANCEL_REVIEW:
                event.setState(State.CANCELED);
                break;
        }
    }

    private void updateEventStatePrivate(Event event, State state){
        switch (state){
            case CANCEL_REVIEW:
                event.setState(State.CANCELED);
                break;
        }
    }

    private Event makeEvent(EventInputDto eventInputDto, User initiator, Category category){
        Event event = eventMapper.convertToCategory(eventInputDto);
        event.setConfirmedRequests(0);
        event.setCreated(ZonedDateTime.now(ZoneId.systemDefault()));
        event.setInitiator(initiator);
        event.setState(State.WAITING);
        event.setViews(0);
        event.setCategory(category);
        return event;
    }
}
