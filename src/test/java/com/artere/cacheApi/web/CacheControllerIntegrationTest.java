package com.artere.cacheApi.web;

import com.example.artere.cacheApi.web.dto.PutRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CacheControllerIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void putThenGetThenExpire() throws InterruptedException {
        String key = "it-key";
        PutRequest req = new PutRequest("it-value", 300);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PutRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<Void> putResp = rest.exchange("/cache/" + key, HttpMethod.PUT, entity, Void.class);
        assertEquals(HttpStatus.OK, putResp.getStatusCode());

        ResponseEntity<String> getResp = rest.getForEntity("/cache/" + key, String.class);
        assertEquals(HttpStatus.OK, getResp.getStatusCode());
        assertTrue(getResp.getBody().contains("it-value"));

        Thread.sleep(500);

        ResponseEntity<String> getAfter = rest.getForEntity("/cache/" + key, String.class);
        assertEquals(HttpStatus.NOT_FOUND, getAfter.getStatusCode());
    }
}
