package booking;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class BookingAssembler implements RepresentationModelAssembler<Booking, EntityModel<Booking>> {
	
	@Override
	  public EntityModel<Booking> toModel(Booking booking) {


	    EntityModel<Booking> bookingModel = EntityModel.of(booking,
	        linkTo(methodOn(BookingController.class).getBookingById(booking.getId())).withSelfRel(),
	        linkTo(methodOn(BookingController.class).getAllBooking()).withRel("Bookings"));
	    
	    if(booking.getStatus().equals(Status.ACTIVE)){
	    	bookingModel.add(linkTo(methodOn(BookingController.class).cancelBooking(booking.getId())).withRel("Cancel Booking"));
	    	bookingModel.add(linkTo(methodOn(BookingController.class).modifyBooking(booking.getId(),booking)).withRel("Modify Booking"));
	    	bookingModel.add(linkTo(methodOn(BookingController.class).completeBooking(booking.getId())).withRel("Complete Booking"));
	    }

	    return bookingModel;
	  }

}
