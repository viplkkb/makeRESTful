package payroll;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="CUSTOMER_OREDER")
public class Order {
	private @Id @GeneratedValue Long id;
	private String description;
	private int quantity  =1 ;
	private Status status;
	
	Order() {}

	Order(String description, int quantity, Status status) {
		this.description = description;
		this.quantity = quantity;
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}
	
	public int getQuantity() {
		return quantity;
	}

	public Status getStatus() {
		return status;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.description, this.status);
	}

	@Override
	public boolean equals(Object o) {
		 if (this == o)
		      return true;
		 if (!(o instanceof Order))
		      return false;
		 Order order = (Order) o;
		 return Objects.equals(this.id, order.id) && Objects.equals(this.description, order.description) 
				 && Objects.equals(this.quantity, order.quantity)
 		         && this.status == order.status;
	}

	@Override
	public String toString() {
		 return "Order{" + "id=" + this.id + ", description='" + this.description + '\'' +",quantity="+ this.quantity+ ", status=" + this.status + '}';
	}


	
}
