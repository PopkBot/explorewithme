package main.event.model;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
import main.category.model.Category;
import main.compilation.model.Compilation;
import main.event.State;
import main.user.model.User;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "events")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Setter
@Getter
@ToString
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "annotation",nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Column(name = "confirmed_requests",nullable = false)
    private Integer confirmedRequests;
    @Column(name = "created",nullable = false)
    private ZonedDateTime createdOn;
    @Column(name = "description",nullable = false)
    private String description;
    @Column(name = "event_date",nullable = false)
    private ZonedDateTime eventDate;
    @OneToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @Column(name = "paid",nullable = false)
    private Boolean paid;
    @Column(name = "participant_limit",nullable = false)
    private Integer participantLimit;
    @Column(name = "published")
    private ZonedDateTime publishedOn;
    @Column(name = "request_moderation",nullable = false)
    private Boolean requestModeration;
    @Column(name = "state",nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;
    @Column(name = "title",nullable = false)
    private String title;
    @Column(name = "views",nullable = false)
    private Integer views;
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb",name = "location",nullable = false)
    private Location location;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(mappedBy = "events",fetch = FetchType.LAZY)
    Set<Compilation> compilations;

}
