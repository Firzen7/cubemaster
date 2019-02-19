package info.firzen.cubemaster2.backend.cube.enums;

import info.firzen.cubemaster2.backend.cube.geom.Point;

public enum MoveDirection {
	HORIZONTAL, VERTICAL;
	
	public static MoveDirection parseVector(Point pt) {
		if(pt.getX() == 0) {
			return VERTICAL;
		}
		else {
			return HORIZONTAL;
		}
	}
}
