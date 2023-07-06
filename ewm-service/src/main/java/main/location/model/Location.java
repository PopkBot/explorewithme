package main.location.model;

import lombok.*;
import main.access.Access;
import main.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "locations")
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "lat",nullable = false)
    private Double lat;
    @Column(name = "lon",nullable = false)
    private Double lon;
    @Column(name = "radius",nullable = false)
    private Integer radius;
    @Column(name = "country")
    private String country;
    @Column(name = "city")
    private String city;
    @Column(name = "place",nullable = false)
    private String place;
    @Column(name = "access", nullable = false)
    @Enumerated(EnumType.STRING)
    private Access access;
    @OneToOne
    @JoinColumn(name = "creator_id")
    private User creator;

}
