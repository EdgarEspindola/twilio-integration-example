package com.example.external_api.twilio;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class SharedPostgresContainer extends PostgreSQLContainer<SharedPostgresContainer> {
    private static final DockerImageName IMAGE_NAME = DockerImageName.parse("postgres:17-alpine");

    public static volatile SharedPostgresContainer sharedPostgresContainer;

    private SharedPostgresContainer(DockerImageName dockerImageName) {
        super(dockerImageName);
        this.withReuse(true);
        // .withUsername("username")
        // .withDatabaseName("databasename")
        // .withLabel("name", "name")
        // .withPassword("password");
    }

    public static SharedPostgresContainer getInstance() {
        if (sharedPostgresContainer == null) {
            synchronized (SharedPostgresContainer.class) {
                if (sharedPostgresContainer == null) {
                    sharedPostgresContainer = new SharedPostgresContainer(IMAGE_NAME);
                    sharedPostgresContainer.start();
                }
            }
        }

        return sharedPostgresContainer;
    }
}