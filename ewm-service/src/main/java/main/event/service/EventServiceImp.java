package main.event.service;

import client.StatClient;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import constants.FormatConstants;
import dto.HitInputDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.CustomPageRequest;
import main.access.Access;
import main.category.model.Category;
import main.category.repository.CategoryRepository;
import main.event.State;
import main.event.dto.*;
import main.event.mapper.EventMapper;
import main.event.model.Event;
import main.event.model.QEvent;
import main.event.repository.EventRepository;
import main.exceptions.ConflictException;
import main.exceptions.ObjectNotFoundException;
import main.exceptions.ValidationException;
import main.location.dto.LocationGetParamsDto;
import main.location.dto.LocationInputDto;
import main.location.model.Location;
import main.location.model.QLocation;
import main.location.repository.LocationRepository;
import main.location.service.LocationService;
import main.user.model.User;
import main.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventServiceImp implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;
    private final LocationService locationService;
    private final LocationRepository locationRepository;
    private final StatClient statClient;

    @Override
    @Transactional
    public EventDto createEvent(EventInputDto eventInputDto, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("User not found")
        );
        Category category;
        if (eventInputDto.getCategory() == null) {
            category = categoryRepository.findAll().get(0);
        } else {
            category = categoryRepository.findById(eventInputDto.getCategory()).orElseThrow(
                    () -> new ObjectNotFoundException("Category not found")
            );
        }
        long hoursBefore = 2L;
        if (LocalDateTime.parse(eventInputDto.getEventDate(),
                        FormatConstants.DATE_TIME_FORMATTER).atZone(ZoneId.systemDefault())
                .isBefore(ZonedDateTime.now(ZoneId.systemDefault()).plusHours(hoursBefore))) {
            throw new ValidationException("Cannot create event " + hoursBefore + " hours before start");
        }

        Event event = eventRepository.save(makeEvent(eventInputDto, user, category));
        log.info("Event has been created {}", event);
        return eventMapper.convertToDto(event);
    }

    /**
     * Получение списка событий по фильтрам: инициаторы, категории, состоянию публикации, временному интервалу,
     * параметрам локации: координаты и радиус поиска, название страны, города или локации.
     * @param paramsDto параметры поиска
     * @return список DTO событий, удовлетворяющих фильтрам
     */
    @Override
    public List<EventDto> getEvents(GetEventsParamsDto paramsDto) {
        paramsDto.validate();
        BooleanExpression query = QEvent.event.initiator.isNotNull();
        if (paramsDto.getUsers() != null && paramsDto.getUsers().size() != 0) {
            query = query.and(QEvent.event.initiator.in(Set.copyOf(paramsDto.getUsers())));
        }
        if (paramsDto.getCategories() != null && paramsDto.getCategories().size() != 0) {
            query = query.and(QEvent.event.category.in(Set.copyOf(paramsDto.getCategories())));
        }
        if (paramsDto.getStates() != null && paramsDto.getStates().size() != 0) {
            query = query.and(QEvent.event.state.in(
                    paramsDto.getStates().stream().map(State::valueOf).collect(Collectors.toSet())));
        }
        if (paramsDto.getRangeStart() != null && paramsDto.getRangeEnd() != null) {
            ZonedDateTime start = LocalDateTime.parse(paramsDto.getRangeStart(), FormatConstants.DATE_TIME_FORMATTER)
                    .atZone(ZoneId.systemDefault());
            ZonedDateTime end = LocalDateTime.parse(paramsDto.getRangeEnd(), FormatConstants.DATE_TIME_FORMATTER)
                    .atZone(ZoneId.systemDefault());
            query = query.and(QEvent.event.eventDate.between(start, end));
        }
        if (paramsDto.getLocationGetParamsDto() != null) {
            BooleanExpression locationQuery = locationQuery(paramsDto.getLocationGetParamsDto());
            if (!locationQuery.equals(QLocation.location.isNotNull())) {
                JPQLQuery<Long> subQuery = JPAExpressions.select(QLocation.location.id)
                        .from(QLocation.location)
                        .where(locationQuery);
                query = query.and(QEvent.event.location.in(subQuery));
            }
        }

        Pageable page = new CustomPageRequest(paramsDto.getFrom(), paramsDto.getSize());
        Page<Event> eventPage = eventRepository.findAll(query, page);
        List<EventDto> eventDtos = eventPage.getContent().stream()
                .map(eventMapper::convertToDto).collect(Collectors.toList());
        log.info("Page of events has been returned {}", eventDtos);
        return eventDtos;
    }


    private BooleanExpression locationQuery(LocationGetParamsDto dto) {

        BooleanExpression query = QLocation.location.isNotNull();

        if (dto.getLon() != null && dto.getLat() != null && dto.getRadius() != null) {
            Double toRads = Math.PI / 180.0;
            NumberPath<Double> latP = Expressions.numberPath(Double.class, String.valueOf(dto.getLat() * toRads));
            NumberPath<Double> lonP = Expressions.numberPath(Double.class, String.valueOf(dto.getLon() * toRads));
            NumberPath<Double> radP = Expressions.numberPath(Double.class, dto.getRadius().toString());
            NumberPath<Double> earthRadius = Expressions.numberPath(Double.class, String.valueOf(6372795.0));

            NumberExpression<Double> sin1Sin2 = MathExpressions.sin(latP)
                    .multiply(MathExpressions.sin(QLocation.location.lat.multiply(toRads)));
            NumberExpression<Double> cos1Cos2CosLon = MathExpressions.cos(latP)
                    .multiply(MathExpressions.cos(QLocation.location.lat.multiply(toRads)))
                    .multiply(MathExpressions.cos(
                            lonP.subtract(QLocation.location.lon.multiply(toRads)).abs()
                    ));
            NumberExpression<Double> distance = MathExpressions.acos(sin1Sin2.add(cos1Cos2CosLon)).multiply(earthRadius);
            query = query.and(QLocation.location.radius.add(radP).gt(distance));
        }

        if (dto.getCity() != null) {
            query = query.and(QLocation.location.city.containsIgnoreCase(dto.getCity()));
        }
        if (dto.getCountry() != null) {
            query = query.and(QLocation.location.country.containsIgnoreCase(dto.getCountry()));
        }
        if (dto.getPlace() != null) {
            query = query.and(QLocation.location.place.containsIgnoreCase(dto.getPlace()));
        }
        return query;
    }

    @Override
    @Transactional
    public EventDto updateEvent(Long eventId, EventUpdateDto eventUpdateDto) {

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException("Event not found")
        );
        switch (eventUpdateDto.getAccess()) {
            case ADMIN:
                updateEventAdmin(event, eventUpdateDto);
                break;
            case PRIVATE:
                updateEventPrivate(event, eventUpdateDto);
                break;
            default:
                throw new ObjectNotFoundException("Unknown access");
        }
        event = eventRepository.save(event);
        log.info("Event has been updated {}", event);
        return eventMapper.convertToDto(event);
    }


    @Override
    public EventDto getEventById(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("User not found")
        );
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException("Event not found")
        );
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ValidationException("User is not the initiator of the event");
        }
        log.info("Event has been returned {}", event);
        return eventMapper.convertToDto(event);
    }

    @Override
    @Transactional
    public EventDto getEventByIdPublic(Long eventId, HitInputDto hitInputDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new ObjectNotFoundException("Event not found")
        );
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ObjectNotFoundException("Event not found");
        }
        ResponseEntity<Object> response = statClient.checkHitsByIp(hitInputDto.getUri(), hitInputDto.getIp());
        if (!(Boolean) response.getBody()) {
            event.setViews(event.getViews() + 1);
            event = eventRepository.save(event);
        }
        statClient.addHit(hitInputDto);
        log.info("Event has been returned {}", event);
        return eventMapper.convertToDto(event);
    }

    /**
     * Получение списка событий по фильтрам: текст запроса в аннотации или описании, категории, платно или бесплатно,
     * временному интервалу, доступные, параметрам локации: координаты и радиус поиска, название страны, города или локации.
     * Передаются только опубликованные события.
     * @param paramsDto параметры поиска
     * @return список DTO событий, удовлетворяющих фильтрам
     */
    @Override
    public List<EventPublicDto> getEventsPublic(GetEventsParamsDto paramsDto, HitInputDto hitDto) {
        paramsDto.validate();
        BooleanExpression query = QEvent.event.state.eq(State.PUBLISHED);
        if (paramsDto.getSearchText() != null) {
            query = query.and(QEvent.event.annotation.containsIgnoreCase(paramsDto.getSearchText())
                    .or(QEvent.event.description.containsIgnoreCase(paramsDto.getSearchText())));
        }
        if (paramsDto.getCategories() != null && paramsDto.getCategories().size() != 0) {
            query = query.and(QEvent.event.category.in(Set.copyOf(paramsDto.getCategories())));
        }
        if (paramsDto.getPaid() != null) {
            query = query.and(QEvent.event.paid.eq(paramsDto.getPaid()));
        }
        if (paramsDto.getRangeStart() != null && paramsDto.getRangeEnd() != null) {
            ZonedDateTime start = LocalDateTime.parse(paramsDto.getRangeStart(), FormatConstants.DATE_TIME_FORMATTER)
                    .atZone(ZoneId.systemDefault());
            ZonedDateTime end = LocalDateTime.parse(paramsDto.getRangeEnd(), FormatConstants.DATE_TIME_FORMATTER)
                    .atZone(ZoneId.systemDefault());
            query = query.and(QEvent.event.eventDate.between(start, end));
        } else {
            ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
            query = query.and(QEvent.event.eventDate.after(now));
        }
        if (paramsDto.getOnlyAvailable() != null && paramsDto.getOnlyAvailable()) {
            query = query.and(QEvent.event.participantLimit.gt(QEvent.event.confirmedRequests));
        }
        if (paramsDto.getLocationGetParamsDto() != null) {
            BooleanExpression locationQuery = locationQuery(paramsDto.getLocationGetParamsDto());
            if (!locationQuery.equals(QLocation.location.isNotNull())) {
                JPQLQuery<Long> subQuery = JPAExpressions.select(QLocation.location.id)
                        .from(QLocation.location)
                        .where(locationQuery);
                query = query.and(QEvent.event.location.in(subQuery));
            }
        }

        Sort sort;
        switch (paramsDto.getSort()) {
            case EVENT_DATE:
                sort = Sort.by(Sort.Direction.ASC, "createdOn");
                break;
            default:
                sort = Sort.by(Sort.Direction.ASC, "views");
                break;
        }

        Pageable page = new CustomPageRequest(paramsDto.getFrom(), paramsDto.getSize(), sort);
        Page<Event> eventPage = eventRepository.findAll(query, page);
        List<EventPublicDto> eventDtos = eventPage.getContent().stream()
                .map(eventMapper::convertToPublicDto).collect(Collectors.toList());
        statClient.addHit(hitDto);
        log.info("Page of events has been returned {}", eventDtos);
        return eventDtos;
    }

    private void updateEventAdmin(Event event, EventUpdateDto eventUpdateDto) {
        long hoursBefore = 1L;
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Cannot change published event");
        }
        if (event.getState().equals(State.CANCELED)) {
            throw new ConflictException("Cannot change canceled event");
        }
        if (event.getEventDate().isBefore(ZonedDateTime.now(ZoneId.systemDefault()).plusHours(hoursBefore))) {
            throw new ValidationException("Cannot change event less then " + hoursBefore + " hour before event date");
        }
        updateEventParams(event, eventUpdateDto);
        if (eventUpdateDto.getStateAction() != null) {
            updateEventStateAdmin(event, eventUpdateDto.getStateAction());
        }
    }

    private void updateEventPrivate(Event event, EventUpdateDto eventUpdateDto) {
        long hoursBefore = 2L;
        User user = userRepository.findById(eventUpdateDto.getUserId()).orElseThrow(
                () -> new ObjectNotFoundException("User not found")
        );
        if (!event.getInitiator().getId().equals(eventUpdateDto.getUserId())) {
            throw new ValidationException("User is not the initiator of the event");
        }
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Cannot change published event");
        }
        if (event.getEventDate().isBefore(ZonedDateTime.now(ZoneId.systemDefault()).plusHours(hoursBefore))) {
            throw new ValidationException("Cannot change event less then " + hoursBefore + " hour before event date");
        }
        if (!(event.getState().equals(State.PENDING) || event.getState().equals(State.CANCELED))) {
            throw new ValidationException("Unable to patch event");
        }
        updateEventParams(event, eventUpdateDto);
        if (eventUpdateDto.getStateAction() != null) {
            updateEventStatePrivate(event, eventUpdateDto.getStateAction());
        }
    }

    private void updateEventParams(Event event, EventUpdateDto eventUpdateDto) {
        if (eventUpdateDto.getAnnotation() != null) {
            event.setAnnotation(eventUpdateDto.getAnnotation());
        }
        if (eventUpdateDto.getCategory() != null) {
            Category category = categoryRepository.findById(eventUpdateDto.getCategory()).orElseThrow(
                    () -> new ObjectNotFoundException("Category not found")
            );
            event.setCategory(category);
        }
        if (eventUpdateDto.getDescription() != null) {
            event.setDescription(eventUpdateDto.getDescription());
        }
        if (eventUpdateDto.getEventDate() != null) {
            ZonedDateTime date = LocalDateTime.parse(eventUpdateDto.getEventDate(), FormatConstants.DATE_TIME_FORMATTER)
                    .atZone(ZoneId.systemDefault());
            event.setEventDate(date);
        }
        if (eventUpdateDto.getLocation() != null) {
            updateEventLocation(event, eventUpdateDto);
        }
        if (eventUpdateDto.getPaid() != null) {
            event.setPaid(eventUpdateDto.getPaid());
        }
        if (eventUpdateDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateDto.getParticipantLimit());
        }
        if (eventUpdateDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateDto.getRequestModeration());
        }
        if (eventUpdateDto.getTitle() != null) {
            event.setTitle(eventUpdateDto.getTitle());
        }
    }

    private void updateEventLocation(Event event, EventUpdateDto eventUpdateDto) {

        Location location;
        switch (eventUpdateDto.getAccess()) {
            case ADMIN:
                location = updateEventLocationAdmin(event, eventUpdateDto);
                break;
            case PRIVATE:
                location = updateEventLocationPrivate(event, eventUpdateDto);
                break;
            default:
                throw new ObjectNotFoundException("Unknown access");
        }
        event.setLocation(location);

    }

    /**
     * Обновляет локацию события. Если передан идентификатор другой локации, в событии заменится локация на существующую.
     * Если локация уже используется, то создастся новая локация и запишется в событие.
     * Если локация не используется другими событиями, параметры текущей локации изменятся.
     * @param event событие, локация которого обновляется
     * @param eventUpdateDto параметры обновления
     * @return объект обновленной локации
     */
    private Location updateEventLocationAdmin(Event event, EventUpdateDto eventUpdateDto) {

        if (eventUpdateDto.getLocation().getId() != null &&
                !eventUpdateDto.getLocation().getId().equals(event.getLocation().getId())) {
            return switchLocation(eventUpdateDto.getLocation().getId());
        }

        Long usages = eventRepository.countLocationUsages(event.getLocation().getId(), event.getId()).getCountId();
        if (usages == 0) {
            eventUpdateDto.getLocation().setId(event.getLocation().getId());
            return locationService.updateLocation(eventUpdateDto.getLocation());
        }

        Location eventLocation = event.getLocation();
        LocationInputDto locationInputDto = eventUpdateDto.getLocation();
        if (locationInputDto.getLat() == null) {
            locationInputDto.setLat(eventLocation.getLat());
        }
        if (locationInputDto.getLon() == null) {
            locationInputDto.setLon(eventLocation.getLon());
        }
        if (locationInputDto.getPlace() == null) {
            locationInputDto.setPlace(eventLocation.getPlace());
        }
        if (locationInputDto.getRadius() == null) {
            locationInputDto.setRadius(eventLocation.getRadius());
        }
        return locationService.addLocationAdmin(locationInputDto);


    }

    /**
     * Обновляет локацию события. Если передан идентификатор другой локации, в событии заменится локация на существующую.
     * Если пользователь не создатель локации или локация уже используется, то создастся новая локация и запишется в событие.
     * Если пользователь является создателем локации и локация не используется другими событиями, параметры текущей локации изменятся.
     * @param event событие, локация которого обновляется
     * @param eventUpdateDto параметры обновления
     * @return объект обновленной локации
     */
    private Location updateEventLocationPrivate(Event event, EventUpdateDto eventUpdateDto) {

        if (eventUpdateDto.getLocation().getId() != null &&
                !eventUpdateDto.getLocation().getId().equals(event.getLocation().getId())) {
            return switchLocation(event.getInitiator().getId(), eventUpdateDto.getLocation().getId());
        }

        Long usages = eventRepository.countLocationUsages(event.getLocation().getId(), event.getId()).getCountId();
        boolean isCreator = event.getLocation().getAccess().equals(Access.PRIVATE) &&
                event.getLocation().getCreator().getId().equals(eventUpdateDto.getUserId());
        if (isCreator && usages == 0) {
            eventUpdateDto.getLocation().setId(event.getLocation().getId());
            return locationService.updateLocation(eventUpdateDto.getLocation());
        }

        Location eventLocation = event.getLocation();
        LocationInputDto locationInputDto = eventUpdateDto.getLocation();
        if (locationInputDto.getLat() == null) {
            locationInputDto.setLat(eventLocation.getLat());
        }
        if (locationInputDto.getLon() == null) {
            locationInputDto.setLon(eventLocation.getLon());
        }
        if (locationInputDto.getPlace() == null) {
            locationInputDto.setPlace(eventLocation.getPlace());
        }
        if (locationInputDto.getRadius() == null) {
            locationInputDto.setRadius(eventLocation.getRadius());
        }
        return locationService.addLocationPrivate(eventUpdateDto.getLocation(), event.getInitiator());
    }

    public Location switchLocation(Long locationId) {
        return switchLocation(-1L, locationId);
    }


    /**
     * Возвращает объект локации при смене лоции по идентификатору
     * @param userId
     * @param locationId
     * @return объект найденной локации
     * @throws ObjectNotFoundException локация не найдена по идентификатору, или недоступна для пользователя
     */
    public Location switchLocation(Long userId, Long locationId) {
        Location location = locationRepository.findById(locationId).orElseThrow(
                () -> new ObjectNotFoundException("Location not found")
        );
        if (eventRepository.countStateLocationUsages(locationId, State.PUBLISHED.toString(),
                userId).getCountId() == 0 && location.getAccess().equals(Access.PRIVATE)) {
            throw new ObjectNotFoundException("Location not found");
        }
        return location;
    }


    private void updateEventStateAdmin(Event event, State state) {
        switch (state) {
            case PUBLISH_EVENT:
                if (event.getState().equals(State.PENDING)) {
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(ZonedDateTime.now(ZoneId.systemDefault()));
                }
                break;
            case REJECT_EVENT:
                event.setState(State.CANCELED);
                break;
            default:
                throw new ObjectNotFoundException("Unknown action");
        }
    }

    private void updateEventStatePrivate(Event event, State state) {
        switch (state) {
            case SEND_TO_REVIEW:
                event.setState(State.PENDING);
                break;
            case CANCEL_REVIEW:
                event.setState(State.CANCELED);
                break;
            default:
                throw new ObjectNotFoundException("Unknown action");
        }
    }

    /**
     * Создание события по параметрам. Локация события может быть задана параметрами или по идентификатору существующей
     * локации. При добавлении локации по идентификатору доступны только опубликованные локации и локации администратора,
     * для пользователя доступны также все его локации.
     * @param eventInputDto параметры для создания события
     * @param initiator пользователь, инициирующий событие
     * @param category категория события
     * @return объект события
     */
    private Event makeEvent(EventInputDto eventInputDto, User initiator, Category category) {
        Event event = eventMapper.convertToEvent(eventInputDto);
        event.setConfirmedRequests(0);
        event.setCreatedOn(ZonedDateTime.now(ZoneId.systemDefault()));
        event.setInitiator(initiator);
        event.setState(State.PENDING);
        event.setViews(0);
        event.setCategory(category);
        Location location;
        if (eventInputDto.getLocation().getId() != null) {
            location = switchLocation(eventInputDto.getLocation().getId());
        } else {
            location = locationService.addLocationPrivate(eventInputDto.getLocation(), initiator);
        }
        event.setLocation(location);
        return event;
    }


}
