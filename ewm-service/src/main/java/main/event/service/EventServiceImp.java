package main.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.category.model.Category;
import main.category.repository.CategoryRepository;
import main.event.State;
import main.event.dto.EventInputDto;
import main.event.mapper.EventMapper;
import main.event.model.Event;
import main.event.repository.EventRepository;
import main.user.model.User;
import main.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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
    public Event createEvent(EventInputDto eventInputDto, Long userId) {
        User user = userRepository.findById(userId).get();
        Category category = categoryRepository.findById(eventInputDto.getCategory()).get();
        return eventRepository.save(makeEvent(eventInputDto,user,category));
    }

    private Event makeEvent(EventInputDto eventInputDto, User initiator, Category category){
        Event event = eventMapper.convertToCategory(eventInputDto);
        event.setConfirmedRequests(0);
        event.setCreated(ZonedDateTime.now(ZoneId.systemDefault()));
        event.setInitiator(initiator);
        event.setState(State.TEST);
        event.setViews(0);
        event.setCategory(category);
        return event;
    }
}
