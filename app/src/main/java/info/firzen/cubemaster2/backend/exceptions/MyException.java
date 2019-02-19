package info.firzen.cubemaster2.backend.exceptions;

public abstract class MyException extends Exception {
	private static final long serialVersionUID = 6338081467852342788L;
	String whatHappened = new String();
	
	public MyException() {
		
	}
	
	public MyException(String what) {
		whatHappened = what;
	}
	
	public String toString() {
		return whatHappened;
	}
}
