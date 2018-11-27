package us.wy.state.deq.impact.aspects;

public class DefaultRetryable implements Retryable {

	private boolean retryable = true;

	public boolean isRetryable() {
		return retryable;
	}

	public void setRetryable(boolean retryable) {
		this.retryable = retryable;
	}

}
