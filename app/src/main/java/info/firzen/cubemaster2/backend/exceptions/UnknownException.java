package info.firzen.cubemaster2.backend.exceptions;

public class UnknownException extends MyException {
	private static final long serialVersionUID = 2077155426091409124L;
	
	public UnknownException() {
		super();
	}
	
	public UnknownException(String what) {
		super(what);
	}
}
