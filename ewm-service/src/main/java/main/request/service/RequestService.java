package main.request.service;

import main.request.dto.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(Long userId, Long eventId);

    List<RequestDto> getRequestsByUserId(Long userId);

    RequestDto cancelRequest(Long userId, Long requestId);
}
