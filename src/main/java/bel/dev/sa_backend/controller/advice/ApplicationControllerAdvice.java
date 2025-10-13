package bel.dev.sa_backend.controller.advice;


import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import bel.dev.sa_backend.dto.ErrorEntity;

import jakarta.persistence.EntityNotFoundException;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.AccessDeniedException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;



@Slf4j
@RestControllerAdvice
public class ApplicationControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({EntityNotFoundException.class})
    public @ResponseBody ErrorEntity HandleException(EntityNotFoundException exception){
        return new ErrorEntity(null, exception.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({RuntimeException.class})
    public @ResponseBody ErrorEntity runtimeException(RuntimeException exception){
        return new ErrorEntity("401", exception.getMessage());
    }

@ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({BadCredentialsException.class})
    public @ResponseBody ProblemDetail badCredentiaProblemDetail(final BadCredentialsException badCredentialsException){
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        problemDetail.setProperty("erreur credentials", "Nous n'avons pas pu vous identifier");
        return problemDetail;
    }


    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({AccessDeniedException.class})
    public @ResponseBody ProblemDetail accessDeniedException(final AccessDeniedException accessDeniedException){
        final ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "Identifiants invalides");
        problemDetail.setProperty("erreur acces", "Vos droits ne vous permettent pas d'effectuer cette action ");
        return problemDetail;
    }


}
