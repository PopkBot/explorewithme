package pack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pack.model.Hit;
import projection.StatProjection;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query(nativeQuery = true,
            value = "SELECT app, uri, COUNT(ip) as hits FROM endpoint_hit " +
                    "WHERE time_stamp >= ?1 AND time_stamp <= ?2 AND " +
                    "uri IN(?3) GROUP BY app,uri ORDER BY hits DESC")
    List<StatProjection> countHits(Timestamp start, Timestamp end, ArrayList<String> uris);

    @Query(nativeQuery = true,
            value = "SELECT app, uri, COUNT(DISTINCT ip) as hits FROM endpoint_hit " +
                    "WHERE time_stamp >= ?1 AND time_stamp <= ?2 AND " +
                    "uri IN(?3) GROUP BY app,uri ORDER BY hits DESC")
    List<StatProjection> countHitsUnique(Timestamp start, Timestamp end, ArrayList<String> uris);

    @Query(nativeQuery = true,
            value = "SELECT app, uri, COUNT(DISTINCT ip) as hits FROM endpoint_hit " +
                    "WHERE time_stamp >= ?1 AND time_stamp <= ?2 " +
                    "GROUP BY app,uri ORDER BY hits DESC")
    List<StatProjection> countHitsUnique(Timestamp start, Timestamp end);

    @Query(nativeQuery = true,
            value = "SELECT app, uri, COUNT(ip) as hits FROM endpoint_hit " +
                    "WHERE time_stamp >= ?1 AND time_stamp <= ?2 " +
                    "GROUP BY app,uri ORDER BY hits DESC")
    List<StatProjection> countHits(Timestamp start, Timestamp end);
}
