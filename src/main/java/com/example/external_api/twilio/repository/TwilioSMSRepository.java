package com.example.external_api.twilio.repository;

import com.example.external_api.twilio.model.TwilioSMS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TwilioSMSRepository extends JpaRepository<TwilioSMS, Long> {
    Page<TwilioSMS> findAllByStatusIgnoringCase(String status, Pageable pageable);
}
