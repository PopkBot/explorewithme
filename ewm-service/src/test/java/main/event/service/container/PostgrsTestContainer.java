package main.event.service.container;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgrsTestContainer extends PostgreSQLContainer<PostgrsTestContainer> {

    public static final String IMAGE_VERSION = "postgres:11.1";
    public static final String DATABASE_NAME = "test";
    public static PostgreSQLContainer container;

    public PostgrsTestContainer() {
        super(IMAGE_VERSION);
    }

    public static PostgreSQLContainer getInstance() {
        if (container == null) {
            container = new PostgrsTestContainer().withDatabaseName(DATABASE_NAME);
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
    }


}
