package info.firzen.cubemaster2.backend.cube.geom;

public class Point {
	private double x;
	private double y;
	
	public Point() {
		
	}
	
	public Point(int x, int y) {
		setX(x);
		setY(y);
	}
	
	public Point(double x, double y) {
		setX(x);
		setY(y);
	}
	
	public static Point minus(Point one, Point two) {
		Point output = new Point();
		output.setX(one.getX() - two.getX());
		output.setY(one.getY() - two.getY());
		return output;
	}
	
	private double degToRad(double degAngle) {
		return Math.toRadians(degAngle);
	}
	
	public void rotate(Point center, double angle) {
		double newX = (x - center.getX()) * Math.cos(degToRad(angle))
				- (y - center.getY()) * Math.sin(degToRad(angle))
				+ center.getX();
		double newY = (x - center.getX()) * Math.sin(degToRad(angle))
				+ (y - center.getY()) * Math.cos(degToRad(angle))
				+ center.getY();
		setX(Math.round(newX));
		setY(Math.round(newY));
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("[");
		buffer.append(getX());
		buffer.append(", ");
		buffer.append(getY());
		buffer.append("]");
		return buffer.toString();
	}
}
