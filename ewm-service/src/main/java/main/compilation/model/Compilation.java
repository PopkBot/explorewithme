package main.compilation.model;

import lombok.*;
import main.event.model.Event;

import javax.persistence.*;
import java.util.Set;

/*
Я правильно понимаю, что замечание про @EqualsAndHashCode относилось к тому, что в пределах одной
сессии объекты сущностей БД корректно работают с equals() и hashCode() от Object?
 */

@Entity
@Table(name = "compilations")
@Setter
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "pinned", nullable = false)
    private Boolean pinned;
    @Column(name = "title", nullable = false)
    private String title;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_compilation",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    Set<Event> events;
}
