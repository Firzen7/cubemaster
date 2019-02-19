package info.firzen.cubemaster2.backend.cube.enums;

import info.firzen.cubemaster2.backend.exceptions.ParseException;
import info.firzen.cubemaster2.backend.exceptions.UnknownException;

public enum SideType {
	UP(5),
	DOWN(4),
	LEFT(3),
	RIGHT(1),
	FRONT(0),
	BACK(2);
	
	private int index;
	
	SideType(int index) {
		this.index = index;
	}
	
	public int getIndex() {
		return index;
	}
	
	public SideType getOppositeSide() throws UnknownException {
		switch(this) {
			case BACK: return FRONT;
			case DOWN: return UP;
			case FRONT: return BACK;
			case LEFT: return RIGHT;
			case RIGHT: return LEFT;
			case UP: return DOWN;
			default:
				throw new UnknownException();
		}
	}
	
	public static SideType parseBasicMove(BasicMove move) {
		switch(move) {
			case BACK: return SideType.BACK;
			case BACK_INVERTED: return SideType.BACK;
			case DOWN: return SideType.DOWN;
			case DOWN_INVERTED: return SideType.DOWN;
			case FRONT: return SideType.FRONT;
			case FRONT_INVERTED: return SideType.FRONT;
			case LEFT: return SideType.LEFT;
			case LEFT_INVERTED: return SideType.LEFT;
			case RIGHT: return SideType.RIGHT;
			case RIGHT_INVERTED: return SideType.RIGHT;
			case UP: return SideType.UP;
			case UP_INVERTED: return SideType.UP;
			default: return null;
		}
	}
	
	public static SideType parseIndex(int index) throws ParseException {
		SideType[] values = values();
		for(SideType val : values) {
			if(val.getIndex() == index) {
				return val;
			}
		}
		
		throw new ParseException("Unknown sidetype index!");
	}
	
	public SideType rotate(BasicMove move) {
		SideType[] values = values();
		
		// here we will save correctly sorted side types
		SideType[] sides = new SideType[6];
		for(SideType val : values) {
			sides[val.getIndex()] = val;
		}
		
		SideType[] newSides = new SideType[6];
		
		switch(move) {
			case LEFT: newSides = rearrangeSides(sides, new int[] {5, 1, 4, 3, 0, 2});
						break;
			case RIGHT: newSides = rearrangeSides(sides, new int[] {4, 1, 5, 3, 2, 0});
						break;
			case UP: newSides = rearrangeSides(sides, new int[] {1, 2, 3, 0, 4, 5});
						break;
			case DOWN: newSides = rearrangeSides(sides, new int[] {3, 0, 1, 2, 4, 5});
						break;
			case FRONT: newSides = rearrangeSides(sides, new int[] {0, 5, 2, 4, 1, 3});
						break;
			case BACK: newSides = rearrangeSides(sides, new int[] {0, 4, 2, 5, 3, 1});
						break;
			
			case LEFT_INVERTED: return rotate(BasicMove.RIGHT);
			case RIGHT_INVERTED: return rotate(BasicMove.LEFT);
			case UP_INVERTED: return rotate(BasicMove.DOWN);
			case DOWN_INVERTED: return rotate(BasicMove.UP);
			case FRONT_INVERTED: return rotate(BasicMove.BACK);
			case BACK_INVERTED: return rotate(BasicMove.FRONT);
		}
		
		return newSides[this.getIndex()];
	}
	
	private SideType[] rearrangeSides(SideType[] sides, int[] newIndexes) {
		SideType[] newSides = new SideType[6];
		for(int i = 0; i < 6; i++) {
			newSides[i] = sides[newIndexes[i]];
		}
		return newSides;
	}
}
