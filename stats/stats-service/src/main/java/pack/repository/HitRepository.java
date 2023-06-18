package pack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pack.model.Hit;
import projection.StatProjection;

import java.sql.Timestamp;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query(nativeQuery = true,
            value = "SELECT app, uri, COUNT(ip) as hits FROM endpoint_hit " +
                    "WHERE time_stamp >= ?1 AND time_stamp <= ?2 AND " +
                    "uri IN(?3) GROUP BY app,uri")
    List<StatProjection> countHits(Timestamp start, Timestamp end, String uris);

    @Query(nativeQuery = true,
            value = "SELECT app, uri, COUNT(DISTINCT ip) FROM endpoint_hit " +
                    "WHERE time_stamp >= ?1 AND time_stamp <= ?2 AND " +
                    "uri IN(?3) GROUP BY app,uri")
    List<StatProjection> countHitsUnique(Timestamp start, Timestamp end, String uris);
}
