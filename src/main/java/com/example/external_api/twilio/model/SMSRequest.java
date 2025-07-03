package com.example.external_api.twilio.model;

public class SMSRequest {
    private String to;
    private String from;
    private String body;

    public SMSRequest() {}

    public SMSRequest(String to, String from, String body) {
        this.to = to;
        this.from = from;
        this.body = body;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
