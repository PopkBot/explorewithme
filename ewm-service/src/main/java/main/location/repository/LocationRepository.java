package main.location.repository;

import main.event.model.Event;
import main.location.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long>, QuerydslPredicateExecutor<Location> {

    List<Location> findAllByLatAndLonAndPlace(Double lat, Double lon, String place);

    List<Location> findAllByLatAndLonAndPlaceAndIdNot(Double lat, Double lon, String place, Long locationId);

    @Query(nativeQuery = true, value = "SELECT * FROM locations AS l LEFT JOIN events AS e ON e.location_id = l.id "+
            " where e.id IS NULL")
    List<Location> getUnusedLocations();


}
