package main.request.model;

import lombok.*;
import main.event.State;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "requests")
@Setter
@Getter
@ToString
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "event",nullable = false)
    private Long event;
    @Column(name = "requester_id",nullable = false)
    private Long requester;
    @Column(name = "created",nullable = false)
    private ZonedDateTime created;
    @Column(name = "status",nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;

}
