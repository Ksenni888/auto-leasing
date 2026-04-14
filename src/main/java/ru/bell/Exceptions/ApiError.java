package ru.bell.Exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

    @Data
    @RequiredArgsConstructor
    public class ApiError {
        public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

        private final HttpStatus status;
        private final String reason;
        private final String message;
        private final List<String> errors;
        @JsonFormat(pattern = TIME_FORMAT)
        private final LocalDateTime timestamp;
    }

