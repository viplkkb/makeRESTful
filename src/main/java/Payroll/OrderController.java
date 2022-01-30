package payroll;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class OrderController {
	// It injects both an OrderRepository as well as OrderModelAssembler 
	private final OrderRepository orderRepository;
	private final OrderModelAssembler assembler;
	
	OrderController(OrderRepository orderRepository, OrderModelAssembler assembler) {
		this.orderRepository = orderRepository;
		this.assembler = assembler;
	}
	
	// Gets all orders, returns CollectionModel<EntityModel<Order>> type
	// handles the aggregate root as well as a single item
	@GetMapping("/orders")
	CollectionModel<EntityModel<Order>> all () {
		List<EntityModel<Order>> orders = this.orderRepository.findAll().stream()//
				.map(assembler::toModel) //
				.collect(Collectors.toList());
		return CollectionModel.of(orders,//
				linkTo(methodOn(OrderController.class).all()).withSelfRel());
	}
	
	// Finds by a order's id.  it returns EntityModel<Order>
	@GetMapping("/orders/{id}")
	EntityModel<Order> one (@PathVariable Long id) {
		Order order = orderRepository.findById(id)//
				.orElseThrow(()->new OrderNotFoundException(id));
		
		return assembler.toModel(order);
	}
	
	// curl -X POST localhost:8080/orders -H "Content-type:application/json" -d "{\"description\" : \"AirPods Pro\"}"
	  
	// handles creating new orders, by starting them in the IN_PROGRESS
	@PostMapping("/orders") 
	ResponseEntity<EntityModel<Order>> newOrder(@RequestBody Order order) {
		order.setStatus(Status.IN_PROGRESS);
		Order newOrder = orderRepository.save(order);
		
		return ResponseEntity //
				.created(linkTo(methodOn(OrderController.class).one(newOrder.getId())).toUri()) //
				.body(assembler.toModel(newOrder));
	}
	
	// curl -v -X DELETE http://localhost:8080/orders/4/cancel
	@DeleteMapping("/orders/{id}/cancel")
	ResponseEntity<?> cancel (@PathVariable Long id) {
		Order order = orderRepository.findById(id) //
				.orElseThrow(()->new OrderNotFoundException(id));
		if(order.getStatus() == Status.IN_PROGRESS) {
			order.setStatus(Status.CANCELLED);
			return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
		}
		return ResponseEntity//
				.status(HttpStatus.METHOD_NOT_ALLOWED) //
				.header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) //
				.body(Problem.create() //
						.withTitle("Method not allowed")
						.withDetail("You can't cancel an order that is in the " + order.getStatus() + "status"));
				
	}
	// curl -v -X PUT http://localhost:8080/orders/5/complete
	@PutMapping("/orders/{id}/complete")
	ResponseEntity<?> complete(@PathVariable Long id) {

	  Order order = orderRepository.findById(id) //
	      .orElseThrow(() -> new OrderNotFoundException(id));

	  if (order.getStatus() == Status.IN_PROGRESS) {
	    order.setStatus(Status.COMPLETED);
	    return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
	  }

	  return ResponseEntity //
	      .status(HttpStatus.METHOD_NOT_ALLOWED) //
	      .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) //
	      .body(Problem.create() //
	          .withTitle("Method not allowed") //
	          .withDetail("You can't complete an order that is in the " + order.getStatus() + " status"));
	}
	
	
	/*
	curl -X PUT localhost:8080/orders/4/update -H "Content-type:application/json" -d "{\"description\" : \"AirPods Pro 2nd Generation\"}"
	curl -X PUT localhost:8080/orders/5/update -H "Content-type:application/json" -d "{\"quantity\" : 2}"  
	 */
	@PutMapping("/orders/{id}/update")
	ResponseEntity<?> update(@PathVariable(name="id") Long id, @RequestBody Order newOrder) {
		Order order = orderRepository.findById(id) //
			      .orElseThrow(() -> new OrderNotFoundException(id));
		
		if (order.getStatus() == Status.IN_PROGRESS) {
			boolean updated = false;
			if(newOrder.getDescription() != null) {
				order.setDescription(newOrder.getDescription());
				updated = true;
			}
			if(newOrder.getQuantity() != order.getQuantity()) {
				order.setQuantity(newOrder.getQuantity());
				updated = true;
			}
			if(updated)
				return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));	
		}
		
		return ResponseEntity //
			      .status(HttpStatus.METHOD_NOT_ALLOWED) //
			      .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE) //
			      .body(Problem.create() //
			          .withTitle("Method not allowed") //
			          .withDetail("You can't complete an order that is in the " + order.getStatus() + " status"));

	}
}
