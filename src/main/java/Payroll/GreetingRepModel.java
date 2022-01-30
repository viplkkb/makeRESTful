package payroll;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class GreetingRepModel extends RepresentationModel<GreetingRepModel> {
	private final String content;

	@JsonCreator
	public GreetingRepModel(@JsonProperty("content") String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

}
