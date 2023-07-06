package main.location.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.access.Access;
import main.exceptions.ObjectNotFoundException;
import main.location.client.LocationClient;
import main.location.dto.LocationClientDto;
import main.location.dto.LocationDto;
import main.location.dto.LocationInputDto;
import main.location.mapper.LocationMapper;
import main.location.model.Location;
import main.location.repository.LocationRepository;
import main.user.model.User;
import main.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationServiceImp implements LocationService{

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final LocationClient locationClient;
    private final UserRepository userRepository;

    @Override
    public LocationDto addLocationFromController(LocationInputDto locationInputDto) {
        return locationMapper.convertToDto(addLocationAdmin(locationInputDto));
    }

    @Override
    public Location addLocationAdmin(LocationInputDto locationInputDto) {
        Location location = locationRepository.save(makeLocation(locationInputDto,Access.ADMIN));
        log.info("Location has been added {}",location);
        return location;
    }

    @Override
    public Location addLocationPrivate(LocationInputDto locationInputDto, User creator) {
        if(creator == null){
            throw new ObjectNotFoundException("User not found");
        }
        Location location = makeLocation(locationInputDto,Access.PRIVATE);
        location.setCreator(creator);
        location = locationRepository.save(location);
        log.info("Location has been added {}",location);
        return location;
    }

    @Override
    public Location updateLocation(LocationInputDto locationInputDto) {
        Location location = locationRepository.findById(locationInputDto.getId()).orElseThrow(
                ()-> new ObjectNotFoundException("Location not found")
        );
        updateLocation(location,locationInputDto);
        location = locationRepository.save(location);
        log.info("Location has been updated {}",location);
        return location;
    }


    private Location makeLocation(LocationInputDto inputDto, Access access){
        Location location = locationMapper.convertToModel(inputDto);
        location.setAccess(access);
        setCityAndCountry(location,inputDto);
        return location;
    }

    private void updateLocation(Location location,LocationInputDto inputDto){
        boolean isDifferentCoords = false;
        if(inputDto.getPlace()!=null){
            location.setPlace(inputDto.getPlace());
        }
        if(inputDto.getLat()!=null && !inputDto.getLat().equals(location.getLat())){
            location.setLat(inputDto.getLat());
            isDifferentCoords=true;
        }
        if(inputDto.getLon()!=null && !inputDto.getLon().equals(location.getLon())){
            location.setLon(inputDto.getLon());
            isDifferentCoords=true;
        }
        if(isDifferentCoords) {
            setCityAndCountry(location,inputDto);
        }
    }

    private void setCityAndCountry(Location location, LocationInputDto inputDto){
        LocationDto clientDto = new LocationDto();
        ResponseEntity<Object> response = locationClient.getCountryAndCityByCoords(inputDto.getLon(), inputDto.getLat());
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            clientDto =locationMapper.convertResponseEntityToDto(response);
        }

        if(clientDto.getCity()==null){
            location.setCity("");
        }else {
            location.setCity(clientDto.getCity());
        }
        if(clientDto.getCountry()==null){
            location.setCountry("");
        }else {
            location.setCountry(clientDto.getCountry());
        }

    }






}
