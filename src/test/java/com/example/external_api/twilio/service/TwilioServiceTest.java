package com.example.external_api.twilio.service;

import com.example.external_api.twilio.model.TwilioSMS;
import com.example.external_api.twilio.repository.TwilioSMSRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TwilioServiceTest {

    @Mock
    private TwilioSMSRepository smsRepository;

    @InjectMocks
    private TwilioService underTest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(underTest, "accountSid", "testSid");
        ReflectionTestUtils.setField(underTest, "authToken", "testToken");
    }

    @Test
    void sendSMS_shouldSendMessageAndSaveToRepository() {
        // Given
        String to = "+1234567890";
        String from = "+0987654321";
        String body = "Hello, World!";
        String status = "sent";

        // Mock Twilio static methods
        try (MockedStatic<Twilio> twilioMock = mockStatic(Twilio.class);
             MockedStatic<Message> messageMock = mockStatic(Message.class)) {

            MessageCreator creatorMock = mock(MessageCreator.class);
            Message message = mock(Message.class);

            messageMock.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
                    .thenReturn(creatorMock);
            when(creatorMock.create()).thenReturn(message);
            when(message.getStatus()).thenReturn(Message.Status.SENT);

            when(smsRepository.save(any(TwilioSMS.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            TwilioSMS result = underTest.sendSMS(to, from, body);

            // Then
            InOrder inOrder = inOrder(creatorMock, smsRepository);
            inOrder.verify(creatorMock).create();
            inOrder.verify(smsRepository).save(any(TwilioSMS.class));

            assertEquals(status, result.getStatus());
            assertEquals(to, result.getToNumber());
            assertEquals(from, result.getFromNumber());
            assertEquals(body, result.getBody());
            assertNull(result.getErrorMessage());
        }
    }

    @Test
    void sendSMS_shouldHandleExceptionAndSaveFailedStatus() {
        // Given
        String to = "+1234567890";
        String from = "+0987654321";
        String body = "Hello, World!";
        String errorMsg = "Twilio error";

        try (MockedStatic<Twilio> twilioMock = mockStatic(Twilio.class);
             MockedStatic<Message> messageMock = mockStatic(Message.class)) {

            MessageCreator creatorMock = mock(MessageCreator.class);

            messageMock.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), anyString()))
                    .thenReturn(creatorMock);
            when(creatorMock.create()).thenThrow(new RuntimeException(errorMsg));

            when(smsRepository.save(any(TwilioSMS.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            TwilioSMS result = underTest.sendSMS(to, from, body);

            // Then
            assertEquals("failed", result.getStatus());
            assertEquals(errorMsg, result.getErrorMessage());
            assertEquals(to, result.getToNumber());
            assertEquals(from, result.getFromNumber());
            assertEquals(body, result.getBody());
        }
    }

    @Test
    void getAllSMS_shouldReturnAllSMS_whenStatusIsNull() {
        // Given
        List<TwilioSMS> smsList = List.of(
                new TwilioSMS("+1234567890", "+0987654321", "Hello 1"),
                new TwilioSMS("+1234567891", "+0987654322", "Hello 2")
        );
        Pageable pageable = mock(Pageable.class);
        Page<TwilioSMS> page = mock(Page.class);
        when(smsRepository.findAll(pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(smsList);

        // When
        List<TwilioSMS> result = underTest.getAllSMS(null, pageable);

        // Then
        assertEquals(2, result.size());
        assertEquals("+1234567890", result.get(0).getToNumber());
        assertEquals("+1234567891", result.get(1).getToNumber());
        verify(smsRepository).findAll(pageable);
    }

    @Test
    void getAllSMS_shouldReturnFilteredSMS_whenStatusIsProvided() {
        // Given
        String status = "SENT";
        List<TwilioSMS> smsList = List.of(
                new TwilioSMS("+1234567890", "+0987654321", "Hello 1")
        );
        Pageable pageable = mock(Pageable.class);
        Page<TwilioSMS> page = mock(Page.class);
        when(smsRepository.findAllByStatusIgnoringCase(status, pageable)).thenReturn(page);
        when(page.getContent()).thenReturn(smsList);

        // When
        List<TwilioSMS> result = underTest.getAllSMS(status, pageable);

        // Then
        assertEquals(1, result.size());
        assertEquals("+1234567890", result.get(0).getToNumber());
        verify(smsRepository).findAllByStatusIgnoringCase(status, pageable);
    }
}
