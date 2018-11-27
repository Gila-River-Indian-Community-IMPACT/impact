package us.oh.state.epa.stars2.framework.exception;

@SuppressWarnings("serial")
public class InvoiceGenerationException extends Exception {

	public InvoiceGenerationException() {
		super();
	}
	
	public InvoiceGenerationException(final String message) {
		super(message);
	}
	
	public InvoiceGenerationException(final String message, final Throwable t) {
		super(message, t);
	}
}
