package main.compilation.model;

import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.BooleanPath;
import com.querydsl.core.types.dsl.EntityPathBase;

import static com.querydsl.core.types.PathMetadataFactory.forVariable;

@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCompilation extends EntityPathBase<Compilation> {

    public static final QCompilation compilation = new QCompilation("compilation");

    public final BooleanPath pinned = createBoolean("pinned");

    public QCompilation(String variable) {
        super(Compilation.class, forVariable(variable));
    }

    public QCompilation(Path<? extends Compilation> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCompilation(PathMetadata metadata) {
        super(Compilation.class, metadata);
    }
}
