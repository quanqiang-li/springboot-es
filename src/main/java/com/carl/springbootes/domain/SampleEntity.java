package com.carl.springbootes.domain;

import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "sample")
public class SampleEntity {

    private String id;

    private String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
