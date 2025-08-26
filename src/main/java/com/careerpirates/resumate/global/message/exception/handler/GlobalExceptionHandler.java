package com.careerpirates.resumate.global.message.exception.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.careerpirates.resumate.global.message.exception.core.BusinessException;
import com.careerpirates.resumate.global.message.exception.core.ErrorCode;
import com.careerpirates.resumate.global.message.exception.core.ErrorResponse;
import com.careerpirates.resumate.global.message.exception.core.GlobalErrorCode;
import com.careerpirates.resumate.global.redis.RedisErrorCode;
import com.careerpirates.resumate.global.utils.GlobalLogger;

import io.jsonwebtoken.io.SerializationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 접근 권한 예외 처리 (Spring Security)
	 * @param e {@link AccessDeniedException}
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {

		ErrorResponse response = ErrorResponse.of(GlobalErrorCode.FORBIDDEN_ACCESS, e.getMessage());

		return ResponseEntity
			.status(HttpStatus.FORBIDDEN)
			.body(response);
	}

	/**
	 * 요청 URI에 대한 올바르지 않은 HTTP METHOD 처리
	 * @param e {@link HttpRequestMethodNotSupportedException}
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ErrorResponse> handleMethodNotAllowedException(HttpRequestMethodNotSupportedException e) {

		GlobalLogger.warn("Request method not supported: {}", e.getMessage());

		return getErrorResponse(GlobalErrorCode.METHOD_NOT_ALLOWED);
	}

	/**
	 * RequestHeader 누락 처리
	 * @param e {@link MissingRequestHeaderException}
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler(MissingRequestHeaderException.class)
	public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException e) {

		GlobalLogger.warn("Request header missing: {}", e.getMessage());

		return getErrorResponse(GlobalErrorCode.MISSING_HEADER);
	}

	/**
	 * {@RequestBody} 누락 처리
	 * @param e {@link HttpMessageNotReadableException}
	 * @param request {@link HttpServletRequest}
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e,
		HttpServletRequest request) {

		Map<String, String> requestInfo = Map.of(
			"path", "uri=" + request.getRequestURI(),
			"method", request.getMethod());
		GlobalLogger.warn("Request body missing or invalid: {}", e.getMessage());

		ErrorResponse response = ErrorResponse.of(GlobalErrorCode.MISSING_REQUEST_BODY, requestInfo);

		return ResponseEntity
			.badRequest()
			.body(response);
	}

	/**
	 * {@RequestBody} 유효성 검사 실패 처리
	 * @param e {@link MethodArgumentNotValidException}
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {

		List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
		ErrorResponse response = ErrorResponse.of(GlobalErrorCode.INVALID_REQUEST_BODY, fieldErrors);

		GlobalLogger.warn("Validation failed: {}", response.getDetails());

		return ResponseEntity
			.badRequest()
			.body(response);
	}

	/**
	 * RequestParam 누락 처리
	 * @param e MissingServletRequestParameterException
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(
		MissingServletRequestParameterException e) {

		String detail = String.format("필수 파라미터 '%s'가 누락되었습니다.", e.getParameterName());
		ErrorResponse response = ErrorResponse.of(GlobalErrorCode.MISSING_REQUEST_PARAM, detail);

		GlobalLogger.warn("Missing request parameter: {}", e.getParameterName());

		return ResponseEntity
			.badRequest()
			.body(response);
	}

	/**
	 * PathVariable, RequestParam 유효성 검사 실패 처리
	 * @param e ConstraintViolationException
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException e) {

		Map<String, String> errors = getErrors(e.getConstraintViolations());
		ErrorResponse errorResponse = ErrorResponse.of(GlobalErrorCode.INVALID_REQUEST_PARAM, errors);

		GlobalLogger.warn("Constraint violation: {}", errors);

		return ResponseEntity
			.badRequest()
			.body(errorResponse);
	}

	private Map<String, String> getErrors(Set<ConstraintViolation<?>> violations) {
		Map<String, String> errors = new HashMap<>();
		violations.forEach(violation -> {
			String fieldName = violation.getPropertyPath().toString();
			String message = violation.getMessage();
			errors.put(fieldName, message);
		});

		return errors;
	}

	/**
	 * 비즈니스 예외 처리
	 * @param e {@link BusinessException}
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBaseException(BusinessException e) {
		logBusinessException(e);

		return getErrorResponse(e.getErrorCode());
	}

	/**
	 * 비즈니스 예외 로깅 메서드
	 * @param e {@link BusinessException}
	 */
	private void logBusinessException(BusinessException e) {
		if (e.getErrorCode().getStatus().is5xxServerError()) {
			GlobalLogger.error("Business exception occurred: ", e.getMessage());
			return;
		}
		GlobalLogger.warn("Business exception occurred: ", e.getMessage());
	}

	/**
	 * 잘못된 인수 전달 시 예외 처리
	 * @param e {@link IllegalArgumentException}
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {

		ErrorResponse errorResponse = ErrorResponse.of(GlobalErrorCode.INVALID_REQUEST_PARAM);
		GlobalLogger.warn("Invalid argument: {}", e.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	/**
	 * 예상하지 못한 모든 예외 처리
	 * @param e {@link Exception}
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(Exception e, WebRequest request) {
		ErrorResponse response = ErrorResponse.of(
			GlobalErrorCode.INTERNAL_SERVER_ERROR,
			Map.of("path", request.getDescription(false))
		);

		GlobalLogger.error("Unexpected error occurred", e);
		return ResponseEntity
			.internalServerError()
			.body(response);
	}

	/**
	 * Redis: 연결 실패 예외 처리
	 * @param e {@link RedisConnectionFailureException}
	 * @return {@link ResponseEntity}
	 */
	@ExceptionHandler(RedisConnectionFailureException.class)
	public ResponseEntity<ErrorResponse> handleRedisConnectionFailure(RedisConnectionFailureException e) {
		ErrorResponse errorResponse = ErrorResponse.of(RedisErrorCode.REDIS_CONNECT_FAIL);
		GlobalLogger.warn("Redis connection failure: {}", e.getMessage());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}

	/**
	 * Redis: 직렬화 및 역직렬화 예외 처리
	 * */
	@ExceptionHandler(SerializationException.class)
	public ResponseEntity<ErrorResponse> handleRedisSerializationException(Exception ex) {
		ErrorResponse errorResponse = ErrorResponse.of(RedisErrorCode.SERIALIZATION_FAIL);
		GlobalLogger.warn("Redis serialization failure: {}", ex.getMessage());

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}

	/**
	 * Redis: 잘못된 API 사용 예외 처리
	 * */
	@ExceptionHandler(InvalidDataAccessApiUsageException.class)
	public ResponseEntity<ErrorResponse> handleRedisApiUsageException(InvalidDataAccessApiUsageException ex) {
		ErrorResponse errorResponse = ErrorResponse.of(RedisErrorCode.INVALID_DATA_ACCESS);
		GlobalLogger.warn("Redis API usage failure: {}", ex.getMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	private static ResponseEntity<ErrorResponse> getErrorResponse(ErrorCode errorCode) {
		ErrorResponse response = ErrorResponse.of(errorCode);

		return ResponseEntity
			.status(errorCode.getStatus())
			.body(response);
	}
}
