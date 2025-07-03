package com.example.external_api.twilio.controller;

import com.example.external_api.twilio.model.SMSRequest;
import com.example.external_api.twilio.model.TwilioSMS;
import com.example.external_api.twilio.service.TwilioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TwilioController.class)
class TwilioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TwilioService twilioService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void sendSMS_shouldReturnCreated_whenSuccess() throws Exception {
        SMSRequest request = new SMSRequest();
        request.setTo("+1234567890");
        request.setFrom("+0987654321");
        request.setBody("Hello!");

        TwilioSMS sms = new TwilioSMS();
        sms.setStatus("sent");

        when(twilioService.sendSMS(anyString(), anyString(), anyString())).thenReturn(sms);

        mockMvc.perform(post("/api/sms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(sms)));
    }

    @Test
    void sendSMS_shouldReturnInternalServerError_whenFailed() throws Exception {
        SMSRequest request = new SMSRequest();
        request.setTo("+1234567890");
        request.setFrom("+0987654321");
        request.setBody("Hello!");

        TwilioSMS sms = new TwilioSMS();
        sms.setStatus("failed");

        when(twilioService.sendSMS(anyString(), anyString(), anyString())).thenReturn(sms);

        mockMvc.perform(post("/api/sms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(objectMapper.writeValueAsString(sms)));
    }

    @Test
    void getAllSMS_shouldReturnListOfSMS() throws Exception {
        TwilioSMS sms1 = new TwilioSMS();
        sms1.setStatus("sent");
        TwilioSMS sms2 = new TwilioSMS();
        sms2.setStatus("sent");
        List<TwilioSMS> smsList = List.of(sms1, sms2);

        when(twilioService.getAllSMS(any(), any(Pageable.class))).thenReturn(smsList);

        mockMvc.perform(get("/api/sms")
                .param("status", "sent"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(smsList)));
    }
}
