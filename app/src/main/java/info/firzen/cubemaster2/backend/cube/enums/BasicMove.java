package info.firzen.cubemaster2.backend.cube.enums;

import info.firzen.cubemaster2.backend.exceptions.ParseException;
import info.firzen.cubemaster2.backend.exceptions.UnknownException;

public enum BasicMove {
	UP("U", false),
	DOWN("D", false),
	LEFT("L", false),
	RIGHT("R", false),
	FRONT("F", false),
	BACK("B", false),
	UP_INVERTED("Ui", true),
	DOWN_INVERTED("Di", true),
	LEFT_INVERTED("Li", true),
	RIGHT_INVERTED("Ri", true),
	FRONT_INVERTED("Fi", true),
	BACK_INVERTED("Bi", true);
	
	private String name;
	private boolean inverted;
	
	BasicMove(String name, boolean inverted) {
		this.name = name;
		this.inverted = inverted;
	}
	
	public static BasicMove getRandomMove() {
		BasicMove[] values = BasicMove.values();
		return values[(int) (Math.random() * (double)values.length)];
	}
	
	public static BasicMove getInvertedMove(BasicMove move) throws UnknownException {
		switch(move) {
			case BACK: return BACK_INVERTED;
			case BACK_INVERTED: return BACK;
			case DOWN: return DOWN_INVERTED;
			case DOWN_INVERTED: return DOWN;
			case FRONT: return FRONT_INVERTED;
			case FRONT_INVERTED: return FRONT;
			case LEFT: return LEFT_INVERTED;
			case LEFT_INVERTED: return LEFT;
			case RIGHT: return RIGHT_INVERTED;
			case RIGHT_INVERTED: return RIGHT;
			case UP: return UP_INVERTED;
			case UP_INVERTED: return UP;
			default:
				throw new UnknownException("Heureka! Impossible just happened!");
		}
	}
	
	public BasicMove getOppositeMove() throws UnknownException {
		switch(this) {
			case BACK: return FRONT_INVERTED;
			case DOWN: return UP_INVERTED;
			case FRONT: return BACK_INVERTED;
			case LEFT: return RIGHT_INVERTED;
			case RIGHT: return LEFT_INVERTED;
			case UP: return DOWN_INVERTED;
			case BACK_INVERTED: return FRONT;
			case DOWN_INVERTED: return UP;
			case FRONT_INVERTED: return BACK;
			case LEFT_INVERTED: return RIGHT;
			case RIGHT_INVERTED: return LEFT;
			case UP_INVERTED: return DOWN;
			default:
				throw new UnknownException();
		}
	}
	
	public static BasicMove parseString(String s) throws ParseException {
		BasicMove[] values = BasicMove.values();
		for(BasicMove move : values) {
			if(move.getName().equals(s)) {
				return move;
			}
		}
		
		throw new ParseException("Cannot parse string " + s);
	}
	
	public String getName() {
		return name;
	}
	
	public static BasicMove parseSideType(SideType side)
			throws UnknownException {
		switch(side) {
		case BACK: return BACK;
		case DOWN: return DOWN;
		case FRONT: return FRONT;
		case LEFT: return LEFT;
		case RIGHT: return RIGHT;
		case UP: return UP;
		default:
			throw new UnknownException();
		}
	}
	
	public static BasicMove parseSideType(SideType side, boolean inverted)
			throws UnknownException {
		if(inverted) {
			switch(side) {
				case BACK: return BACK_INVERTED;
				case DOWN: return DOWN_INVERTED;
				case FRONT: return FRONT_INVERTED;
				case LEFT: return LEFT_INVERTED;
				case RIGHT: return RIGHT_INVERTED;
				case UP: return UP_INVERTED;
				default:
					throw new UnknownException();
			}
		}
		else {
			return parseSideType(side);
		}
	}
	
	public BasicMove rotate(BasicMove move) throws UnknownException {
		SideType side = SideType.parseBasicMove(this);
		SideType rotated = side.rotate(move);
		return BasicMove.parseSideType(rotated, this.isInverted());
	}
	
	public String toString() {
		return getName();
	}

	public boolean isInverted() {
		return inverted;
	}
}
