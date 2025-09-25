package io.github.aicyi.example.boot.config;

import io.github.aicyi.commons.core.exception.MessageSendException;
import io.github.aicyi.commons.lang.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackages = {"io.github.aicyi.example"})
public class MessagingExceptionHandler {

    @ExceptionHandler(MessageSendException.class)
    public ResponseEntity<Response> handleMessageSendException(MessageSendException e) {
        Response response = Response.builder().withError("MESSAGE_SEND_FAILURE", e.getMessage()).build();
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }

    @ExceptionHandler(MessageConversionException.class)
    public ResponseEntity<Response> handleMessageConversionException(MessageConversionException e) {
        Response response = Response.builder().withError("MESSAGE_CONVERSION_FAILURE", e.getMessage()).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MessageHandlingException.class)
    public ResponseEntity<Response> handleMessageHandlingException(MessageHandlingException e) {
        Response response = Response.builder().withError("MESSAGE_HANDLING_FAILURE", e.getMessage()).build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}