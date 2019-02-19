package info.firzen.cubemaster2.backend.cube.geom;

import info.firzen.cubemaster2.backend.cube.enums.BasicMove;
import info.firzen.cubemaster2.backend.cube.enums.RotationAxis;
import info.firzen.cubemaster2.backend.cube.enums.SideType;
import info.firzen.cubemaster2.backend.exceptions.PointException;
import info.firzen.cubemaster2.backend.exceptions.UnknownException;

public class Point3D {
	private double x = 0;
	private double y = 0;
	private double z = 0;
	
	public Point3D() {
		
	}
	
	public Point3D(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
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
	
	public double getZ() {
		return z;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
	
	public double distance(Point3D other) throws Exception {
		if(other != null) {
			return Math.sqrt(Math.pow(Math.abs(getX() - other.getX()), 2)
					+ Math.pow(Math.abs(getY() - other.getY()), 2)
					+ Math.pow(Math.abs(getZ() - other.getZ()), 2));
		}
		else {
			throw new Exception("Second point must nut be null!");
		}
	}
	
	public void rotate(BasicMove move, Point3D center, double angle)
			throws PointException {
		rotate(SideType.parseBasicMove(move), center, angle);
	}
	
	public void rotate(SideType plane, Point3D center, double angle)
			throws PointException {
		if(plane == SideType.FRONT || plane == SideType.BACK) {
			Point point = new Point(getX(), getZ());
			Point center2D = new Point(center.getX(), center.getZ());
			point.rotate(center2D, angle);
			this.setX(point.getX());
			this.setZ(point.getY());
		}
		else if(plane == SideType.UP || plane == SideType.DOWN) {
			Point point = new Point(getX(), getY());
			Point center2D = new Point(center.getX(), center.getY());
			point.rotate(center2D, angle);
			this.setX(point.getX());
			this.setY(point.getY());
		}
		else if(plane == SideType.LEFT || plane == SideType.RIGHT) {
			Point point = new Point(getY(), getZ());
			Point center2D = new Point(center.getY(), center.getZ());
			point.rotate(center2D, angle);
			this.setY(point.getX());
			this.setZ(point.getY());
		}
		else {
			throw new PointException("Cannot compute 2D point");
		}
	}
	
	public Point3D getClone() {
		return new Point3D(getX(), getY(), getZ());
	}
	
	public static Point3D plus(Point3D one, Point3D two) {
		Point3D output = new Point3D();
		output.setX(one.getX() + two.getX());
		output.setY(one.getY() + two.getY());
		output.setZ(one.getZ() + two.getZ());
		return output;
	}

	public static Point3D minus(Point3D one, Point3D two) {
		Point3D output = new Point3D();
		output.setX(one.getX() - two.getX());
		output.setY(one.getY() - two.getY());
		output.setZ(one.getZ() - two.getZ());
		return output;
	}
	
	public static Point3D divide(Point3D point, double number) {
		double x = point.getX() / number;
		double y = point.getY() / number;
		double z = point.getZ() / number;
		return new Point3D(x, y, z);
	}
	
	public Point convertTo2D(SideType side) throws Exception {
		if(side != null) {
			RotationAxis axis = RotationAxis.parseSideType(side);
			return convertTo2D(axis);
		}
		else {
			throw new Exception("Cannot convert point with null side!");
		}
	}
	
	public Point convertTo2D(RotationAxis axis) throws Exception {
		if(axis != null) {
			switch(axis) {
				case X:
					return new Point(getY(), getZ());
				case Y:
					return new Point(getX(), getZ());
				case Z:
					return new Point(getX(), getY());
				default:
					throw new UnknownException("Unknown axis " + axis + "!");
			}
		}
		else {
			throw new Exception("Cannot convert point with null axis!");
		}
	}
	
	public Point3D getCopy() {
		Point3D output = new Point3D(x, y, z);
		return output;
	}
	
	public boolean equals(Object other) {
		if(other == null) {
			return false;
		}
		
		if(other instanceof Point3D) {
			Point3D o = (Point3D) other;
			return o.getX() == x && o.getY() == y && o.getZ() == z;
		}
		else {
			return false;
		}
	}
	
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("[");
		buffer.append(getX());
		buffer.append(", ");
		buffer.append(getY());
		buffer.append(", ");
		buffer.append(getZ());
		buffer.append("]");
		return buffer.toString();
	}
}
