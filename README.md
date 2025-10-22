# 🎵 Spotify Final Project

## Project Overview
This is a Spring Boot-based Spotify clone that allows users to register, log in, create playlists, listen to music, and explore artist profiles. The application tracks user listening statistics and generates dynamic session-based playlists based on user preferences.

## Technologies Used
- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security
- PostgreSQL
- Maven
- Lombok
- Swagger (OpenAPI)
- Thymeleaf (for admin/user interfaces if applicable)

## Features
- User registration, login, and email verification
- Role-based access control (Listener, Artist, Admin)
- Music and album management
- Playlist creation and management
- Listening tracking and statistics
- Session-based suggested playlists based on top genres
- Artist profile viewing with similar genre recommendations

## Getting Started

### Prerequisites
- Java 17+
- PostgreSQL
- Maven
- Git

--
--
--
--

## 📝 Postman Tutorial

This section describes how to test the Spotify Final Project APIs using **Postman**. The APIs are organized into sections: **Registration**, **Listener**, **Artist**, **Admin**, and **Other Endpoints**.

---

### 1️⃣ Registration

#### Register a User

- **Endpoint:** `POST /users/register`
- **Description:** Registers a new user (Listener or Artist)
- To verify real email account is required, and
- **Request Body (JSON):**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "username": "johndoe",
  "birthDate": "1990-01-01",
  "email": "john@example.com",
  "password": "password123",
  "role": "LISTENER"
}
```



