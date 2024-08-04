package com.nhh203.productservice.exception;


import com.nhh203.productservice.exception.payload.ExceptionMessage;
import com.nhh203.productservice.exception.wrapper.*;
import com.nhh203.productservice.viewmodel.error.ErrorVm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ApiExceptionHandler {
	private static final String ERROR_LOG_FORMAT = "Error: URI: {}, ErrorCode: {}, Message: {}";


	@ExceptionHandler(value = {
			MethodArgumentNotValidException.class,
			HttpMessageNotReadableException.class,
	})
	public <T extends BindException> ResponseEntity<ExceptionMessage> handleValidationException(final T e) {
		log.info("**ApiExceptionHandler controller, handle validation exception*\n");
		final var badRequest = HttpStatus.BAD_REQUEST;

		return new ResponseEntity<>(
				ExceptionMessage.builder()
						.message("*" + Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage() + "!**")
						.httpStatus(badRequest)
						.timestamp(ZonedDateTime
								.now(ZoneId.systemDefault()))
						.build(), badRequest);
	}

	@ExceptionHandler(value = {CategoryNotFoundException.class, ProductNotFoundException.class,})
	public <T extends RuntimeException> ResponseEntity<ExceptionMessage> handleApiRequestException(final T e) {
		log.info("**ApiExceptionHandler controller, handle API request*\n");
		final var badRequest = HttpStatus.BAD_REQUEST;

		return new ResponseEntity<>(
				ExceptionMessage.builder()
						.message("#### " + e.getMessage() + "! ####")
						.httpStatus(badRequest)
						.timestamp(ZonedDateTime
								.now(ZoneId.systemDefault()))
						.build(), badRequest);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGenericException(Exception ex) {
		log.error("Unexpected error: {}", ex.getMessage(), ex);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
	}


	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorVm> handleBadRequestException(BadRequestException ex, WebRequest request) {
		String message = ex.getMessage();
		ErrorVm errorVm = new ErrorVm(HttpStatus.BAD_REQUEST.toString(), HttpStatus.BAD_REQUEST.getReasonPhrase(), message);
		return ResponseEntity.badRequest().body(errorVm);
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorVm> handleNotFoundException(NotFoundException ex, WebRequest request) {
		String message = ex.getMessage();
		ErrorVm errorVm = new ErrorVm(HttpStatus.NOT_FOUND.toString(), HttpStatus.NOT_FOUND.getReasonPhrase(), message);
		log.warn(ERROR_LOG_FORMAT, this.getServletPath(request), 404, message);
		log.debug(ex.toString());
		return new ResponseEntity<>(errorVm, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(DuplicatedException.class)
	public ResponseEntity<ErrorVm> handleDuplicatedException(DuplicatedException ex) {
		String message = ex.getMessage();
		ErrorVm errorVm = new ErrorVm(HttpStatus.BAD_REQUEST.toString(), HttpStatus.BAD_REQUEST.getReasonPhrase(), message);
		return new ResponseEntity<>(errorVm, HttpStatus.BAD_REQUEST);
	}


	private String getServletPath(WebRequest webRequest) {
		ServletWebRequest servletRequest = (ServletWebRequest) webRequest;
		return servletRequest.getRequest().getServletPath();
	}

}
