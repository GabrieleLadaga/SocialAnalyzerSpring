package com.unical.sdcc.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PubSubService {

    private final Publisher publisher;

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    public void publishMessage(String jobId, String profileURL) {
        String payload = String.format("{\"jobId\":\"%s\",\"profileUrl\":\"%s\"}", jobId, profileURL);
        ByteString data = ByteString.copyFrom(payload, StandardCharsets.UTF_8);
        PubsubMessage message = PubsubMessage.newBuilder()
                .setData(data)
                .putAttributes("jobId", jobId)
                .build();

        ApiFuture<String> future = publisher.publish(message);

        future.addListener(() -> {
            try {
                String messageId = future.get();
                log.info("Error message pushing with ID: {}", messageId);
            } catch (Exception e) {
                log.error("Error publishing to Pub/Sub: {}", e.getMessage());
            }
        }, EXECUTOR);
    }

}
