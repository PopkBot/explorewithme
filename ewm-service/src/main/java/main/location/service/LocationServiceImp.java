package main.location.service;

import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.CustomPageRequest;
import main.access.Access;
import main.event.State;
import main.event.model.QEvent;
import main.event.repository.EventRepository;
import main.exceptions.ConflictException;
import main.exceptions.ObjectAlreadyExistsException;
import main.exceptions.ObjectNotFoundException;
import main.location.client.LocationClient;
import main.location.dto.LocationDto;
import main.location.dto.LocationGetParamsDto;
import main.location.dto.LocationInputDto;
import main.location.mapper.LocationMapper;
import main.location.model.Location;
import main.location.model.QLocation;
import main.location.repository.LocationRepository;
import main.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationServiceImp implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;
    private final LocationClient locationClient;
    private final EventRepository eventRepository;

    @Override
    public LocationDto addLocationFromController(LocationInputDto locationInputDto) {
        return locationMapper.convertToDto(addLocationAdmin(locationInputDto));
    }

    @Override
    public LocationDto updateLocationFromController(LocationInputDto locationInputDto) {

        Location location = locationRepository.findById(locationInputDto.getId()).orElseThrow(
                () -> new ObjectNotFoundException("Location not found")
        );
        Long usages = eventRepository.countLocationUsages(locationInputDto.getId()).getCountId();
        if (usages != 0) {
            throw new ConflictException("Unable to update: location is in use");
        }
        patchLocation(location, locationInputDto);
        if (locationRepository.findAllByLatAndLonAndPlaceAndIdNot(locationInputDto.getLat(),
                locationInputDto.getLon(), locationInputDto.getPlace(), location.getId()).size() > 0) {
            throw new ObjectAlreadyExistsException("Location already exists");
        }
        location = locationRepository.save(location);
        log.info("Location has been updated {}", location);
        return locationMapper.convertToDto(location);
    }

    @Override
    public LocationDto deleteLocationById(Long id) {
        Location location = locationRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("Location not found")
        );
        Long usages = eventRepository.countLocationUsages(id).getCountId();
        if (usages != 0) {
            throw new ConflictException("Unable to delete: location is in use");
        }
        locationRepository.delete(location);
        log.info("Location has been deleted {}", location);
        return locationMapper.convertToDto(location);
    }

    @Override
    public List<LocationDto> deleteUnusedLocations() {
        List<Location> unusedLocations = locationRepository.getUnusedLocations();
        locationRepository.deleteAll(unusedLocations);
        log.info("Unused locations have been deleted {}", unusedLocations);
        return unusedLocations.stream().map(locationMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<LocationDto> getLocations(LocationGetParamsDto dto) {

        dto.validate();
        BooleanExpression query = QLocation.location.isNotNull();
        if (!dto.getAccess().equals(Access.ADMIN)) {
            JPQLQuery<Long> subQuery = JPAExpressions.select(QEvent.event.location)
                    .from(QEvent.event)
                    .where(QEvent.event.state.eq(State.PUBLISHED));
            query = query.and(QLocation.location.id.in(subQuery));
        }
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
        Pageable page = new CustomPageRequest(dto.getFrom(), dto.getSize());
        Page<Location> locationPage = locationRepository.findAll(query, page);
        List<LocationDto> locationDtos = locationPage.getContent().stream()
                .map(locationMapper::convertToDto).collect(Collectors.toList());
        log.info("Page of locations has been returned {}", locationDtos);
        return locationDtos;
    }

    @Override
    public LocationDto getLocationById(Long id) {
        Location location = locationRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("Location not found")
        );
        log.info("Location has been returned {}", location);
        return locationMapper.convertToDto(location);
    }

    @Override
    public LocationDto getLocationByIdPublic(Long id) {
        Location location = locationRepository.findById(id).orElseThrow(
                () -> new ObjectNotFoundException("Location not found")
        );
        if (eventRepository.countStateLocationUsages(id, State.PUBLISHED.toString(),
                -1L).getCountId() == 0 && location.getAccess().equals(Access.PRIVATE)) {
            throw new ObjectNotFoundException("Location not found");
        }
        log.info("Location has been returned {}", location);
        return locationMapper.convertToDto(location);
    }

    @Override
    @Transactional
    public Location addLocationAdmin(LocationInputDto locationInputDto) {
        if (locationRepository.findAllByLatAndLonAndPlace(locationInputDto.getLat(),
                locationInputDto.getLon(), locationInputDto.getPlace()).size() > 0) {
            throw new ObjectAlreadyExistsException("Location already exists");
        }
        Location location = locationRepository.save(makeLocation(locationInputDto, Access.ADMIN));
        log.info("Location has been added {}", location);
        return location;
    }

    @Override
    @Transactional
    public Location addLocationPrivate(LocationInputDto locationInputDto, User creator) {
        if (creator == null) {
            throw new ObjectNotFoundException("User not found");
        }
        if(locationInputDto.getRadius() == null){
            locationInputDto.setRadius(10);
        }
        if(locationInputDto.getPlace() == null){
            locationInputDto.setPlace("Untitled place");
        }
        if (locationRepository.findAllByLatAndLonAndPlace(locationInputDto.getLat(),
                locationInputDto.getLon(), locationInputDto.getPlace()).size() > 0) {
            throw new ObjectAlreadyExistsException("Location already exists");
        }
        Location location = makeLocation(locationInputDto, Access.PRIVATE);
        location.setCreator(creator);
        location = locationRepository.save(location);
        log.info("Location has been added {}", location);
        return location;
    }

    @Override
    @Transactional
    public Location updateLocation(LocationInputDto locationInputDto) {
        Location location = locationRepository.findById(locationInputDto.getId()).orElseThrow(
                () -> new ObjectNotFoundException("Location not found")
        );
        patchLocation(location, locationInputDto);
        if (locationRepository.findAllByLatAndLonAndPlaceAndIdNot(locationInputDto.getLat(),
                locationInputDto.getLon(), locationInputDto.getPlace(), location.getId()).size() > 0) {
            throw new ObjectAlreadyExistsException("Location already exists");
        }
        location = locationRepository.save(location);
        log.info("Location has been updated {}", location);
        return location;
    }


    private Location makeLocation(LocationInputDto inputDto, Access access) {
        Location location = locationMapper.convertToModel(inputDto);
        location.setAccess(access);
        setCityAndCountry(location, inputDto);
        return location;
    }

    private void patchLocation(Location location, LocationInputDto inputDto) {
        boolean isDifferentCoords = false;
        if (inputDto.getPlace() != null) {
            location.setPlace(inputDto.getPlace());
        }
        if (inputDto.getLat() != null && !inputDto.getLat().equals(location.getLat())) {
            location.setLat(inputDto.getLat());
            isDifferentCoords = true;
        }
        if (inputDto.getLon() != null && !inputDto.getLon().equals(location.getLon())) {
            location.setLon(inputDto.getLon());
            isDifferentCoords = true;
        }
        if (inputDto.getRadius() != null) {
            location.setRadius(inputDto.getRadius());
        }
        if (isDifferentCoords) {
            setCityAndCountry(location, inputDto);
        }
    }

    private void setCityAndCountry(Location location, LocationInputDto inputDto) {
        LocationDto clientDto = new LocationDto();
        ResponseEntity<Object> response = locationClient.getCountryAndCityByCoords(inputDto.getLon(), inputDto.getLat());
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            clientDto = locationMapper.convertResponseEntityToDto(response);
        }

        if (clientDto.getCity() == null) {
            location.setCity("");
        } else {
            location.setCity(clientDto.getCity());
        }
        if (clientDto.getCountry() == null) {
            location.setCountry("");
        } else {
            location.setCountry(clientDto.getCountry());
        }

    }


}
