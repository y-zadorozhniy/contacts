package dev.domain.contacts.exceptions;

import dev.domain.contacts.dto.response.ServerResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Log4j2
public class ResponseExceptionHandler {

    @ExceptionHandler(
            value = {
                    IllegalArgumentException.class
            }
    )
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ServerResponse handleBadRequestErrors(Exception exception) {
        log.error("[Bad request] {}", exception.getMessage(), exception);
        return ServerResponse.builder()
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(
            value = {
                    NotFoundException.class
            }
    )
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ServerResponse handleNotFoundErrors(Exception exception) {
        log.info("[Not found] {}", exception.getMessage(), exception);
        return ServerResponse.builder()
                .message(exception.getMessage())
                .build();
    }

    @ExceptionHandler(
            value = {
                    ServerErrorException.class
            }
    )
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ServerResponse handleServerErrors(Exception exception) {
        log.info("[Server error] {}", exception.getMessage(), exception);
        return ServerResponse.builder()
                .message(exception.getMessage())
                .build();
    }
}
