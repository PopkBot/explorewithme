package main.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.event.State;
import main.event.model.Event;
import main.event.repository.EventRepository;
import main.exceptions.ConflictException;
import main.exceptions.ObjectAlreadyExistsException;
import main.exceptions.ObjectNotFoundException;
import main.exceptions.ValidationException;
import main.request.dto.RequestDto;
import main.request.dto.StatusSettingDto;
import main.request.dto.StatusSettingInputDto;
import main.request.mapper.RequestMapper;
import main.request.model.Request;
import main.request.repository.RequestRepository;
import main.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImp implements RequestService{

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public RequestDto createRequest(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(
                ()-> new ObjectNotFoundException("User not found")
        );
        Event event = eventRepository.findById(eventId).orElseThrow(
                ()-> new ObjectNotFoundException("Event not found")
        );
        Optional<Request> requestOpt = requestRepository.findByRequesterAndEvent(userId,eventId);
        if(requestOpt.isPresent()){
            throw new ObjectAlreadyExistsException("Request already exists");
        }
        if(Objects.equals(event.getInitiator().getId(), userId)){
            throw new ConflictException("Initiator cannot request for participation in his own event");
        }
        if(!event.getState().equals(State.PUBLISHED)){
            throw new ConflictException("Event has not been published yet");
        }
        Request request = Request.builder()
                .requester(userId)
                .event(eventId)
                .created(ZonedDateTime.now(ZoneId.systemDefault()))
                .build();


        if(!event.getRequestModeration() || event.getParticipantLimit() == 0){
            request.setStatus(State.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests()+1);
            eventRepository.save(event);
        }else if(event.getParticipantLimit()<=event.getConfirmedRequests()){
            throw new ConflictException("Event`s participant limit has been reached");
        }
        else{
            request.setStatus(State.PENDING);
        }
        request = requestRepository.save(request);
        log.info("Request has been created {}",request);
        return requestMapper.convertToDto(request);
    }

    @Override
    public List<RequestDto> getRequestsByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(
                ()-> new ObjectNotFoundException("User not found")
        );
        List<RequestDto> requestDtos = requestRepository.findAllByRequester(userId).stream()
                .map(requestMapper::convertToDto).collect(Collectors.toList());
        log.info("List of user`s {} requests has been returned {}",userId,requestDtos);
        return requestDtos;
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(
                ()-> new ObjectNotFoundException("User not found")
        );
        Request request = requestRepository.findById(requestId).orElseThrow(
                ()-> new ObjectNotFoundException("Request not found")
        );
        if(request.getRequester()!=userId){
            throw new ValidationException("User has no access to the request");
        }
        Event event = eventRepository.findById(request.getEvent()).orElseThrow(
                ()-> new ObjectNotFoundException("Event to found")
        );
        if(request.getStatus().equals(State.CONFIRMED)){
            event.setConfirmedRequests(event.getConfirmedRequests()-1);
            eventRepository.save(event);
        }
        event.setConfirmedRequests(event.getConfirmedRequests()-1);
        eventRepository.save(event);
        request.setStatus(State.CANCELED);
        request = requestRepository.save(request);
        log.info("Request has been deleted {}",request);
        return requestMapper.convertToDto(request);
    }

    @Override
    public List<RequestDto> getEventRequests(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(
                ()-> new ObjectNotFoundException("User not found")
        );
        Event event = eventRepository.findById(eventId).orElseThrow(
                ()-> new ObjectNotFoundException("Event not found")
        );
        if(!event.getInitiator().getId().equals(userId)){
            throw new ValidationException("User is not the initiator of the event");
        }
        List<RequestDto> requestDtos = requestRepository.findAllByEvent(eventId).stream()
                .map(requestMapper::convertToDto).collect(Collectors.toList());
        log.info("List of requests has been returned {}",requestDtos);
        return requestDtos;
    }

    @Override
    @Transactional
    public StatusSettingDto setStatusOfRequests(Long userId, Long eventId, StatusSettingInputDto dto) {
        userRepository.findById(userId).orElseThrow(
                ()-> new ObjectNotFoundException("User not found")
        );
        Event event = eventRepository.findById(eventId).orElseThrow(
                ()-> new ObjectNotFoundException("Event not found")
        );
        if(!event.getInitiator().getId().equals(userId)){
            throw new ValidationException("User is not the initiator of the event");
        }
        if(event.getParticipantLimit().equals(0) || !event.getRequestModeration()){
            List<RequestDto> confirmed = requestRepository.findAllByEventAndStatus(eventId,State.CONFIRMED)
                    .stream().map(requestMapper::convertToDto).collect(Collectors.toList());
            List<RequestDto> rejected = requestRepository.findAllByEventAndStatus(eventId,State.REJECTED)
                    .stream().map(requestMapper::convertToDto).collect(Collectors.toList());

            StatusSettingDto statusSettingDto = StatusSettingDto.builder()
                    .confirmedRequests(confirmed)
                    .rejectedRequests(rejected)
                    .build();
            log.info("Statuses of event {} requests {}",eventId,statusSettingDto);
            return statusSettingDto;
        }

        if(dto.getStatus().equals(State.REJECTED) &&
                requestRepository.countStatusRequests(State.CONFIRMED.toString(),eventId,dto.getRequestIds()).getCountId()>0){
            throw new ConflictException("Cannot reject confirmed request");
        }

        if(event.getParticipantLimit()<event.getConfirmedRequests()+dto.getRequestIds().size() &&
            dto.getStatus().equals(State.CONFIRMED)){
            throw new ConflictException("Limit of participants for this event has been reached");
        }
        requestRepository.setStatusOfRequests(dto.getStatus().toString(), dto.getRequestIds(),State.PENDING.toString());
        List<RequestDto> confirmed = requestRepository.findAllByEventAndStatus(eventId,State.CONFIRMED)
                .stream().map(requestMapper::convertToDto).collect(Collectors.toList());
        event.setConfirmedRequests(confirmed.size());
        event = eventRepository.save(event);
        if(event.getConfirmedRequests().equals(event.getParticipantLimit())){
            requestRepository.setStatusOfRequests(State.REJECTED.toString(),State.PENDING.toString());
        }
        List<RequestDto> rejected = requestRepository.findAllByEventAndStatus(eventId,State.REJECTED)
                .stream().map(requestMapper::convertToDto).collect(Collectors.toList());
        StatusSettingDto statusSettingDto = StatusSettingDto.builder()
                .confirmedRequests(confirmed)
                .rejectedRequests(rejected)
                .build();
        log.info("Statuses of event {} requests {}",eventId,statusSettingDto);
        return statusSettingDto;
    }

}
