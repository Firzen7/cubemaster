package info.firzen.cubemaster2.cube;

public class CubeSide {
	
	private int size = 3;
	private int[][] fields = new int[size][size];
	
	public CubeSide() {
		
	}
	
	public CubeSide(int size) {
		setSize(size);
	}
	
	public int getSize() {
		return size;
	}
	
	public void setSize(int size) {
		this.size = size;
		fields = new int[size][size];
	}
	
	public int[][] getFields() {
		return fields;
	}
	
	public void setFields(int[][] fields) {
		this.fields = fields;
	}
	
	public void setField(int x, int y, int field) {
		fields[x][y] = field;
	}
}
