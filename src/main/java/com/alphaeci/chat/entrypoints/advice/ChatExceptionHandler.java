package com.alphaeci.chat.entrypoints.advice;

import com.alphaeci.chat.domain.exceptions.ChatRoomNotFoundException;
import com.alphaeci.chat.domain.exceptions.InvalidMessageException;
import com.alphaeci.chat.domain.exceptions.MessageNotFoundException;
import com.alphaeci.chat.domain.exceptions.MessageReportNotFoundException;
import com.alphaeci.chat.domain.exceptions.UserNotMemberException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ChatExceptionHandler {

    @ExceptionHandler({ChatRoomNotFoundException.class, MessageNotFoundException.class, MessageReportNotFoundException.class})
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(UserNotMemberException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(UserNotMemberException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(InvalidMessageException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(InvalidMessageException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
    }
}
