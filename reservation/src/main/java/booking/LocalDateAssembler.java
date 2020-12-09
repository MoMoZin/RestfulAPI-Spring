package booking;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class LocalDateAssembler  implements RepresentationModelAssembler<LocalDate, EntityModel<LocalDate>> {

	@Override
	  public EntityModel<LocalDate> toModel(LocalDate localDate) {


	    EntityModel<LocalDate> localDateModel = EntityModel.of(localDate,
	        linkTo(methodOn(BookingController.class).checkAvailability()).withSelfRel(),
	        linkTo(methodOn(BookingController.class).checkAvailability(localDate.toString(), localDate.plusMonths(1).toString())).withRel("checkAvailabilitywithRange"));

	    return localDateModel;
	  }
}
