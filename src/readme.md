Quick start
Prerequisites

Java 17 and Maven installed.

Clone the repository and open the project root.

Run application

bash
mvn spring-boot:run
Run tests

bash
mvn test
API usage
Endpoint	Method	Request body	Success response
/cache/{key}	PUT	JSON { "value": "string", "ttlMillis": 60000 }	200 OK
/cache/{key}	GET	none	200 OK + { "value": "string" } or 404 Not Found
/cache/{key}	DELETE	none	204 No Content
Parameters and behavior
key path parameter: string used as cache key. Must not be null or empty.

value JSON field: string stored under the key. Required for PUT.

ttlMillis JSON field: long time-to-live in milliseconds. If omitted, default 60000 (60 seconds) is used. Use 0 or negative to indicate immediate expiration (value will be stored but may be removed immediately by cleaner).

Example requests
PUT example

bash
curl -X PUT "http://localhost:8080/cache/myKey" \
  -H "Content-Type: application/json" \
  -d '{"value":"hello world","ttlMillis":120000}'
GET example

bash
curl -X GET "http://localhost:8080/cache/myKey"
# Response 200: {"value":"hello world"}
# Response 404: when key missing or expired
DELETE example

bash
curl -X DELETE "http://localhost:8080/cache/myKey"
# Response 204 No Content
Architecture overview
High level  
The project is a small Spring Boot application exposing a REST API to store and retrieve values in an in-memory TTL cache. The cache core is implemented from standard Java concurrency primitives and does not rely on external caching libraries or Spring’s @Cacheable.

Core design choices

Storage: ConcurrentHashMap for thread-safe key→value storage.

Expiration: DelayQueue of DelayedCacheItem objects plus a single background cleaner thread that removes keys when their TTL expires.

API layer: Spring Boot @RestController exposing simple endpoints for put/get/delete.

Tests: Unit tests for cache logic and integration tests for the REST endpoints.

Why this design

ConcurrentHashMap gives lock-free reads/writes for high concurrency.

DelayQueue avoids scanning the entire map periodically; the cleaner thread blocks until the next item expires, then removes it.

File by file description
pom.xml

Maven build file. Declares Spring Boot dependencies and Java version.

src/main/java/com/example/cache/TtlCacheApplication.java

Application entry point. Starts Spring Boot and embedded server.

src/main/java/com/example/cache/cache/SimpleCache.java

Core cache implementation.

Responsibilities: put(key, value, ttlMillis), get(key), remove(key), clear(), shutdown().

Concurrency: uses ConcurrentHashMap and a single-thread ExecutorService to run the cleaner.

src/main/java/com/example/cache/cache/DelayedCacheItem.java

Implements Delayed. Holds a cache key and absolute expiration timestamp. Used by DelayQueue to schedule removals.

src/main/java/com/example/cache/web/CacheController.java

REST controller exposing /cache/{key} endpoints for PUT, GET, DELETE.

Instantiates and uses SimpleCache<String>.

src/main/java/com/example/cache/web/dto/PutRequest.java

DTO for PUT request body. Fields: value and ttlMillis. Default TTL set to 60000.

src/main/java/com/example/cache/web/dto/GetResponse.java

DTO for GET response. Field: value.

src/test/java/com/example/cache/cache/SimpleCacheTest.java

Unit tests for cache behavior: basic put/get, TTL expiration, and simple concurrency test.

src/test/java/com/example/cache/web/CacheControllerIntegrationTest.java

Integration test that starts the Spring context and verifies PUT→GET→expire→GET behavior using TestRestTemplate.

README.md

This file. Explains how to run, API usage, architecture and file roles.