package com.example.external_api.twilio.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TwilioSMS {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String toNumber;
    private String fromNumber;
    private String body;
    private String status;
    private LocalDateTime dateSent;
    private String errorMessage;

    public TwilioSMS() {}

    public TwilioSMS(String toNumber, String fromNumber, String body) {
        this.toNumber = toNumber;
        this.fromNumber = fromNumber;
        this.body = body;
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getToNumber() { return toNumber; }
    public void setToNumber(String toNumber) { this.toNumber = toNumber; }
    public String getFromNumber() { return fromNumber; }
    public void setFromNumber(String fromNumber) { this.fromNumber = fromNumber; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getDateSent() { return dateSent; }
    public void setDateSent(LocalDateTime dateSent) { this.dateSent = dateSent; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
}
