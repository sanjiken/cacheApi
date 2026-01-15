package com.artere.cacheApi.web;


import com.artere.cacheApi.cache.SimpleCache;
import com.artere.cacheApi.web.dto.GetResponse;
import com.artere.cacheApi.web.dto.PutRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/cache")
public class CacheController {

    private final SimpleCache<String> cache = new SimpleCache<>();

    @PutMapping("/{key}")
    public ResponseEntity<Void> put(@PathVariable String key, @RequestBody PutRequest req) {
        if (req.getValue() == null) return ResponseEntity.badRequest().build();
        cache.put(key, req.getValue(), req.getTtlMillis());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{key}")
    public ResponseEntity<GetResponse> get(@PathVariable String key) {
        String v = cache.get(key);
        if (v == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(new GetResponse(v));
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> delete(@PathVariable String key) {
        cache.remove(key);
        return ResponseEntity.noContent().build();
    }
}
