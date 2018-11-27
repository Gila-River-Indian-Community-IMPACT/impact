package us.wy.state.deq.impact.aspects;

public interface Retryable {
	
	boolean isRetryable();
	
	void setRetryable(boolean retryable);

}
