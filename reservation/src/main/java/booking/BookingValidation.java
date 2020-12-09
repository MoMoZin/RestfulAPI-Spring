package booking;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Exception.BookingExceptions;

public class BookingValidation {
	

	private static final Logger log = LoggerFactory.getLogger(BookingValidation.class);
	
	
	void availabilityValidation(Booking booking, BookingRepository bookingRepository)
	{
		  String errorMessage="";
		  LocalDate departureDate =booking.getDepartureDate();  	  

		  //Booking date availability validation
		  LocalDate startDate =booking.getArrivalDate();
		  List<LocalDate> dateList = new ArrayList<LocalDate>();
		  while (!startDate.isAfter(departureDate.minusDays(1))) {
			  dateList.add(startDate);	
			  Booking existingBooking = bookingRepository.findBookingByArrivalDate(startDate);
			  if(existingBooking!=null && existingBooking.getStatus().equals(Status.ACTIVE))
			  {
					  errorMessage="Selected booking date is not available!";
					  throw new BookingExceptions(errorMessage);
			  }
			  startDate = startDate.plusDays(1);
		  }		  	  
		  
		
	}
	

	void availabilityValidationForUpdate(Booking booking, BookingRepository bookingRepository)
	{
		  String errorMessage="";
		  LocalDate departureDate =booking.getDepartureDate();  	  

		  //Booking date availability validation
		  LocalDate startDate =booking.getArrivalDate();
		  List<LocalDate> dateList = new ArrayList<LocalDate>();
		  while (!startDate.isAfter(departureDate.minusDays(1))) {
			  dateList.add(startDate);	
			  Booking existingBooking = bookingRepository.findBookingByArrivalDate(startDate);
			  log.info("existingBooking >> " + existingBooking);
			  log.info("booking >> " + booking);
			  if(existingBooking!=null && existingBooking.getStatus().equals(Status.ACTIVE) && !existingBooking.getId().equals(booking.getId()))
			  {
					  errorMessage="Selected booking date is not available!!";
					  throw new BookingExceptions(errorMessage);
			  }
			  startDate = startDate.plusDays(1);
		  }		  	  
		  
		
	}


	void arrivalDateValidation(LocalDate arrivalDate, BookingRepository bookingRepository)
	{
		  LocalDate today = LocalDate.now();
		  String errorMessage="";
		  LocalDate maxReserveDate = today.plusMonths(1);	
		  
		  //Selected date validation
		  if(arrivalDate.isBefore(today) || arrivalDate.isEqual(today))
		  {
			  errorMessage="Arrival date must be minimum one day ahead!";
			  throw new BookingExceptions(errorMessage);
		  }
		  
		  if(arrivalDate.isAfter(maxReserveDate))
		  {
			  errorMessage="Reservation can only be done for a month in advance!";
			  throw new BookingExceptions(errorMessage);
		  }
	}
	
	void departureDateValidation(LocalDate departureDate,LocalDate arrivalDate, BookingRepository bookingRepository)
	{
		  String errorMessage="";

		 if(ChronoUnit.DAYS.between(arrivalDate, departureDate)>3)
		  {
			  errorMessage="Unable to reserve more than three days!";
			  throw new BookingExceptions(errorMessage);
		  }
		  
		 if(departureDate.isBefore(arrivalDate) )
		  {
			  errorMessage="Departure date can not be later than arrival date!";
			  throw new BookingExceptions(errorMessage);
		  }
		  
		 if(departureDate.isEqual(arrivalDate))
		  {
			  errorMessage="Duration must be at least one day!";
			  throw new BookingExceptions(errorMessage);
		  }
	}	

	
	void cancelDateValidation(LocalDate arrivalDate, BookingRepository bookingRepository)
	{
		  LocalDate today = LocalDate.now();
		  String errorMessage="";

		 if(arrivalDate.isBefore(today) || arrivalDate.isEqual(today) )
		  {
			  errorMessage="Unable to cancel the reservation that is already active!";
			  throw new BookingExceptions(errorMessage);
		  }		  
	}
	
	//validation for booking under same user for more than 3 days in a row
	void dateRangeValidation(Booking booking, BookingRepository bookingRepository)
	{
		LocalDate startDate = booking.getArrivalDate();
		Booking existingBooking = bookingRepository.findBookingByDepartureDate(startDate);
		 String errorMessage="";
		if(existingBooking != null )
		{
			if(existingBooking.getFirstName().equals(booking.getFirstName()) && existingBooking.getLastName().equals(booking.getLastName()) && existingBooking.getEmail().equals(booking.getEmail()))
			{
				int totalDate = (int) ChronoUnit.DAYS.between(existingBooking.getArrivalDate(), existingBooking.getDepartureDate());
				log.info("totalDate>>" + totalDate);
				totalDate += (int) ChronoUnit.DAYS.between(booking.getArrivalDate(), booking.getDepartureDate());
						log.info("totalDate>>" + totalDate);
				if(totalDate>3)
				{
					  errorMessage="Unable to book more than three day in a row!";
					  throw new BookingExceptions(errorMessage);
				}				
			}
		}
		
		LocalDate endDate = booking.getDepartureDate();
		Booking existingBookingEnd = bookingRepository.findBookingByArrivalDate(endDate);
		
		if(existingBookingEnd != null )
		{
			if(existingBookingEnd.getFirstName().equals(booking.getFirstName()) && existingBookingEnd.getLastName().equals(booking.getLastName()) && existingBookingEnd.getEmail().equals(booking.getEmail()))
			{
				int totalDate = (int) ChronoUnit.DAYS.between(existingBookingEnd.getArrivalDate(), existingBookingEnd.getDepartureDate());
				log.info("totalDate>>" + totalDate);
				totalDate += (int) ChronoUnit.DAYS.between(booking.getArrivalDate(), booking.getDepartureDate());
						log.info("totalDate>>" + totalDate);
				if(totalDate>3)
				{
					  errorMessage="Unable to book more than three day in a row!";
					  throw new BookingExceptions(errorMessage);
				}				
			}
		}		
		
	}
}
