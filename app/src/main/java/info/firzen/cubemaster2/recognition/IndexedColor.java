package info.firzen.cubemaster2.recognition;

public class IndexedColor {
	private int index;
	private Rgb color;
	
	IndexedColor(int index, Rgb color) {
		this.index = index;
		this.color = color;
	}
	
	public int getIndex() {
		return index;
	}
	
	public void setIndex(int index) {
		this.index = index;
	}
	
	public Rgb getColor() {
		return color;
	}
	
	public void setColor(Rgb color) {
		this.color = color;
	}
	
	public String toString() {
		return index + ", " + color; 
	}
}

