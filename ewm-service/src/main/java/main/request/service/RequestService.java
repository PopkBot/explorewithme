package main.request.service;

import main.request.dto.RequestDto;
import main.request.dto.StatusSettingDto;
import main.request.dto.StatusSettingInputDto;

import java.util.List;

public interface RequestService {

    RequestDto createRequest(Long userId, Long eventId);

    List<RequestDto> getRequestsByUserId(Long userId);

    RequestDto cancelRequest(Long userId, Long requestId);

    List<RequestDto> getEventRequests(Long userId, Long eventId);

    StatusSettingDto setStatusOfRequests(Long userId, Long eventId, StatusSettingInputDto dto);
}
