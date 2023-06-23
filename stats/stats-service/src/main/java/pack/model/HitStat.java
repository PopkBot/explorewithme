package pack.model;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "view_stats")
@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HitStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "app", nullable = false)
    private String app;
    @Column(name = "uri", nullable = false)
    private String uri;
    @Column(name = "hits", nullable = false)
    private Long hits;


}


