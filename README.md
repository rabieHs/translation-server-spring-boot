# Translation App Backend

## Overview
This Spring Boot application provides a translation service for posts, supporting text and file translations.

## System Architecture
- Spring Boot Backend
- MySQL Database
- External Translation API (Flask-based)

## Database Schema
### Posts Table
```sql
CREATE TABLE posts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    userid BIGINT NOT NULL,
    text TEXT,
    file TEXT
);
```

## API Endpoints

### 1. Create a Post
- **URL:** `/api/posts`
- **Method:** `POST`
- **Request Parameters:**
    - `userId` (Long, required): User ID creating the post
    - `text` (String, optional): Text content of the post
    - `file` (MultipartFile, optional): File to be uploaded
    - `targetLanguage` (String, optional, default="english"): Target language for translation

#### Request Example
```bash
curl -X POST http://localhost:8080/api/posts \
  -F "userId=1" \
  -F "text=Hello World" \
  -F "file=@/path/to/file.pdf" \
  -F "targetLanguage=arabic"
```

### 2. Get All Posts
- **URL:** `/api/posts`
- **Method:** `GET`
- **Query Parameters:**
    - `language` (String, optional, default="english"): Language for translation

#### Request Example
```bash
curl http://localhost:8080/api/posts?language=arabic
```

### 3. Get Post by ID
- **URL:** `/api/posts/{id}`
- **Method:** `GET`

#### Request Example
```bash
curl http://localhost:8080/api/posts/1
```

## Configuration

### application.properties
```properties
# Translation API Base URL
translation.api.base-url=http://your-translation-api-url

# MySQL Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/yourdb
spring.datasource.username=your-username
spring.datasource.password=your-password
```

## File Handling
- Files are stored as Base64 encoded strings in the database
- When uploading, the file is converted to a Base64 string before saving

## Translation Process
1. For text posts, content is translated via external API
2. Files can be translated separately or along with text
3. Translation occurs on-demand when fetching posts

## Error Handling
- 404 Not Found for non-existent posts
- 500 Internal Server Error for translation failures

## Dependencies
- Spring Boot
- Spring Data JPA
- MySQL Connector
- RestTemplate for API calls
- Lombok (optional, for reducing boilerplate code)

## Recommended Improvements
- Implement caching for translations
- Add more robust error handling
- Create unit and integration tests

## Deployment
1. Configure MySQL database
2. Set translation API endpoint
3. Build with Maven: `mvn clean package`
4. Run: `java -jar target/translationapp.jar`