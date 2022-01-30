package payroll;

import org.springframework.data.jpa.repository.JpaRepository;

// To support interacting with orders in the database
// Must define a corresponding Spring Data repository
// Spring Data JPA's JpaRepository base interface
interface OrderRepository extends JpaRepository<Order, Long> {

}
