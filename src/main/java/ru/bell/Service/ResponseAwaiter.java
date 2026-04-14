package ru.bell.Service;

import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class ResponseAwaiter {
    private final Map<String, CompletableFuture<Long>> pendingRequests = new ConcurrentHashMap<>();

    public CompletableFuture<Long> waitForResponse(String correlationId) {
        CompletableFuture<Long> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        CompletableFuture.delayedExecutor(30, TimeUnit.SECONDS)
                .execute(() -> {
                    if (!future.isDone()) {
                        future.completeExceptionally(new TimeoutException());
                        pendingRequests.remove(correlationId);
                    }
                });
        return future;
    }

    public void completeResponse(String correlationId, Long clientId) {
        CompletableFuture<Long> future = pendingRequests.remove(correlationId);
        if (future != null) {
            future.complete(clientId);
        }
    }
}

