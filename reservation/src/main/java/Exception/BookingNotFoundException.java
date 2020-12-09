package Exception;

public class BookingNotFoundException extends RuntimeException {

 public BookingNotFoundException(Long id) {
    super("Booking ID " + id + " does not exists!");
  }
}