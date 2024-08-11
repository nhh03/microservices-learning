package com.yas.search.exception;

import com.yas.search.viewmodel.error.ErrorVm;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler {

  private static final String ERROR_LOG_FORMAT = "Error: URI: {}, ErrorCode: {}, Message: {}";

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorVm> handleNotFoundException(NotFoundException ex, WebRequest request) {
    String message = ex.getMessage();
    ErrorVm errorVm = new ErrorVm(HttpStatus.NOT_FOUND.toString(),
        HttpStatus.NOT_FOUND.getReasonPhrase(), message);
    log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), 404, message);
    log.debug(ex.toString());
    return new ResponseEntity<>(errorVm, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorVm> handleBadRequestException(BadRequestException ex,
      WebRequest request) {
    String message = ex.getMessage();
    ErrorVm errorVm = new ErrorVm(HttpStatus.BAD_REQUEST.toString(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(), message);
    return ResponseEntity.badRequest().body(errorVm);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    List<String> errors = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + " " + error.getDefaultMessage())
        .toList();

    ErrorVm errorVm = new ErrorVm(HttpStatus.BAD_REQUEST.toString(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(), "Request information is not valid", errors);
    return ResponseEntity.badRequest().body(errorVm);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
    List<String> errors = new ArrayList<>();
    for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
      errors.add(violation.getRootBeanClass().getName() + " " +
          violation.getPropertyPath() + ": " + violation.getMessage());
    }

    ErrorVm errorVm = new ErrorVm(HttpStatus.BAD_REQUEST.toString(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(), "Request information is not valid", errors);
    return ResponseEntity.badRequest().body(errorVm);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<?> handleDataIntegrityViolationException(
      DataIntegrityViolationException e) {
    String message = NestedExceptionUtils.getMostSpecificCause(e).getMessage();
    ErrorVm errorVm = new ErrorVm(HttpStatus.BAD_REQUEST.toString(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(), message);
    return ResponseEntity.badRequest().body(errorVm);
  }

  @ExceptionHandler(DuplicatedException.class)
  protected ResponseEntity<Object> handleDuplicated(DuplicatedException e) {
    ErrorVm errorVm = new ErrorVm(HttpStatus.BAD_REQUEST.toString(),
        HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
    return ResponseEntity.badRequest().body(errorVm);
  }

  private String getServletPath(WebRequest webRequest) {
    ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
    return servletRequest.getRequest().getServletPath();
  }
}
