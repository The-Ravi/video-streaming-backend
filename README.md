# Video Engagement Statistics System

![Java Version](https://img.shields.io/badge/Java-17)
![SpringBoot Version](https://img.shields.io/badge/springboot-3.2.X)

## Table of Contents

- [Description](#description)
- [Features](#Features)
- [Requirements](#requirements)
- [Configuration](#configuration)
- [System Flow](#system-flow)
- [Server Deployment](#server-deployment)
- [Installation](#installation)
- [Usage](#usage)
- [Contact Information](#contact-information)


## Description

This repository contains a Video Engagement Statistics System built using Spring Boot. The system tracks and manages video engagement data, such as impressions and views, for videos in a streaming platform. The application includes multiple services to handle operations related to video content, such as retrieving engagement statistics, tracking new engagements, and managing video metadata.

## Detailed Documentation and Assumptions

- **Security Architecture**
    1. **JWT Authentication**
        - To secure the endpoints, JWT (JSON Web Tokens) is used for authentication. This ensures that sensitive operations, such as fetching video content and engagement statistics, are only accessible by authenticated users.
        - The JWT token is generated upon login and used for securing all other endpoints. JWT tokens are stateless and do not require storing session data on the server side, making the system scalable.

    2. **Token Generation and Refresh**
        - The application provides two key APIs to handle token authentication
            - POST /v1/auth/get-token: This endpoint generates a JWT token using basic authentication credentials `(username: admin, password: admin)`.
            - POST /v1/auth/token-refresh: This endpoint generates new JWT token using refresh token provided at get-token time `(refreshToken: refreshToken)`.

    3. **Credentials**
        - The current username is `admin` and the password is `admin`. However, the password is stored in the database as Base64 decoded for security purposes.
        - The password encryption can be updated in the future to improve security, and this mechanism ensures that sensitive data is securely stored.

    4. **Secure Endpoints**
        - All other endpoints in the application are secured and require a valid JWT token to access.

    5. **Role-Based Authorization (Optional for future extensions)**
        - The system can be extended to include role-based authorization by including user roles in the JWT payload. For example, you could have ADMIN, USER, etc., to control access to different parts of the system based on the role.
        
    6. **JWT Token Expiration**
        - The JWT tokens have an expiration time is set to 1 hour and refresh token validity is double for security. Once expired, the user will need to refresh the token using the refresh token API.

- **Design Decisions**

    1. **Architecture**
        - Spring Boot Framework: Chosen as the backend framework due to its ease of use, scalability, and built-in support for REST APIs. Spring Boot's features such as automatic configuration, embedded web servers, and a wide range of tools make it an ideal choice for this project.

        - JPA/Hibernate: Used for interacting with the database. The relational database design leverages JPA's ORM capabilities to simplify data persistence.

        - Database Choice: We used MySQL as the database for persistence, as it offers relational data storage and high performance for this use case. Data is stored for video content and engagement statistics in separate tables to maintain separation of concerns.

        - Error Handling: We centralized error handling through a custom exception (InternalServerErrorException) to maintain consistent error responses across all endpoints.

        - Builder Pattern: We used builder design pattern for better object management and handling and creation in almost all of the entities, pojos,request,responses.

    2. **Engagement Statistics Tracking**
        - Kafka Integration (Optional): We designed the system to optionally support Kafka for handling engagement tracking messages. Kafka is a powerful distributed streaming platform, allowing the system to scale efficiently and process large amounts of engagement data asynchronously. If Kafka is not used, the system stores engagement data directly in the database.

        - Event-driven Design: The decision to use an event-driven approach for tracking engagements allows us to maintain scalability. Engagement events are asynchronously processed, enabling real-time tracking of user interactions with video content.

    3. **Soft Delete Approach**
        - We implemented soft deletes for video content, meaning that videos are marked as deleted but remain in the database. This approach avoids the loss of video content data while still preventing deleted videos from being included in search results or engagement tracking.

    4. **Pagination and Search Functionality**
        - The search functionality for video metadata supports multiple fields like title, director, genre, and others. We added pagination to ensure that results are limited to manageable sizes. The use of Spring Data JPA Specifications allows flexible and efficient querying across multiple fields.

    5. **Unit Testing and Mocking**
        - We used JUnit 5 for unit testing and Mockito for mocking dependencies. Mocking the repositories and services allows us to test each method in isolation without needing an actual database or external services.

- **Assumptions Made**
    1. **Engagement Type Enumeration**
        - We assume that the engagement types (VIEW, IMPRESSION) are predefined and are passed correctly in the API requests. This is handled by the EngagementType enum, which validates the engagement type provided in requests.
    
    2. **Pagination**
        - The system assumes that all search results are paginated, and only a specific number of records are returned for each request. The page number and size are required as parameters for search operations.

    3. **Database and Server Environment**
        - The database and server environment are set up correctly. We assume that MySQL is running locally or in a cloud environment, and the appropriate configurations (such as database URL, username, and password) are set up in the application.properties.

## Features

- Video Content Retrieval: Fetch video metadata and engagement statistics for a given video content ID.
- Search Video Metadata: Allows users to search for video metadata using different search criteria (title, director, genre, cast) with support for pagination.
- Track Engagement Statistics: Track and store video engagement statistics such as impressions and views.
- Soft Delete Video: Soft delete videos, marking them as inactive rather than deleting them from the database.
- Publish Video
- Video Metadata Management: Add, edit, and update metadata for videos, such as title, director, and year of release.
- Error Handling: The system has robust error handling to manage common failures like invalid video IDs or unexpected system errors.

## Requirements

- **Java**: 17
- **MySQL**: 8.0.33
- **Maven**: 3.2.12 or higher
- **JUnit 5 & Mockito**
- **Kafka** (Optional)

## Configuration

- **Setup Database**

  - Create a MySQL database:

    ```sql
        CREATE DATABASE database_name;
    ```

- **Application Properties**

  - Add Common Configuration in application properties `src/main/resources/application.properties`.

  ```properties
     # Active Profile
     spring.profiles.active=dev
  ```

  - Add profile specific configuration in profile (prod/dev/local) `src/main/resources/application-{profile}.properties`.

  ```properties
        # * PORT CONFIGURATIONS
        server.port= desired_port

        # Database Config
        spring.datasource.url=jdbc:mysql://localhost:3306/database_name
        spring.datasource.username=your_username
        spring.datasource.password=your_password

        # Log Config
        logging.file.path=your_log_path
        logging.file.name= your_file_name

        # jwt/session configurations
        jwtSecretKey= desireSeceretKey
        jwtExpirationTimeInSec= desireTime

        # To use kafka or not
        engagement.useKafka: true/false
  ```

# system-flow

- **Authentication Flow**

    (Handles User Login, JWT Token Generation, and Security)

    Components: AuthenticationController, AuthenticationServiceImpl

    Flow:

    [User Requests Login]
        |
        v
    [AuthenticationController]  
        |  
        v
    [AuthenticationService] ---> [Validate User Credentials]  
        |  
        v  
    [Generate JWT Token]  
        |  
        v  
    [Return JWT & Refresh Token]  

- **Publish Video & Streaming Flow**

    (Manages Video Publish, Metadata, and Streaming Requests)

    Components: VideoController, VideoServiceImpl

    Flow:

    [User Publish Video]
        |
        v
    [VideoController]  
        |  
        v
    [VideoService]  
        |  
        v  
    [Store Video Metadata] ---> [Process & Transcode Video]  
        |  
        v  
    [Save Video URL in Database]  
        |  
        v  
    [Return Publish Success Response]  

    -------------------

    [User Requests to Play Video]
        |
        v
    [VideoController]  
        |  
        v  
    [VideoService]  
        |  
        v  
    [Fetch Video URL from Database]  
        |  
        v  
    [Return Streaming URL]

- **Video Engagement Tracking Flow**

    (Tracks Video Views & Impressions in Database or Kafka)

    Components: EngagementController, EngagementServiceImpl

    Flow:

    [User Engagement Video]
        |
        v
    [EngagementController]  
        |  
        v
    [EngagementService]  
        |  
        v  
    [Check if Kafka is Enabled?]  
        |  
        v  
    [YES] ---> [Send Engagement Event to Kafka]  
        |
        v  
    [NO] ---> [Check if Engagement Exists in DB]  
        |  
        v  
    [Update Impressions/Views]  
        |  
        v  
    [Save in Database]  
        |  
        v  
    [Return Success Response]  

-  **Engagement Analytics Flow**

    (Retrieves Statistics Like Views & Impressions for a Video)

    Components: EngagementController, EngagementServiceImpl

    Flow:

    [User Requests Engagement Stats for Video]
        |
        v
    [EngagementController]  
        |  
        v  
    [EngagementService]  
        |  
        v  
    [Fetch Video from Database]  
        |  
        v  
    [Retrieve Engagement Stats (Views, Impressions)]  
        |  
        v  
    [Return Engagement Response] 

- **Video Metadata Search Flow**

    (Allows Searching for Videos Using Various Filters and Pagination)

    Components: VideoController, VideoServiceImpl

    Flow:

    [User Searches for Videos]
        |
        v
    [VideoController]  
        |  
        v  
    [VideoService]  
        |  
        v  
    [Fetch Matching Videos Using Filters]  
        |  
        v  
    [Paginate Results]  
        |  
        v  
    [Return Search Results] 


- **Video Soft Delete Flow**

    (Marks Videos as Deleted Without Removing from Database)

    Components: VideoController, VideoServiceImpl

    Flow:

    [User Requests to Delete Video]
        |
        v
    [VideoController]  
        |  
        v  
    [VideoService]  
        |  
        v  
    [Find Video in Database]  
        |  
        v  
    [Check if Already Deleted]  
        |  
        v  
    [YES] ---> [Return Already Deleted Message]  
        |
        v  
    [NO] ---> [Mark as Deleted in DB by marking isActive False]  
        |  
        v  
    [Return Soft Delete Success Message]  

## Server Deployment

- The server should have java 17, MySQL and tomcat.
- The deployment package should be a WAR or JAR file, depending on the application requirements.
- SQL dump should be uploaded on the server DB.

## Installation

1. **Clone the Repository**

   ```sh
       cd existing_repo
       git remote add origin https://github.com/The-Ravi/videostreaming
       git branch -M main
       git push -uf origin main
   ```
   
2. **Build the Project**
   ```sh
       mvn clean install
   ```

## Usage

1. **Run the Application**

   ```sh
       mvn spring-boot:run
   ```

2. **Access the APIs**
   - The application will be running at `http://localhost:8070`.
   - API documentation can be accessed at `http://localhost:8070/swagger-ui/index.html#`.

## Contact Information

- **Project Maintainer**: [Ravi Mishra](ravi.mishra@blackngreen.com)
- **Repository**: [Video Engagement Statistics System](https://github.com/The-Ravi/videostreaming)
