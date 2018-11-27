package us.wy.state.deq.impact.database.dao.envite;

import org.codehaus.jackson.annotate.JsonProperty;

public class EnviteClaim {
	@JsonProperty("Type") public String type;
	@JsonProperty("Value") public String value;
}
