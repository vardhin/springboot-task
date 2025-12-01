# Webhook Task Application

A Spring Boot application that demonstrates a complete workflow of webhook generation, SQL query preparation, and solution submission to an external API.

## Overview

This application automatically:
1. Generates a webhook URL and access token from a remote API
2. Prepares a complex SQL query for database analysis
3. Submits the SQL solution to the generated webhook with proper authentication

## Project Structure

```
springboot-task/
├── src/
│   ├── main/
│   │   ├── java/com/example/webhooktask/
│   │   │   ├── WebhookTaskApplication.java    # Main application class
│   │   │   ├── runner/
│   │   │   │   └── WebhookRunner.java         # CommandLineRunner implementation
│   │   │   ├── service/
│   │   │   │   └── WebhookService.java        # Core business logic
│   │   │   └── dto/
│   │   │       ├── WebhookRequest.java        # Request DTO for webhook generation
│   │   │       ├── WebhookResponse.java       # Response DTO from webhook generation
│   │   │       └── SolutionRequest.java       # Request DTO for solution submission
│   │   └── resources/
│   │       └── application.properties         # Application configuration
│   └── test/
│       └── java/com/example/webhooktask/
│           └── WebhookTaskApplicationTests.java
├── build.gradle                               # Gradle build configuration
├── pom.xml                                    # Maven build configuration
└── readme.md
```

## Features

- **Webhook Generation**: Automatically generates a unique webhook URL and access token
- **SQL Query Building**: Constructs a complex SQL query for employee and department analysis
- **Secure Submission**: Submits the solution with proper authentication headers
- **Comprehensive Logging**: Detailed logging at each step of the workflow
- **Error Handling**: Robust exception handling throughout the application

## SQL Query Details

The application generates a SQL query that:
- Joins `DEPARTMENT`, `EMPLOYEE`, and `PAYMENTS` tables
- Filters employees with payments > 70,000
- Calculates average age per department
- Aggregates employee names as comma-separated list
- Orders results by department ID in descending order

## Technologies Used

- **Spring Boot 3.2.0** - Application framework
- **Spring Web** - REST client and HTTP handling
- **Jackson** - JSON serialization/deserialization
- **SLF4J/Logback** - Logging framework
- **JUnit 5** - Testing framework
- **Maven/Gradle** - Build tools (dual support)

## Prerequisites

- Java 17 or higher
- Maven 3.6+ or Gradle 7.0+
- Internet connection (for external API calls)

## Installation & Setup

### Using Maven

````bash
# Clone the repository
git clone <repository-url>
cd springboot-task

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run