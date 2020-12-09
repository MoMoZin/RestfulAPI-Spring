package booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import Exception.BookingExceptions;

@ControllerAdvice
public class BookingAdvice {
	@ResponseBody
	@ExceptionHandler(BookingExceptions.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	  String bookingNotFoundHandler(BookingExceptions ex) {
	    return ex.getMessage();
	  }	  

}
