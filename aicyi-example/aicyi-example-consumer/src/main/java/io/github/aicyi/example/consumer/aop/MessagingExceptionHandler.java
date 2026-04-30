package io.github.aicyi.example.consumer.aop;

import io.github.aicyi.commons.core.message.MessageSendException;
import io.github.aicyi.midware.web.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = {"io.github.aicyi.example"})
public class MessagingExceptionHandler {

    @ExceptionHandler(MessageSendException.class)
    public ResponseEntity<Response<Void>> handleMessageSendException(MessageSendException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(Response.failure("MESSAGE_SEND_FAILURE", e.getMessage()));
    }

    @ExceptionHandler(MessageConversionException.class)
    public ResponseEntity<Response<Void>> handleMessageConversionException(MessageConversionException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.failure("MESSAGE_CONVERSION_FAILURE", e.getMessage()));
    }

    @ExceptionHandler(MessageHandlingException.class)
    public ResponseEntity<Response<Void>> handleMessageHandlingException(MessageHandlingException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.failure("MESSAGE_HANDLING_FAILURE", e.getMessage()));
    }
}