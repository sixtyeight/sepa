package at.metalab.sepa;

public class SepaException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SepaException(String message) {
		super(message);
	}
	
	public SepaException(Throwable cause) {
		super(cause);
	}
	
	public SepaException(String message, Throwable cause) {
		super(message, cause);
	}

}
