public class FutoshikiException extends RuntimeException {

	public FutoshikiException(String message) {
		super(message);
	}
	
	@Override
	public String toString() {
		return this.getMessage();
	}

}