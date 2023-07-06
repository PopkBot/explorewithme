package main.event.service;


import constants.FormatConstants;
import main.EwmApp;
import main.access.Access;
import main.category.dto.CategoryDto;
import main.category.dto.CategoryInputDto;
import main.category.service.CategoryService;
import main.event.SortType;
import main.event.State;
import main.event.dto.EventDto;
import main.event.dto.EventInputDto;
import main.event.dto.EventUpdateDto;
import main.event.dto.GetEventsParamsDto;
import main.location.model.Location;
import main.event.service.container.config.ContainersEnvironment;
import main.exceptions.ConflictException;
import main.exceptions.ObjectNotFoundException;
import main.exceptions.ValidationException;
import main.user.dto.UserDto;
import main.user.dto.UserInputDto;
import main.user.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Transactional
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EwmApp.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class EventServiceImpTest extends ContainersEnvironment {

    @Autowired
    private EventService eventService;
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
/*
    @Test
    void testCreateEvent() {

        UserInputDto userInputDto = UserInputDto.builder()
                .name("name")
                .email("e@mail.c")
                .build();
        UserDto userDto = userService.createUser(userInputDto);

        CategoryInputDto categoryInputDto = CategoryInputDto.builder()
                .name("cat")
                .build();
        CategoryDto categoryDto = categoryService.createCategory(categoryInputDto);

        EventInputDto eventInputDto = EventInputDto.builder()
                .eventDate(LocalDateTime.now().plusHours(10).format(FormatConstants.DATE_TIME_FORMATTER))
                .title("event")
                .paid(true)
                .location(
                        Location.builder()
                                .lat(1.0)
                                .lon(2.0)
                                .build())
                .description("descr")
                .annotation("ann")
                .requestModeration(false)
                .participantLimit(1)
                .category(categoryDto.getId())
                .build();

        ObjectNotFoundException oe = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> eventService.createEvent(eventInputDto, -1L));
        Assertions.assertEquals("User not found", oe.getMessage());

        eventInputDto.setCategory(-1L);

        oe = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> eventService.createEvent(eventInputDto, userDto.getId()));
        Assertions.assertEquals("Category not found", oe.getMessage());


        eventInputDto.setEventDate(LocalDateTime.now().plusMinutes(10).format(FormatConstants.DATE_TIME_FORMATTER));
        eventInputDto.setCategory(categoryDto.getId());

        ValidationException ve = Assertions.assertThrows(ValidationException.class,
                () -> eventService.createEvent(eventInputDto, userDto.getId()));
        Assertions.assertEquals("Cannot create event 2 hours before start", ve.getMessage());

        eventInputDto.setEventDate(LocalDateTime.now().plusHours(10).format(FormatConstants.DATE_TIME_FORMATTER));
        EventDto eventDto = eventService.createEvent(eventInputDto, userDto.getId());
        Assertions.assertEquals(userDto.getId(), eventDto.getInitiator().getId());
    }

    @Test
    void testGetEventsNoQueries() {

        GetEventsParamsDto paramsDto = GetEventsParamsDto.builder()
                .from(0)
                .size(10)
                .build();
        List<EventDto> eventDtoList = eventService.getEvents(paramsDto);
        Assertions.assertEquals(0, eventDtoList.size());

        UserInputDto userInputDto1 = UserInputDto.builder()
                .name("name")
                .email("e1@mail.c")
                .build();
        UserInputDto userInputDto2 = UserInputDto.builder()
                .name("name")
                .email("e2@mail.c")
                .build();
        UserInputDto userInputDto3 = UserInputDto.builder()
                .name("name")
                .email("e3@mail.c")
                .build();
        UserDto userDto1 = userService.createUser(userInputDto1);
        UserDto userDto2 = userService.createUser(userInputDto2);
        UserDto userDto3 = userService.createUser(userInputDto3);

        CategoryInputDto categoryInputDto = CategoryInputDto.builder()
                .name("cat")
                .build();
        CategoryDto categoryDto = categoryService.createCategory(categoryInputDto);

        EventInputDto eventInputDto1 = EventInputDto.builder()
                .eventDate(LocalDateTime.now().plusHours(10).format(FormatConstants.DATE_TIME_FORMATTER))
                .title("event1")
                .paid(true)
                .location(
                        Location.builder()
                                .lat(1.0f)
                                .lon(2.0f)
                                .build())
                .description("descr")
                .annotation("ann")
                .requestModeration(false)
                .participantLimit(1)
                .category(categoryDto.getId())
                .build();
        EventInputDto eventInputDto2 = EventInputDto.builder()
                .eventDate(LocalDateTime.now().plusHours(20).format(FormatConstants.DATE_TIME_FORMATTER))
                .title("event2")
                .paid(true)
                .location(
                        Location.builder()
                                .lat(1.0f)
                                .lon(2.0f)
                                .build())
                .description("descr")
                .annotation("ann")
                .requestModeration(false)
                .participantLimit(1)
                .category(categoryDto.getId())
                .build();
        EventInputDto eventInputDto3 = EventInputDto.builder()
                .eventDate(LocalDateTime.now().plusHours(30).format(FormatConstants.DATE_TIME_FORMATTER))
                .title("event3")
                .paid(true)
                .location(
                        Location.builder()
                                .lat(1.0f)
                                .lon(2.0f)
                                .build())
                .description("descr")
                .annotation("ann")
                .requestModeration(false)
                .participantLimit(1)
                .category(categoryDto.getId())
                .build();
        EventDto eventDto1 = eventService.createEvent(eventInputDto1, userDto1.getId());
        EventDto eventDto2 = eventService.createEvent(eventInputDto2, userDto2.getId());
        EventDto eventDto3 = eventService.createEvent(eventInputDto3, userDto3.getId());

        Assertions.assertEquals(userDto1.getId(), eventDto1.getInitiator().getId());
        Assertions.assertEquals(userDto2.getId(), eventDto2.getInitiator().getId());
        Assertions.assertEquals(userDto3.getId(), eventDto3.getInitiator().getId());

        eventDtoList = eventService.getEvents(paramsDto);
        Assertions.assertEquals(3, eventDtoList.size());
    }

    @Test
    void testGetEventsDifferentQueries() {

        UserInputDto userInputDto1 = UserInputDto.builder()
                .name("name")
                .email("e1@mail.c")
                .build();
        UserInputDto userInputDto2 = UserInputDto.builder()
                .name("name")
                .email("e2@mail.c")
                .build();
        UserInputDto userInputDto3 = UserInputDto.builder()
                .name("name")
                .email("e3@mail.c")
                .build();
        UserDto userDto1 = userService.createUser(userInputDto1);
        UserDto userDto2 = userService.createUser(userInputDto2);
        UserDto userDto3 = userService.createUser(userInputDto3);

        CategoryInputDto categoryInputDto1 = CategoryInputDto.builder()
                .name("cat1")
                .build();
        CategoryInputDto categoryInputDto2 = CategoryInputDto.builder()
                .name("cat2")
                .build();
        CategoryDto categoryDto1 = categoryService.createCategory(categoryInputDto1);
        CategoryDto categoryDto2 = categoryService.createCategory(categoryInputDto2);

        EventInputDto eventInputDto1 = EventInputDto.builder()
                .eventDate(LocalDateTime.now().plusHours(10).format(FormatConstants.DATE_TIME_FORMATTER))
                .title("event1")
                .paid(true)
                .location(
                        Location.builder()
                                .lat(1.0f)
                                .lon(2.0f)
                                .build())
                .description("descr")
                .annotation("ann")
                .requestModeration(false)
                .participantLimit(1)
                .category(categoryDto1.getId())
                .build();
        EventInputDto eventInputDto2 = EventInputDto.builder()
                .eventDate(LocalDateTime.now().plusHours(20).format(FormatConstants.DATE_TIME_FORMATTER))
                .title("event2")
                .paid(true)
                .location(
                        Location.builder()
                                .lat(1.0f)
                                .lon(2.0f)
                                .build())
                .description("descr")
                .annotation("ann")
                .requestModeration(false)
                .participantLimit(1)
                .category(categoryDto1.getId())
                .build();
        EventInputDto eventInputDto3 = EventInputDto.builder()
                .eventDate(LocalDateTime.now().plusHours(30).format(FormatConstants.DATE_TIME_FORMATTER))
                .title("event3")
                .paid(true)
                .location(
                        Location.builder()
                                .lat(1.0f)
                                .lon(2.0f)
                                .build())
                .description("descr")
                .annotation("ann")
                .requestModeration(false)
                .participantLimit(1)
                .category(categoryDto2.getId())
                .build();
        EventDto eventDto1 = eventService.createEvent(eventInputDto1, userDto1.getId());
        EventDto eventDto2 = eventService.createEvent(eventInputDto2, userDto2.getId());
        EventDto eventDto3 = eventService.createEvent(eventInputDto3, userDto2.getId());

        Assertions.assertEquals(userDto1.getId(), eventDto1.getInitiator().getId());
        Assertions.assertEquals(userDto2.getId(), eventDto2.getInitiator().getId());
        Assertions.assertEquals(userDto2.getId(), eventDto3.getInitiator().getId());

        GetEventsParamsDto paramsDto = GetEventsParamsDto.builder()
                .from(0)
                .size(10)
                .sort(SortType.EVENT_DATE)
                .users(List.of(userDto2.getId(), userDto3.getId()))
                .build();
        List<EventDto> eventDtos = eventService.getEvents(paramsDto);
        Assertions.assertEquals(2, eventDtos.size());
        Assertions.assertEquals(eventDto2.getTitle(), eventDtos.get(0).getTitle());
        Assertions.assertEquals(eventDto3.getTitle(), eventDtos.get(1).getTitle());

        paramsDto = GetEventsParamsDto.builder()
                .from(0)
                .size(10)
                .sort(SortType.EVENT_DATE)
                .categories(List.of(categoryDto1.getId()))
                .build();
        eventDtos = eventService.getEvents(paramsDto);
        Assertions.assertEquals(2, eventDtos.size());
        Assertions.assertEquals(eventDto1.getTitle(), eventDtos.get(0).getTitle());
        Assertions.assertEquals(eventDto2.getTitle(), eventDtos.get(1).getTitle());

        paramsDto = GetEventsParamsDto.builder()
                .from(0)
                .size(10)
                .rangeStart(LocalDateTime.now().plusHours(11).format(FormatConstants.DATE_TIME_FORMATTER))
                .rangeEnd(LocalDateTime.now().plusHours(25).format(FormatConstants.DATE_TIME_FORMATTER))
                .build();
        eventDtos = eventService.getEvents(paramsDto);
        Assertions.assertEquals(1, eventDtos.size());
        Assertions.assertEquals(eventDto2.getTitle(), eventDtos.get(0).getTitle());

        EventUpdateDto eventUpdateDto = EventUpdateDto.builder()
                .access(Access.ADMIN)
                .stateAction(State.PUBLISH_EVENT)
                .build();

        eventDto1 = eventService.updateEvent(eventDto1.getId(), eventUpdateDto);

        Assertions.assertEquals(State.PUBLISHED.name(), eventDto1.getState());

        paramsDto = GetEventsParamsDto.builder()
                .from(0)
                .size(10)
                .states(List.of(State.PUBLISHED.name()))
                .build();
        eventDtos = eventService.getEvents(paramsDto);
        Assertions.assertEquals(1, eventDtos.size());
        Assertions.assertEquals(eventDto1.getTitle(), eventDtos.get(0).getTitle());

        paramsDto = GetEventsParamsDto.builder()
                .from(0)
                .size(10)
                .sort(SortType.EVENT_DATE)
                .states(List.of(State.PENDING.name()))
                .build();
        eventDtos = eventService.getEvents(paramsDto);
        Assertions.assertEquals(2, eventDtos.size());
        Assertions.assertEquals(eventDto2.getTitle(), eventDtos.get(0).getTitle());
        Assertions.assertEquals(eventDto3.getTitle(), eventDtos.get(1).getTitle());

        paramsDto = GetEventsParamsDto.builder()
                .from(0)
                .size(10)
                .sort(SortType.EVENT_DATE)
                .states(List.of(State.PENDING.name()))
                .categories(List.of(categoryDto1.getId()))
                .build();
        eventDtos = eventService.getEvents(paramsDto);
        Assertions.assertEquals(1, eventDtos.size());
        Assertions.assertEquals(eventDto2.getTitle(), eventDtos.get(0).getTitle());

    }

    @Test
    void testEventUpdateByAdmin() {

        UserInputDto userInputDto1 = UserInputDto.builder()
                .name("name")
                .email("e1@mail.c")
                .build();

        UserDto userDto1 = userService.createUser(userInputDto1);


        CategoryInputDto categoryInputDto1 = CategoryInputDto.builder()
                .name("cat1")
                .build();

        CategoryDto categoryDto1 = categoryService.createCategory(categoryInputDto1);

        EventInputDto eventInputDto1 = EventInputDto.builder()
                .eventDate(LocalDateTime.now().plusHours(10).format(FormatConstants.DATE_TIME_FORMATTER))
                .title("event1")
                .paid(true)
                .location(
                        Location.builder()
                                .lat(1.0f)
                                .lon(2.0f)
                                .build())
                .description("descr")
                .annotation("ann")
                .requestModeration(false)
                .participantLimit(1)
                .category(categoryDto1.getId())
                .build();

        EventDto eventDto1 = eventService.createEvent(eventInputDto1, userDto1.getId());
        Assertions.assertEquals(userDto1.getId(), eventDto1.getInitiator().getId());
        Assertions.assertEquals(State.PENDING.name(), eventDto1.getState());

        EventUpdateDto eventUpdateDto = EventUpdateDto.builder()
                .eventDate(LocalDateTime.now().plusMinutes(10).format(FormatConstants.DATE_TIME_FORMATTER))
                .access(Access.ADMIN)
                .build();
        EventUpdateDto finalEventUpdateDto = eventUpdateDto;
        ObjectNotFoundException oe = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> eventService.updateEvent(-1L, finalEventUpdateDto));
        Assertions.assertEquals("Event not found", oe.getMessage());

        eventService.updateEvent(eventDto1.getId(), finalEventUpdateDto);

        EventDto finalEventDto = eventDto1;
        EventUpdateDto finalEventUpdateDto1 = eventUpdateDto;
        ValidationException ve = Assertions.assertThrows(ValidationException.class,
                () -> eventService.updateEvent(finalEventDto.getId(), finalEventUpdateDto1));
        Assertions.assertEquals("Cannot change event less then 1 hour before event date", ve.getMessage());

        eventInputDto1.setEventDate(LocalDateTime.now().plusHours(10).format(FormatConstants.DATE_TIME_FORMATTER));
        eventDto1 = eventService.createEvent(eventInputDto1, userDto1.getId());
        Assertions.assertEquals(userDto1.getId(), eventDto1.getInitiator().getId());
        Assertions.assertEquals(State.PENDING.name(), eventDto1.getState());

        String newEventDate = LocalDateTime.now().plusHours(20).format(FormatConstants.DATE_TIME_FORMATTER);

        eventUpdateDto = EventUpdateDto.builder()
                .eventDate(newEventDate)
                .title("new Title")
                .access(Access.ADMIN)
                .paid(false)
                .annotation("new Ann")
                .requestModeration(true)
                .description("new descr")
                .location(Location.builder().lon(-1f).lat(-2f).build())
                .participantLimit(10)
                .build();
        EventDto updatedEventDto = eventService.updateEvent(eventDto1.getId(), eventUpdateDto);
        Assertions.assertEquals("new Title", updatedEventDto.getTitle());
        Assertions.assertEquals("new Ann", updatedEventDto.getAnnotation());
        Assertions.assertEquals("new descr", updatedEventDto.getDescription());
        Assertions.assertTrue(updatedEventDto.getRequestModeration());
        Assertions.assertFalse(updatedEventDto.getPaid());
        Assertions.assertEquals(newEventDate, updatedEventDto.getEventDate().format(FormatConstants.DATE_TIME_FORMATTER));
        Assertions.assertEquals(Location.builder().lon(-1f).lat(-2f).build(), updatedEventDto.getLocation());
        Assertions.assertEquals(10, updatedEventDto.getParticipantLimit());

        eventUpdateDto = EventUpdateDto.builder()
                .stateAction(State.PUBLISH_EVENT)
                .access(Access.ADMIN)
                .build();
        ZonedDateTime publishedNow = ZonedDateTime.now(ZoneId.systemDefault());
        updatedEventDto = eventService.updateEvent(eventDto1.getId(), eventUpdateDto);
        Assertions.assertEquals(State.PUBLISHED.name(), updatedEventDto.getState());
        Assertions.assertTrue(updatedEventDto.getPublishedOn().isAfter(publishedNow.minusMinutes(1)) &&
                updatedEventDto.getPublishedOn().isBefore(publishedNow.plusMinutes(1)));

        EventDto finalEventDto1 = eventDto1;
        EventUpdateDto finalEventUpdateDto2 = eventUpdateDto;
        ConflictException ce = Assertions.assertThrows(ConflictException.class,
                () -> eventService.updateEvent(finalEventDto1.getId(), finalEventUpdateDto2));
        Assertions.assertEquals("Cannot change published event", ce.getMessage());

        EventDto eventDto2 = eventService.createEvent(eventInputDto1, userDto1.getId());
        eventUpdateDto.setStateAction(State.REJECT_EVENT);
        updatedEventDto = eventService.updateEvent(eventDto2.getId(), eventUpdateDto);

        Assertions.assertEquals(State.CANCELED.name(), updatedEventDto.getState());

        eventUpdateDto.setStateAction(State.PUBLISH_EVENT);
        EventUpdateDto finalEventUpdateDto3 = eventUpdateDto;
        ce = Assertions.assertThrows(ConflictException.class,
                () -> eventService.updateEvent(eventDto2.getId(), finalEventUpdateDto3));
        Assertions.assertEquals("Cannot change canceled event", ce.getMessage());
    }

    @Test
    void testEventUpdateByPrivate() {

        UserInputDto userInputDto1 = UserInputDto.builder()
                .name("initiator")
                .email("e1@mail.c")
                .build();

        UserInputDto userInputDto2 = UserInputDto.builder()
                .name("user")
                .email("e2@mail.c")
                .build();

        UserDto userDto1 = userService.createUser(userInputDto1);
        UserDto userDto2 = userService.createUser(userInputDto2);


        CategoryInputDto categoryInputDto1 = CategoryInputDto.builder()
                .name("cat1")
                .build();

        CategoryDto categoryDto1 = categoryService.createCategory(categoryInputDto1);

        EventInputDto eventInputDto1 = EventInputDto.builder()
                .eventDate(LocalDateTime.now().plusHours(10).format(FormatConstants.DATE_TIME_FORMATTER))
                .title("event1")
                .paid(true)
                .location(
                        Location.builder()
                                .lat(1.0f)
                                .lon(2.0f)
                                .build())
                .description("descr")
                .annotation("ann")
                .requestModeration(false)
                .participantLimit(1)
                .category(categoryDto1.getId())
                .build();

        EventDto eventDto1 = eventService.createEvent(eventInputDto1, userDto1.getId());
        Assertions.assertEquals(userDto1.getId(), eventDto1.getInitiator().getId());
        Assertions.assertEquals(State.PENDING.name(), eventDto1.getState());

        EventUpdateDto eventUpdateDto = EventUpdateDto.builder()
                .access(Access.PRIVATE)
                .userId(-1L)
                .build();

        EventUpdateDto finalEventUpdateDto = eventUpdateDto;
        ObjectNotFoundException oe = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> eventService.updateEvent(eventDto1.getId(), finalEventUpdateDto));
        Assertions.assertEquals("User not found", oe.getMessage());

        eventUpdateDto.setUserId(userDto2.getId());
        EventUpdateDto finalEventUpdateDto1 = eventUpdateDto;
        ValidationException ve = Assertions.assertThrows(ValidationException.class,
                () -> eventService.updateEvent(eventDto1.getId(), finalEventUpdateDto1));
        Assertions.assertEquals("User is not the initiator of the event", ve.getMessage());

        EventUpdateDto finalEventUpdateDto2 = eventUpdateDto;
        oe = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> eventService.updateEvent(-1L, finalEventUpdateDto2));
        Assertions.assertEquals("Event not found", oe.getMessage());

        String newEventDate = LocalDateTime.now().plusHours(1).format(FormatConstants.DATE_TIME_FORMATTER);
        eventUpdateDto = EventUpdateDto.builder()
                .userId(userDto1.getId())
                .eventDate(newEventDate)
                .title("new Title")
                .access(Access.PRIVATE)
                .paid(false)
                .annotation("new Ann")
                .requestModeration(true)
                .description("new descr")
                .location(Location.builder().lon(-1f).lat(-2f).build())
                .participantLimit(10)
                .build();
        EventDto updatedEventDto = eventService.updateEvent(eventDto1.getId(), eventUpdateDto);
        Assertions.assertEquals("new Title", updatedEventDto.getTitle());
        Assertions.assertEquals("new Ann", updatedEventDto.getAnnotation());
        Assertions.assertEquals("new descr", updatedEventDto.getDescription());
        Assertions.assertTrue(updatedEventDto.getRequestModeration());
        Assertions.assertFalse(updatedEventDto.getPaid());
        Assertions.assertEquals(newEventDate, updatedEventDto.getEventDate().format(FormatConstants.DATE_TIME_FORMATTER));
        Assertions.assertEquals(Location.builder().lon(-1f).lat(-2f).build(), updatedEventDto.getLocation());
        Assertions.assertEquals(10, updatedEventDto.getParticipantLimit());

        EventUpdateDto finalEventUpdateDto3 = eventUpdateDto;
        ve = Assertions.assertThrows(ValidationException.class,
                () -> eventService.updateEvent(eventDto1.getId(), finalEventUpdateDto3));
        Assertions.assertEquals("Cannot change event less then 2 hour before event date", ve.getMessage());

        EventDto eventDto2 = eventService.createEvent(eventInputDto1, userDto1.getId());
        EventUpdateDto publishEvent = EventUpdateDto.builder()
                .access(Access.ADMIN)
                .stateAction(State.PUBLISH_EVENT)
                .build();
        eventService.updateEvent(eventDto2.getId(), publishEvent);
        EventUpdateDto finalEventUpdateDto4 = eventUpdateDto;
        ConflictException ce = Assertions.assertThrows(ConflictException.class,
                () -> eventService.updateEvent(eventDto2.getId(), finalEventUpdateDto4));
        Assertions.assertEquals("Cannot change published event", ce.getMessage());

        EventDto eventDto3 = eventService.createEvent(eventInputDto1, userDto1.getId());
        EventUpdateDto cancelEvent = EventUpdateDto.builder()
                .userId(userDto1.getId())
                .access(Access.PRIVATE)
                .stateAction(State.CANCEL_REVIEW)
                .build();

        EventDto canceledEventDto = eventService.updateEvent(eventDto3.getId(), cancelEvent);
        Assertions.assertEquals(State.CANCELED.name(), canceledEventDto.getState());

        EventUpdateDto publishRequest = EventUpdateDto.builder()
                .userId(userDto1.getId())
                .access(Access.PRIVATE)
                .stateAction(State.SEND_TO_REVIEW)
                .build();
        EventDto pendingEventDto = eventService.updateEvent(eventDto3.getId(), publishRequest);
        Assertions.assertEquals(State.PENDING.name(), pendingEventDto.getState());

    }

    @Test
    void testGetEventById() {

        UserInputDto userInputDto1 = UserInputDto.builder()
                .name("initiator")
                .email("e1@mail.c")
                .build();

        UserInputDto userInputDto2 = UserInputDto.builder()
                .name("user")
                .email("e2@mail.c")
                .build();
        UserDto userDto1 = userService.createUser(userInputDto1);
        UserDto userDto2 = userService.createUser(userInputDto2);


        CategoryInputDto categoryInputDto1 = CategoryInputDto.builder()
                .name("cat1")
                .build();
        CategoryDto categoryDto1 = categoryService.createCategory(categoryInputDto1);

        EventInputDto eventInputDto1 = EventInputDto.builder()
                .eventDate(LocalDateTime.now().plusHours(10).format(FormatConstants.DATE_TIME_FORMATTER))
                .title("event1")
                .paid(true)
                .location(
                        Location.builder()
                                .lat(1.0f)
                                .lon(2.0f)
                                .build())
                .description("descr")
                .annotation("ann")
                .requestModeration(false)
                .participantLimit(1)
                .category(categoryDto1.getId())
                .build();

        EventDto eventDto1 = eventService.createEvent(eventInputDto1, userDto1.getId());
        Assertions.assertEquals(userDto1.getId(), eventDto1.getInitiator().getId());
        Assertions.assertEquals(State.PENDING.name(), eventDto1.getState());

        ObjectNotFoundException oe = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> eventService.getEventById(-1L, eventDto1.getId()));
        Assertions.assertEquals("User not found", oe.getMessage());

        oe = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> eventService.getEventById(userDto1.getId(), -1L));
        Assertions.assertEquals("Event not found", oe.getMessage());

        ValidationException ve = Assertions.assertThrows(ValidationException.class,
                () -> eventService.getEventById(userDto2.getId(), eventDto1.getId()));
        Assertions.assertEquals("User is not the initiator of the event", ve.getMessage());

        EventDto getEventDto = eventService.getEventById(userDto1.getId(), eventDto1.getId());
        Assertions.assertEquals(eventDto1, getEventDto);

    }


    @Test
    void testGetEventByIdPublic() {

    }

    @Test
    void testGetEventsPublic() {

    }
*/
}