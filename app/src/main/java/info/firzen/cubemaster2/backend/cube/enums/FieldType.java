package info.firzen.cubemaster2.backend.cube.enums;

public enum FieldType {
	FLAT(1),
	EDGE(2),
	CORNER(3);
	
	private int sidesCount;
	
	FieldType(int sides) {
		sidesCount = sides;
	}
	
	public int getSidesCount() {
		return sidesCount;
	}
}
