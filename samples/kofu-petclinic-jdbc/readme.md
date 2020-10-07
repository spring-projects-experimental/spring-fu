Kofu version of Spring Boot famous Petclinic sample with JDBC persistence.

To build and run the native application packaged in a lightweight container with `functional` mode:
```
mvn spring-boot:build-image
docker-compose up
```

To build and run the native application as a native executable with a local `native-image` install:
```
mvn -Pnative package
target/org.springframework.samples.petclinic.kofupetclinicapplicationkt
```
