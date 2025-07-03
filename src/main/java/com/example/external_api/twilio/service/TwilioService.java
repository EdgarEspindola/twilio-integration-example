package com.example.external_api.twilio.service;

import com.example.external_api.twilio.model.TwilioSMS;
import com.example.external_api.twilio.repository.TwilioSMSRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TwilioService {
    private static final Logger logger = LoggerFactory.getLogger(TwilioService.class);

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    private final TwilioSMSRepository smsRepository;

    public TwilioService(TwilioSMSRepository smsRepository) {
        this.smsRepository = smsRepository;
    }

    @Transactional
    public TwilioSMS sendSMS(String to, String from, String body) {
        Twilio.init(accountSid, authToken);

        TwilioSMS sms = new TwilioSMS(to, from, body);
        
        try {
            Message message = Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(from),
                    body
            ).create();
            sms.setStatus(message.getStatus().toString());
            sms.setDateSent(LocalDateTime.now());
            sms.setErrorMessage(null);
            logger.info("SMS sent to {} from {}: {}", to, from, body);
        
        } catch (Exception e) {
            sms.setStatus(Message.Status.FAILED.toString());
            sms.setDateSent(LocalDateTime.now());
            sms.setErrorMessage(e.getMessage());
            logger.error("Failed to send SMS to {} from {}: {}. Error: {}", to, from, body, e.getMessage());
        }

        return smsRepository.save(sms);
    }

    public List<TwilioSMS> getAllSMS(String status, Pageable pageable) {
        if (status != null && !status.isEmpty()) {
            return smsRepository.findAllByStatusIgnoringCase(status, pageable).getContent();
        }
        return smsRepository.findAll(pageable).getContent();
    }
}
