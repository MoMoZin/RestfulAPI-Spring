package booking;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

interface BookingRepository extends JpaRepository<Booking, Long> {
	
	List<Booking> findBookingByStatus(Status status);

	Booking findBookingByArrivalDate(LocalDate arrivalDate);

	Booking findBookingByDepartureDate(LocalDate departureDate);
 
}