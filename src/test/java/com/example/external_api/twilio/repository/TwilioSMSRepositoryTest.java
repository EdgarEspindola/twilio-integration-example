package com.example.external_api.twilio.repository;

import com.example.external_api.twilio.SharedPostgresContainer;
import com.example.external_api.twilio.model.TwilioSMS;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
class TwilioSMSRepositoryTest {
    @Container
    @ServiceConnection
    private static final PostgreSQLContainer<?> POSTGRES = SharedPostgresContainer.getInstance();

    @Autowired
    private TwilioSMSRepository underTest;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
    }

    @Test
    @DisplayName("Should find all TwilioSMS by status with pagination")
    void testFindAllByStatus() {
        // Given
        TwilioSMS sms1 = new TwilioSMS();
        sms1.setStatus("SENT");
        sms1.setBody("sid1");
        sms1.setToNumber("+1234567890");
        sms1.setFromNumber("+0987654321");
        underTest.save(sms1);

        TwilioSMS sms2 = new TwilioSMS();
        sms2.setStatus("FAILED");
        sms2.setBody("sid2");
        sms2.setToNumber("+1234567891");
        sms2.setFromNumber("+0987654322");
        underTest.save(sms2);

        TwilioSMS sms3 = new TwilioSMS();
        sms3.setStatus("SENT");
        sms3.setBody("sid3");
        sms3.setToNumber("+1234567892");
        sms3.setFromNumber("+0987654323");
        underTest.save(sms3);

        // When
        Page<TwilioSMS> sentPage = underTest.findAllByStatusIgnoringCase("SENT", PageRequest.of(0, 10));

        // Then
        assertThat(sentPage).isNotNull();
        assertThat(sentPage.getTotalElements()).isEqualTo(2);
        assertThat(sentPage.getContent())
            .extracting(TwilioSMS::getStatus)
            .containsOnly("SENT");
    }
}
