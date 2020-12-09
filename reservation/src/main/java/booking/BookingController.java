package booking;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import Exception.BookingExceptions;
import Exception.BookingNotFoundException;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class BookingController {

	private final BookingRepository bookingRepository;
	private final BookingAssembler bookingAssembler;
	private final LocalDateAssembler localDateAssembler;
	
	private BookingValidation v;

	private static final Logger log = LoggerFactory.getLogger(BookingController.class);
	
	public BookingController(BookingRepository bookingRepository, BookingAssembler bookingAssembler,LocalDateAssembler localDateAssembler) {
		super();
		this.bookingRepository = bookingRepository;
		this.bookingAssembler = bookingAssembler;
		this.localDateAssembler = localDateAssembler;
	}

	//get all bookings
	@GetMapping("/bookings")
	  public CollectionModel<EntityModel<Booking>> getAllBooking() {

	    List<EntityModel<Booking>> bookings = bookingRepository.findAll().stream() //
	        .map(bookingAssembler::toModel) //
	        .collect(Collectors.toList());

	    return CollectionModel.of(bookings, //
	        linkTo(methodOn(BookingController.class).getAllBooking()).withSelfRel());
	  }


	//get booking by ID
	  @GetMapping("/bookings/{id}")
	  public EntityModel<Booking> getBookingById(@Valid @PathVariable Long id) {

		  Booking booking = bookingRepository.findById(id) //
	        .orElseThrow(() -> new BookingNotFoundException(id));

	    return bookingAssembler.toModel(booking);
	  }
	
	  //get reservation availability of default one month period
	  @GetMapping("/checkAvailability")
	  public CollectionModel<EntityModel<LocalDate>> checkAvailability() {	
		  
		  LocalDate start = LocalDate.now();
		  LocalDate end = LocalDate.now().plusMonths(1);
		  List<LocalDate> totalDates = new ArrayList<>();
		  while (!start.isAfter(end)) {
		      totalDates.add(start);
		      start = start.plusDays(1);
		  }
		  log.info("totalDates " + totalDates);
		  
		  List<Booking> activeBookings = bookingRepository.findBookingByStatus(Status.ACTIVE);
		  log.info("activeBookings " + activeBookings);
		  
		  List<LocalDate> bookedDates = new ArrayList<>();
		  
		  for (Booking b : activeBookings)
		  {
			  LocalDate bookingStart = b.getArrivalDate();
			  
			  while (!bookingStart.isAfter(b.getDepartureDate())) {
				  bookedDates.add(bookingStart);
				  totalDates.remove(bookingStart);
			      bookingStart = bookingStart.plusDays(1);
			  }
		  }

		  if(totalDates.size()<=0)
		  {
			  throw new BookingExceptions("No available date for one month from now!");
		  }

		  log.info("bookedDates " + bookedDates);
		  log.info("totalDates " + totalDates);
		 
		  List<EntityModel<LocalDate>> availableDates = totalDates.stream() 
			        .map(localDateAssembler::toModel) 
			        .collect(Collectors.toList());
				  
		  return CollectionModel.of(availableDates, 
			        linkTo(methodOn(BookingController.class).getAllBooking()).withSelfRel());	    
	  }
	  
	//get reservation availability with given date range
	  @GetMapping("/checkAvailabilitywithRange")
	  @ResponseBody
	  public CollectionModel<EntityModel<LocalDate>> checkAvailability(@Valid @RequestParam(required = true) String startDate, @Valid @RequestParam(required = true) String endDate) {	
		  

		  log.info("startDate" + startDate);
		  log.info("endDate " + endDate);
		  
		  LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		  LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		  List<LocalDate> totalDates = new ArrayList<>();
		  while (!start.isAfter(end)) {
		      totalDates.add(start);
		      start = start.plusDays(1);
		  }
		  log.info("totalDates " + totalDates);
		  
		  List<Booking> activeBookings = bookingRepository.findBookingByStatus(Status.ACTIVE);
		  log.info("activeBookings " + activeBookings);
		  
		  List<LocalDate> bookedDates = new ArrayList<>();
		  
		  for (Booking b : activeBookings)
		  {
			  LocalDate bookingStart = b.getArrivalDate();
			  
			  while (!bookingStart.isAfter(b.getDepartureDate())) {
				  bookedDates.add(bookingStart);
				  totalDates.remove(bookingStart);
			      bookingStart = bookingStart.plusDays(1);
			  }
		  }

		  log.info("bookedDates " + bookedDates);
		  log.info("totalDates " + totalDates);
		  if(totalDates.size()<=0)
		  {
			  throw new BookingExceptions("No available date for provided date range!");
		  }
		 
		  List<EntityModel<LocalDate>> availableDates = totalDates.stream() 
			        .map(localDateAssembler::toModel) 
			        .collect(Collectors.toList());
				  
		  return CollectionModel.of(availableDates, 
				  linkTo(methodOn(BookingController.class).getAllBooking()).withSelfRel());	    
	  }
	  
	  //Create new booking
	  @PostMapping("/reserve")
	  ResponseEntity<EntityModel<Booking>> newBooking(@Valid @RequestBody Booking booking)  {

		  log.info("new bookign " + booking);
		  
		  v= new BookingValidation();
		  v.availabilityValidation(booking, bookingRepository);
		  v.arrivalDateValidation(booking.getArrivalDate(), bookingRepository);
		  v.departureDateValidation(booking.getDepartureDate(), booking.getArrivalDate(), bookingRepository);
		  v.dateRangeValidation(booking, bookingRepository);

		  booking.setStatus(Status.ACTIVE);		  
		  Booking newBooking = bookingRepository.save(booking);
		  log.info("new bookign " + newBooking);

		    return ResponseEntity //
		        .created(linkTo(methodOn(BookingController.class).getBookingById(newBooking.getId())).toUri()) //
		        .body(bookingAssembler.toModel(newBooking));	 
		 		        
	  }
	  
	  //Modify existing booking
	  @PutMapping("/reserve/{id}/modify")
	  ResponseEntity<?> modifyBooking(@PathVariable Long id,  @RequestBody Booking booking){
		  
		  booking.setId(id);
		  Booking existingBooking = bookingRepository.findById(id) 
			        .orElseThrow(() -> new BookingNotFoundException(id));
		  
		  if(existingBooking.getStatus().equals(Status.ACTIVE)) {
			  v= new BookingValidation();
			  if(booking.getArrivalDate()!=null && !booking.getArrivalDate().equals(existingBooking.getArrivalDate()) ) 
			  {					 
				  v.arrivalDateValidation(booking.getArrivalDate(), bookingRepository);
				  existingBooking.setArrivalDate(booking.getArrivalDate());
			  }			  
			  
			  if(booking.getDepartureDate()!=null &&  !booking.getDepartureDate().equals(existingBooking.getDepartureDate()))
			  {
				  v.departureDateValidation(booking.getDepartureDate(), booking.getArrivalDate(), bookingRepository);
				  existingBooking.setDepartureDate(booking.getDepartureDate());
			  }
			  v.availabilityValidationForUpdate(booking,bookingRepository);

			  if(booking.getFirstName()!=null && !booking.getFirstName().equals(existingBooking.getFirstName()) && !booking.getFirstName().equals(""))
			  {				  
				  existingBooking.setFirstName(booking.getFirstName());
			  }
			  
			  if(booking.getLastName()!=null && !booking.getLastName().equals(existingBooking.getLastName()) && !booking.getLastName().equals(""))
			  {				  
				  existingBooking.setLastName(booking.getLastName());
			  }
			  
			  if(booking.getEmail()!=null &&  !booking.getEmail().equals(existingBooking.getEmail()) && !booking.getEmail().equals(""))
			  {				  
				  existingBooking.setEmail(booking.getEmail());
			  }
			  			  
		      return ResponseEntity.ok(bookingAssembler.toModel(bookingRepository.save(existingBooking)));
		    }

		    return ResponseEntity 
		        .status(HttpStatus.METHOD_NOT_ALLOWED) 
		        .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) 
		        .body(Problem.create() 
		            .withTitle("Method not allowed") 
		            .withDetail("You can't modify a reservation that is in the " + existingBooking.getStatus() + " status!"));
		  }
	  
	//cancel existing booking 
	  @PutMapping("/reserve/{id}/cancel")
	  ResponseEntity<?> cancelBooking(@Valid @PathVariable Long id){
		  Booking existingBooking = bookingRepository.findById(id) 
			        .orElseThrow(() -> new BookingNotFoundException(id));

		  if(existingBooking.getStatus().equals(Status.ACTIVE)) {
			  v=new BookingValidation();
			  v.cancelDateValidation(existingBooking.getArrivalDate(), bookingRepository);
			  
			  existingBooking.setStatus(Status.CANCELLED);

			    return ResponseEntity.ok(bookingAssembler.toModel(bookingRepository.save(existingBooking)));
		  }
		  return ResponseEntity 
			        .status(HttpStatus.METHOD_NOT_ALLOWED) 
			        .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) 
			        .body(Problem.create() 
			            .withTitle("Method not allowed") 
			            .withDetail("You can't complete a reservation that is in the " + existingBooking.getStatus() + " status!"));
		  

		  }
	  
	//complete existing booking -- check out
	  @PutMapping("/reserve/{id}/complete")
	  ResponseEntity<?> completeBooking(@Valid @PathVariable Long id) {
		  Booking existingBooking = bookingRepository.findById(id) 
			        .orElseThrow(() -> new BookingNotFoundException(id));

		  if(existingBooking.getStatus().equals(Status.ACTIVE)) {
			  existingBooking.setStatus(Status.COMPLETED);

			    return ResponseEntity.ok(bookingAssembler.toModel(bookingRepository.save(existingBooking)));
		  }
		  return ResponseEntity 
			        .status(HttpStatus.METHOD_NOT_ALLOWED) 
			        .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) 
			        .body(Problem.create() 
			            .withTitle("Method not allowed") 
			            .withDetail("You can't complete a reservation that is in the " + existingBooking.getStatus() + " status!"));	  

		  }
	 
}
