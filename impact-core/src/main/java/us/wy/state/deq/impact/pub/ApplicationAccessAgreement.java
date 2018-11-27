package us.wy.state.deq.impact.pub;

import java.io.Serializable;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("session")
@Component("appAccessAgreement")
@SuppressWarnings("serial")
public class ApplicationAccessAgreement implements Serializable {

	private boolean hasAgreed = false; // has the user agreed to the disclaimer

	public ApplicationAccessAgreement() {
		super();
	}

	public boolean isHasAgreed() {
		return hasAgreed;
	}

	public void setHasAgreed(boolean hasAgreed) {
		this.hasAgreed = hasAgreed;
	}
}
