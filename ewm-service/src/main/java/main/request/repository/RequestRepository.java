package main.request.repository;

import main.event.State;
import main.request.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Optional<Request> findByRequesterAndEvent(Long requesterId, Long eventId);

    List<Request> findAllByRequester(Long requesterId);

    List<Request> findAllByEvent(Long event);

    List<Request> findAllByEventAndStatus(Long eventId, State status);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE requests SET status = ?1 "+
            "WHERE id IN(?2) AND status = ?3")
    void setStatusOfRequests(String newStatus, List<Long> ids, String checkStatus);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE requests SET status = ?1 "+
            "WHERE status = ?2")
    void setStatusOfRequests(String newStatus, String checkStatus);
}
