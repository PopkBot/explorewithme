package pack.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pack.model.Hit;
import projection.HitCount;
import projection.StatProjection;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface HitRepository extends JpaRepository<Hit, Long> {

    Optional<Hit> findByUriAndIp(String uri, String ip);

    @Query(nativeQuery = true, value = "SELECT COUNT(id) AS count FROM endpoint_hit " +
            "WHERE uri = ?1")
    HitCount countHits(String uri);

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
