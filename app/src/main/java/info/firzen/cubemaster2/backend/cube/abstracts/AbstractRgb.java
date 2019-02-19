package info.firzen.cubemaster2.backend.cube.abstracts;

import info.firzen.cubemaster2.backend.cube.interfaces.IRgb;

public class AbstractRgb implements IRgb {
	protected int red = 0;
	protected int green = 0;
	protected int blue = 0;

	public int getRed() {
		return red;
	}
	
	public void setRed(int red) {
		this.red = red;
	}
	
	public int getGreen() {
		return green;
	}
	
	public void setGreen(int green) {
		this.green = green;
	}
	
	public int getBlue() {
		return blue;
	}
	
	public void setBlue(int blue) {
		this.blue = blue;
	}
	
	public int getMax() {
		int max = red;
		
		if(green > max) max = green;
		if(blue > max) max = blue;
		
		return max;
	}

	public int getMin() {
		int min = red;
		
		if(green < min) min = green;
		if(blue < min) min = blue;
		
		return min;
	}
	
	public double distance(IRgb c2) {
	    double rmean = (getRed() + c2.getRed()) / 2;
	    int r = getRed() - c2.getRed();
	    int g = getGreen() - c2.getGreen();
	    int b = getBlue() - c2.getBlue();
	    double weightR = 2 + rmean / 256;
	    double weightG = 4.0;
	    double weightB = 2 + (255 - rmean) / 256;
	    return Math.sqrt(weightR * r * r + weightG * g * g + weightB * b * b);
	}
	
	public int hashCode() {
		return (((Integer)red).toString() + " " + ((Integer)green).toString()
				+ " " + ((Integer)blue).toString()).hashCode(); 
	}

	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("[");
		buffer.append(red);
		buffer.append(", ");
		buffer.append(green);
		buffer.append(", ");
		buffer.append(blue);
		buffer.append("]");
		return buffer.toString();
	}

	public boolean equals(Object other) {
		if(other != null && other instanceof IRgb) {
			return ((IRgb)other).getRed() == getRed() &&
					((IRgb)other).getGreen() == getGreen() &&
					((IRgb)other).getBlue() == getBlue();
		}
		else {
			return false;
		}
	}
}
