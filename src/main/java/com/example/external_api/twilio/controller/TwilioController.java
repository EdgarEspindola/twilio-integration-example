package com.example.external_api.twilio.controller;

import com.example.external_api.twilio.model.TwilioSMS;
import com.example.external_api.twilio.service.TwilioService;
import com.example.external_api.twilio.model.SMSRequest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms")
public class TwilioController {
    private static final Logger logger = LoggerFactory.getLogger(TwilioController.class);
    private final TwilioService twilioService;

    public TwilioController(TwilioService twilioService) {
        this.twilioService = twilioService;
    }

    @PostMapping
    public ResponseEntity<TwilioSMS> sendSMS(@RequestBody SMSRequest smsRequest) {
        logger.info("Received request to send SMS to {} from {}", smsRequest.getTo(), smsRequest.getFrom());
        TwilioSMS sms = twilioService.sendSMS(smsRequest.getTo(), smsRequest.getFrom(), smsRequest.getBody());
        if ("FAILED".equalsIgnoreCase(sms.getStatus())) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(sms);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(sms);
    }

    @GetMapping
    public ResponseEntity<List<TwilioSMS>> getAllSMS(@RequestParam(required = false) String status,
                                                     Pageable pageable) {
        List<TwilioSMS> result = twilioService.getAllSMS(status, pageable);
        return ResponseEntity.ok(result);
    }
}
