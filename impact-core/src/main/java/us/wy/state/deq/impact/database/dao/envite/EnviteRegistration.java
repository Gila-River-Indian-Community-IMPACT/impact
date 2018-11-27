package us.wy.state.deq.impact.database.dao.envite;

import org.codehaus.jackson.annotate.JsonProperty;

public class EnviteRegistration {
	@JsonProperty("IsValid") public String isValid;
	@JsonProperty("ErrorList") public String[] errorList;
	@JsonProperty("PassList") public String[] passList;
	@JsonProperty("NewId") public String newId;
}
