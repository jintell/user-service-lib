# User Service Library

A reactive Java Spring Boot library for managing user profiles, authentication, addresses, and roles, designed for integration into scalable platforms.

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Main Features](#main-features)
- [Getting Started](#getting-started)
- [Usage Examples](#usage-examples)
- [Core API & Service Patterns](#core-api--service-patterns)
- [Extending the Library](#extending-the-library)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

**User Service Library** provides reusable Spring Boot components for:
- User and profile management
- Reactive address handling
- OAuth2 client registration
- Role-based access
- Template-driven authentication flows

It is designed around Spring WebFlux for fully non-blocking, scalable operations.

---

## Architecture

- **Reactive CRUD Repositories:** Leverages `ReactiveCrudRepository` interfaces for asynchronous data access.
- **Domain Models:** User, UserProfile, Address, Role, OAuth2RegisteredClient.
- **Service Layer:** Handles business logic (e.g., `AddressService`, `UserProfileService`).
- **Template Classes:** Abstracts for sign-in and sign-up processes.
- **Utility Classes:** For object mapping, UUID generation, and error handling.
- **Native SQL Projections:** Custom queries for advanced search and paging.

### Main Packages

- `org.meldtech.platform.repository`: Reactive data access.
- `org.meldtech.platform.service`: Business logic and orchestration.
- `org.meldtech.platform.model.api`: API request/response records.
- `org.meldtech.platform.util`: Utilities for serialization, paging, UUID, errors.

---

## Main Features

- **User CRUD**: Create, update, fetch users by username/publicId, enable/disable users.
- **Profile Search**: Advanced search using custom SQL, paging support.
- **Address Management**: Add, update, fetch addresses asynchronously.
- **Role Management**: Assign and query user roles.
- **OAuth2 Client Registry**: Manage OAuth2 clients for authentication flows.
- **Authentication Templates**: Pluggable sign-in and sign-up patterns.

---

## Getting Started

### Prerequisites

- Java 17+
- Spring Boot 3+
- Reactive database (e.g., R2DBC driver for your DB)
- Maven or Gradle

### Installation

Add the library as a dependency to your Spring Boot project:

```xml
<dependency>
  <groupId>org.meldtech.platform</groupId>
  <artifactId>user-service-lib</artifactId>
  <version>{latest-version}</version>
</dependency>
```

Or for Gradle:

```groovy
implementation 'org.meldtech.platform:user-service-lib:{latest-version}'
```

Configure your database properties for R2DBC in `application.yml`:

```yaml
spring:
  r2dbc:
    url: r2dbc:postgresql://localhost:5432/yourdb
    username: username
    password: password
```

---

## Usage Examples

### 1. Fetching a User by Username

```java
@Autowired
private UserRepository userRepository;

public Mono<User> getUserByUsername(String username) {
    return userRepository.findByUsername(username);
}
```

### 2. Paginated User Profile Search

```java
@Autowired
private UserProfileRepository profileRepository;

public Flux<UserProfile> searchProfiles(String firstName, String lastName, String email) {
    return profileRepository.getSearchResult(
        firstName.toLowerCase(), lastName.toLowerCase(), "", email.toLowerCase(), ""
    );
}
```

### 3. Add Address for a User

```java
@Autowired
private AddressService addressService;

public Mono<AppResponse> addAddress(AddressRequestRecord record) {
    return addressService.addAddress(record);
}
```

### 4. Get User Metrics

```java
@Autowired
private UserProfileService userProfileService;

public Mono<AppResponse> getUserMetrics() {
    return userProfileService.getUserMetrics();
}
```

---

## Core API & Service Patterns

### Reactive Repository Interfaces

```java
public interface UserRepository extends ReactiveCrudRepository<User, Integer> {
    Mono<User> findByUsername(String username);
    Mono<User> findByPublicId(String publicId);
}
```

### Typical Service Method

```java
public Mono<AppResponse> updateAddress(AddressRequestRecord record) {
    // Validates, updates, and saves address for user
    // Returns a reactive response
}
```

### Template-Based Authentication

```java
public abstract class UserSignInTemplate<S, R, T> {
    public Mono<R> login(S request) {
        // Decode, validate, and generate access token
    }
    // Abstract methods for customization
}
```

---

## Extending the Library

- Implement custom service classes by extending provided templates.
- Add new domain models and repository interfaces as needed.
- Use utility methods (`AppUtil.getUUID()`, `AppUtil.convertToType()`) for serialization and unique identifiers.

---

## Contributing

1. Fork the repo and clone.
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit and push your changes.
4. Submit a pull request.

---

## License

This project currently does not specify a license. Please contact the project owner for usage terms.

---

## Maintainers

- Author: Josiah Adetayo ([josleke@gmail.com](mailto:josleke@gmail.com), [josiah.adetayo@meld-tech.com](mailto:josiah.adetayo@meld-tech.com))

---

## Additional Resources

- [Spring WebFlux Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html)
- [Spring Data R2DBC](https://spring.io/projects/spring-data-r2dbc)

---

*Feel free to open issues or contribute enhancements!*
