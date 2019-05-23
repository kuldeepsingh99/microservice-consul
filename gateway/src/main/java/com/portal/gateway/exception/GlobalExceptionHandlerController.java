package com.portal.gateway.exception;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global Exception Handler
 * @author Kuldeep
 *
 */
@RestControllerAdvice
public class GlobalExceptionHandlerController {

	private static final Logger LOG = Logger.getLogger(GlobalExceptionHandlerController.class.getName());

    @ExceptionHandler(value = { CustomException.class })
    @SuppressWarnings("unchecked")
	@ResponseStatus(UNAUTHORIZED)
    public String handleCustomException(HttpServletResponse res, CustomException ex) throws IOException {
    	LOG.info(ex.getMessage());
    	JSONObject obj = new JSONObject();
    	obj.put("message", ex.getMessage());
    	res.setStatus(ex.getHttpStatus().value());
		return obj.toJSONString();
    }
    
    @SuppressWarnings("unchecked")
	@ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(HttpServletResponse res, AccessDeniedException e) throws IOException {
    	LOG.info(e.getMessage());
    	JSONObject obj = new JSONObject();
    	obj.put("message", "Access denied");
    	res.setStatus(HttpStatus.FORBIDDEN.value());
		return obj.toJSONString();
    }

    @SuppressWarnings("unchecked")
	@ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(HttpServletResponse res, IllegalArgumentException e) throws IOException {
    	LOG.info(e.getMessage());
    	JSONObject obj = new JSONObject();
    	obj.put("message", "Something went wrong");
    	res.setStatus(HttpStatus.BAD_REQUEST.value());
		return obj.toJSONString();
    }

    @SuppressWarnings("unchecked")
	@ExceptionHandler(Exception.class)
    public String handleException(HttpServletResponse res, Exception e) throws IOException {
    	LOG.info(e.getMessage());
    	JSONObject obj = new JSONObject();
    	obj.put("message", "Something went wrong");
    	res.setStatus(HttpStatus.BAD_REQUEST.value());
		return obj.toJSONString();
    }
}
