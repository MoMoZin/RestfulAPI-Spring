package booking;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class LoadDatabase {
	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	  @Bean
	  public CommandLineRunner initDatabase(BookingRepository bookingRepository) {

	    return args -> {
	    	bookingRepository.save(new Booking("Harry", "Potter", "harrypotter@gmail.com",LocalDate.parse("2020-12-01", DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalDate.parse("2020-12-03", DateTimeFormatter.ofPattern("yyyy-MM-dd")), Status.COMPLETED));
	    	bookingRepository.save(new Booking("Ron", "Weasley", "ronweasley@gmail.com",LocalDate.parse("2020-12-08", DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalDate.parse("2020-12-10", DateTimeFormatter.ofPattern("yyyy-MM-dd")), Status.ACTIVE));
	    	bookingRepository.save(new Booking("Hermione", "Granger", "hermionegranger@gmail.com",LocalDate.parse("2020-12-13", DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalDate.parse("2020-12-15", DateTimeFormatter.ofPattern("yyyy-MM-dd")), Status.ACTIVE));
	    	bookingRepository.save(new Booking("Rose", "Galler", "rosegaller@gmail.com",LocalDate.parse("2020-12-15", DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalDate.parse("2020-12-17", DateTimeFormatter.ofPattern("yyyy-MM-dd")), Status.ACTIVE));
	    	bookingRepository.save(new Booking("Monica", "Green", "monicagreen@gmail.com",LocalDate.parse("2020-12-19", DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalDate.parse("2020-12-20", DateTimeFormatter.ofPattern("yyyy-MM-dd")), Status.ACTIVE));

	    	bookingRepository.findAll().forEach(booking -> log.info("Preloaded " + booking));

	    };
	  }

}
