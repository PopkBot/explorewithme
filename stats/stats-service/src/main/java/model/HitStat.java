package model;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "endpointHit")
@Getter
@Setter
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HitStat {

    @Column(name = "app",nullable = false)
    private String app;
    @Column(name = "uri",nullable = false)
    private String uri;
    @Column(name = "hits",nullable = false)
    private Long hits;


}
