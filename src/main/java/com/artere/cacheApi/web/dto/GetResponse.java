package com.example.artere.cacheApi.web.dto;

public class GetResponse {
    private String value;

    public GetResponse() {}

    public GetResponse(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
