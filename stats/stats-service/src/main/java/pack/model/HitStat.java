package pack.model;


import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@IdClass(HitStatKey.class)
@Table(name = "view_stats")
@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HitStat {

    @Id
    @Column(name = "app", nullable = false)
    private String app;
    @Id
    @Column(name = "uri", nullable = false)
    private String uri;
    @Column(name = "hits", nullable = false)
    private Long hits;


}


