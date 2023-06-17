package repository;

import dto.StatOutDto;
import model.Hit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit,Long> {

    @Query(nativeQuery = true,
            value = "SELECT app, uri, COUNT(app) FROM endpointHit "+
                    "WHERE time_stamp >= ?1 AND time_stamp <= ?2 AND "+
                    "uri IN(?3) GROUP BY app")
    List<StatOutDto> countHits(Timestamp start, Timestamp end, String uris);

    @Query(nativeQuery = true,
            value = "SELECT app, uri, COUNT(DISTINCT app) FROM endpointHit "+
                    "WHERE time_stamp >= ?1 AND time_stamp <= ?2 AND "+
                    "uri IN(?3) GROUP BY app")
    List<StatOutDto> countHitsUnique(Timestamp start, Timestamp end, String uris);
}
