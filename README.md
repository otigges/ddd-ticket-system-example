# Ticket System Example

![Java CI](https://github.com/otigges/ddd-ticket-system-example/workflows/Java%20CI/badge.svg)

Example for Domain-driven Design, showing how to build technology free Internal Building Blocks with Java.

Prerequisites:

    * JDK 21
    * Optional: Keycloak
    
To build, test and run use gradle:

    ./gradlew build
    ./gradlew test
    ./gradlew bootRun
    
## API 

Rest API is available at: http://localhost:8080/

## Use Keycloak for authentication

To activate configure Keycloak you have to set these environment properties:

    KEYCLOAK_ENABLED=true
    KEYCLOAK_REALM=the-realm
    KEYCLOAK_CLIENT_SECRET=the-client-secret 
    KEYCLOAK_AUTH_SERVER_URL=http://localhost:8070/auth
    
