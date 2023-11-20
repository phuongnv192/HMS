package com.module.project.exception.handler;

import com.module.project.exception.HmsErrorCode;
import com.module.project.exception.HmsResponse;
import com.module.project.exception.HmsException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@Slf4j
@Order(1)
public class HmsExceptionHandler {

    @ExceptionHandler({HmsException.class})
    public ResponseEntity<HmsResponse<Object>> hmsExceptionHandler(HmsException e) {
        log.error("hmsExceptionHandler: HmsException: {}", ExceptionUtils.getStackTrace(e));
        return new ResponseEntity<>(e.getHmsResponse(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<HmsResponse<Object>> exceptionHandler(Exception e) {
        log.error("exceptionHandler: Exception: {}", ExceptionUtils.getStackTrace(e));
        HmsResponse<Object> responseDTO = new HmsResponse<>();
        responseDTO.setCode(HmsErrorCode.INTERNAL_SERVER_ERROR);
        responseDTO.setMessage("Internal Server Error");
        return new ResponseEntity<>(responseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({HttpMediaTypeNotSupportedException.class,
            HttpMediaTypeNotAcceptableException.class, MissingPathVariableException.class,
            MissingServletRequestParameterException.class, ServletRequestBindingException.class,
            ConversionNotSupportedException.class, TypeMismatchException.class, HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            MissingServletRequestPartException.class, BindException.class, NoHandlerFoundException.class,
            AsyncRequestTimeoutException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<HmsResponse<Object>> otherBadRequestExceptionHandler(Exception e) {
        log.error("otherBadRequestExceptionHandler: Exception: {}", ExceptionUtils.getStackTrace(e));
        HmsResponse<Object> responseDTO = new HmsResponse<>();
        responseDTO.setCode(HmsErrorCode.INVALID_REQUEST);
        responseDTO.setMessage("Invalid Request");
        return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public void methodNotAllowExceptionResponse() {
        // use response default
    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public void methodForbiddenExceptionResponse() {
        // use response default
    }
}