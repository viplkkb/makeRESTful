package payroll;

//import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class EmployeeController {

  private final EmployeeRepository repository;

  EmployeeController(EmployeeRepository repository) {
    this.repository = repository;
  }

  // curl -v localhost:8080/employees
  // Aggregate root
  // tag::get-aggregate-root[]
	/*
	 * @GetMapping("/employees") List<Employee> all() { return repository.findAll();
	 * }
	 */
  // end::get-aggregate-root[]
  
  /*
   CollectionModel<> is another container that is aimed at encapsulating collections of resources like EntityModel<>
   it also includes links
   
   "encapsulating collections" should encapsulate collections of employee resources
    not Collections of employees 
   */
  
  @GetMapping("/employees")
  CollectionModel<EntityModel<Employee>> all() {

    List<EntityModel<Employee>> employees = repository.findAll().stream()
        .map(employee -> EntityModel.of(employee,
            linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
            linkTo(methodOn(EmployeeController.class).all()).withRel("employees")))
        .collect(Collectors.toList());

    return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
  }

  // curl -X POST localhost:8080/employees -H "Content-type:application/json" -d "{\"name\": \"Samwise Gamgee\", \"role\": \"gardener\"}"
  @PostMapping("/employees")
  Employee newEmployee(@RequestBody Employee newEmployee) {
    return repository.save(newEmployee);
  }

  // Single item
  // curl -v localhost:8080/employees/99
	/*
	 * @GetMapping("/employees/{id}") Employee one(@PathVariable Long id) {
	 * 
	 * return repository.findById(id) .orElseThrow(() -> new
	 * EmployeeNotFoundException(id)); }
	 */
  
  /*
   Its return type is EntityModel<T> that is generic container that includes not only the data but a collection of links
   - linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel() means hateoas make a link to the
   EmployeeController's one() method, flag it as self link.
   - linkTo(methodOn(EmployeeController.class).all()).withRel("employees") asks Spring HATEOAS build a link to the 
   aggregate root, all(), and call it "employees".
   */
  @GetMapping("/employees/{id}")
  EntityModel<Employee> one(@PathVariable Long id) {

    Employee employee = repository.findById(id) //
        .orElseThrow(() -> new EmployeeNotFoundException(id));

    return EntityModel.of(employee, //
        linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
        linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
  }

// curl -X PUT localhost:8080/employees/3 -H "Content-type:application/json" -d "{\"name\": \"Samwise Gamgee\", \"role\": \"ring bearer\"}"
  @PutMapping("/employees/{id}")
  Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
    
    return repository.findById(id)
      .map(employee -> {
        employee.setName(newEmployee.getName());
        employee.setRole(newEmployee.getRole());
        return repository.save(employee);
      })
      .orElseGet(() -> {
        newEmployee.setId(id);
        return repository.save(newEmployee);
      });
  }

  // curl -X DELETE localhost:8080/employees/3
  @DeleteMapping("/employees/{id}")
  void deleteEmployee(@PathVariable Long id) {
    repository.deleteById(id);
  }
}