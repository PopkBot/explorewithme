package pack.model;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "endpoint_hit")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Hit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "app", nullable = false)
    private String app;
    @Column(name = "uri", nullable = false)
    private String uri;
    @Column(name = "ip", nullable = false)
    private String ip;
    @Column(name = "time_stamp", nullable = false)
    private ZonedDateTime created;

}
