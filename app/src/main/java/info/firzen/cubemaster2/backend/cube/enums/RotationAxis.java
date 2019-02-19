package info.firzen.cubemaster2.backend.cube.enums;

import info.firzen.cubemaster2.backend.exceptions.ParseException;

public enum RotationAxis {
	X, Y, Z;
	
	public static RotationAxis parseSideType(SideType side) throws ParseException {
		if(side != null) {
			if(SideType.FRONT == side || SideType.BACK == side) {
				return Y;
			}
			else if(SideType.UP == side || SideType.DOWN == side) {
				return Z;
			}
			else if(SideType.LEFT == side || SideType.RIGHT == side) {
				return X;
			}
			else {
				throw new ParseException("Cannot parse " + side + "!");
			}
		}
		else {
			throw new ParseException("Side must not be null!");
		}
	}
}
