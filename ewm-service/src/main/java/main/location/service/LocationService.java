package main.location.service;

import main.location.dto.LocationDto;
import main.location.dto.LocationGetParamsDto;
import main.location.dto.LocationInputDto;
import main.location.model.Location;
import main.user.model.User;

import java.util.List;

public interface LocationService {

    LocationDto addLocationFromController(LocationInputDto locationInputDto);

    LocationDto updateLocationFromController(LocationInputDto locationInputDto);

    LocationDto deleteLocationById(Long id);

    List<LocationDto> deleteUnusedLocations();

    List<LocationDto> getLocations(LocationGetParamsDto dto);

    LocationDto getLocationById(Long id);

    LocationDto getLocationByIdPublic(Long id);

    Location addLocationAdmin(LocationInputDto locationInputDto);

    Location addLocationPrivate(LocationInputDto locationInputDto, User creator);

    Location updateLocation(LocationInputDto locationInputDto);


}
