package main.location.service;

import main.location.dto.LocationDto;
import main.location.dto.LocationInputDto;
import main.location.model.Location;
import main.user.model.User;

public interface LocationService {

    LocationDto addLocationFromController(LocationInputDto locationInputDto);

    Location addLocationAdmin(LocationInputDto locationInputDto);

    Location addLocationPrivate(LocationInputDto locationInputDto, User creator);

    Location updateLocation(LocationInputDto locationInputDto);



}
