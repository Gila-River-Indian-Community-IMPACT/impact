package us.oh.state.epa.stars2.framework.exception;

@SuppressWarnings("serial")
public class InvalidUserException extends RuntimeException {

	public InvalidUserException(String string) {
		super(string);
	}

}
