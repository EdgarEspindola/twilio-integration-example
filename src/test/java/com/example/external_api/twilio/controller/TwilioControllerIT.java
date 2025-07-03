package com.example.external_api.twilio.controller;

import com.example.external_api.twilio.SharedPostgresContainer;
import com.example.external_api.twilio.model.SMSRequest;
import com.example.external_api.twilio.model.TwilioSMS;
import com.example.external_api.twilio.repository.TwilioSMSRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
@ActiveProfiles("test")
class TwilioControllerIT {

    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> POSTGRES = SharedPostgresContainer.getInstance();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TwilioSMSRepository smsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        smsRepository.deleteAll();
    }

    @Test
    void sendSMS_and_getAllSMS_shouldPersistAndRetrieveSMS() throws Exception {
        // Mock Twilio Message.creator to avoid real API call
        try (MockedStatic<Message> messageMockedStatic = mockStatic(Message.class, invocation -> {
            if (invocation.getMethod().getName().equals("creator")) {
                MessageCreator messageCreator = mock(MessageCreator.class);
                Message message = mock(Message.class);
                when(messageCreator.create()).thenReturn(message);
                when(message.getStatus()).thenReturn(Message.Status.SENT);
                return messageCreator;
            }

            return invocation.callRealMethod();

        })) {
            SMSRequest request = new SMSRequest();
            request.setTo("+1234567890");
            request.setFrom("+0987654321");
            request.setBody("Integration test message");

            // Send SMS
            mockMvc.perform(post("/api/sms")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(org.hamcrest.Matchers.equalToIgnoringCase("SENT")))
                    .andExpect(jsonPath("$.toNumber").value("+1234567890"))
                    .andExpect(jsonPath("$.fromNumber").value("+0987654321"))
                    .andExpect(jsonPath("$.body").value("Integration test message"));
                    

            // Verify persisted in DB
            List<TwilioSMS> all = smsRepository.findAll();
            assertThat(all).hasSize(1);
            assertThat(all.get(0).getBody()).isEqualTo("Integration test message");

            // Retrieve via GET
            mockMvc.perform(get("/api/sms")
                    .param("status", "SENT"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].status").value(org.hamcrest.Matchers.equalToIgnoringCase("SENT")))
                    .andExpect(jsonPath("$[0].body").value("Integration test message"));
        }
    }
}
