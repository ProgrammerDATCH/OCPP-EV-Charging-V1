package com.programmerdatch.ocpp_test4;

import eu.chargetime.ocpp.JSONServer;
import eu.chargetime.ocpp.ServerEvents;
import eu.chargetime.ocpp.feature.profile.ServerCoreEventHandler;
import eu.chargetime.ocpp.feature.profile.ServerCoreProfile;
import eu.chargetime.ocpp.model.core.*;
import eu.chargetime.ocpp.model.SessionInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Configuration
public class OCPPServerConfiguration {

    @Bean
    public JSONServer jsonServer() {
        // Create the core profile
        ServerCoreProfile coreProfile = new ServerCoreProfile(new ServerCoreEventHandler() {
            @Override
            public AuthorizeConfirmation handleAuthorizeRequest(UUID sessionId, AuthorizeRequest request) {
                log.info("Received authorize request from session {}: {}", sessionId, request);
                // Always authorize for demo purposes
                IdTagInfo tagInfo = new IdTagInfo();
                tagInfo.setStatus(AuthorizationStatus.Accepted);
                return new AuthorizeConfirmation(tagInfo);
            }

            @Override
            public BootNotificationConfirmation handleBootNotificationRequest(UUID sessionId, BootNotificationRequest request) {
                log.info("Received boot notification from session {}: {}", sessionId, request);
                // Accept the boot notification
                return new BootNotificationConfirmation(
                        ZonedDateTime.now(),
                        5, // 5 second heartbeat interval
                        RegistrationStatus.Accepted
                );
            }

            @Override
            public DataTransferConfirmation handleDataTransferRequest(UUID uuid, DataTransferRequest dataTransferRequest) {
                return null;
            }

            @Override
            public StartTransactionConfirmation handleStartTransactionRequest(UUID sessionId, StartTransactionRequest request) {
                log.info("Received start transaction request from session {}: {}", sessionId, request);
                IdTagInfo tagInfo = new IdTagInfo();
                tagInfo.setStatus(AuthorizationStatus.Accepted);
                return new StartTransactionConfirmation(tagInfo, 1); // Transaction ID = 1
            }

            @Override
            public StopTransactionConfirmation handleStopTransactionRequest(UUID sessionId, StopTransactionRequest request) {
                log.info("Received stop transaction request from session {}: {}", sessionId, request);
                return new StopTransactionConfirmation();
            }

            @Override
            public HeartbeatConfirmation handleHeartbeatRequest(UUID sessionId, HeartbeatRequest request) {
                log.info("Received heartbeat from session {}", sessionId);
                return new HeartbeatConfirmation(ZonedDateTime.now());
            }

            @Override
            public MeterValuesConfirmation handleMeterValuesRequest(UUID uuid, MeterValuesRequest meterValuesRequest) {
                return null;
            }

            @Override
            public StatusNotificationConfirmation handleStatusNotificationRequest(UUID sessionId, StatusNotificationRequest request) {
                log.info("Received status notification from session {}: {}", sessionId, request);
                return new StatusNotificationConfirmation();
            }
        });

        // Create the JSON server
        JSONServer server = new JSONServer(coreProfile);

        // Listen for connect/disconnect events
        server.open("localhost", 8887, new ServerEvents() {
            @Override
            public void newSession(UUID sessionId, SessionInformation sessionInfo) {
                log.info("New session: {} from {}", sessionId, sessionInfo.getIdentifier());
            }

            @Override
            public void lostSession(UUID sessionId) {
                log.info("Lost session: {}", sessionId);
            }
        });

        return server;
    }
}