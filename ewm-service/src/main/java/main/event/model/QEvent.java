package main.event.model;

import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.*;
import main.event.State;

import java.time.ZonedDateTime;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QEvent extends EntityPathBase<Event> {

    public static final QEvent event = new QEvent("event");

    public final NumberPath<Long> initiator = createNumber("initiator.id", Long.class);

    public final EnumPath<State> state = createEnum("state", State.class);

    public final NumberPath<Long> category = createNumber("category.id", Long.class);

    public final TimePath<ZonedDateTime> createdOn = createTime("createdOn", ZonedDateTime.class);

    public final TimePath<ZonedDateTime> eventDate = createTime("eventDate", ZonedDateTime.class);

    public final StringPath annotation = createString("annotation");

    public final StringPath description = createString("description");

    public final BooleanPath paid = createBoolean("paid");

    public final NumberPath<Integer> participantLimit = createNumber("participantLimit", Integer.class);

    public final NumberPath<Integer> confirmedRequests = createNumber("confirmedRequests", Integer.class);

    public QEvent(String variable) {
        super(Event.class, forVariable(variable));
    }

    public QEvent(Path<? extends Event> path) {
        super(path.getType(), path.getMetadata());
    }

    public QEvent(PathMetadata metadata) {
        super(Event.class, metadata);
    }

}
