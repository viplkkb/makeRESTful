package Payroll;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/*
This code uses Spring @RestController annotation, which marks the class as a controller 
where every method returns a domain object instead of a view. It is shorthand for including 
both @Controller and @ResponseBody.

The Greeting object must be converted to JSON. Thanks to Spring’s HTTP message converter support, 
you need not do this conversion manually. Because Jackson 2 is on the classpath, 
Spring’s MappingJackson2HttpMessageConverter 
is automatically chosen to convert the Greeting instance to JSON.

The Greeting objects automatically are converted to JSON by Spring’s MappingJackson2HttpMessageConverter.
 */

/*
Test the Service
http://localhost:8080/greeting
 */
@RestController
public class GreetingController {
	private final static String template = "Hello, %s!";
	private final AtomicLong counter = new AtomicLong();
	
	@GetMapping("/greeting")
	public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
		return new Greeting(counter.incrementAndGet(),name);
	}
}
