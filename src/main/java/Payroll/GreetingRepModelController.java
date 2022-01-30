package payroll;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingRepModelController {
	
	private static final String TEMPLATE = "Hello, %s!";

	@RequestMapping("/greeting2")
	public HttpEntity<GreetingRepModel> greeting(
		@RequestParam(value = "name", defaultValue = "World") String name) {

		GreetingRepModel greeting = new GreetingRepModel(String.format(TEMPLATE, name));
		greeting.add(linkTo(methodOn(GreetingController.class).greeting(name)).withSelfRel());

		return new ResponseEntity<>(greeting, HttpStatus.OK);
	}

}
