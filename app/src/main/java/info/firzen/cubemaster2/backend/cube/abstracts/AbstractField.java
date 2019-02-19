package info.firzen.cubemaster2.backend.cube.abstracts;

import java.util.ArrayList;
import java.util.List;

import info.firzen.cubemaster2.backend.cube.Move;
import info.firzen.cubemaster2.backend.cube.enums.BasicMove;
import info.firzen.cubemaster2.backend.cube.enums.FieldType;
import info.firzen.cubemaster2.backend.cube.enums.SideType;
import info.firzen.cubemaster2.backend.cube.geom.Point3D;
import info.firzen.cubemaster2.backend.cube.interfaces.IField;
import info.firzen.cubemaster2.backend.cube.interfaces.ISticker;
import info.firzen.cubemaster2.backend.exceptions.FieldException;
import info.firzen.cubemaster2.backend.exceptions.PointException;
import info.firzen.cubemaster2.cube.CubeColor;

public abstract class AbstractField implements IField {
	protected Point3D location = new Point3D();
	protected FieldType type;
	protected ISticker[] stickers;
	protected int stickersCount = 0;
	
	public boolean containsSticker(ISticker color) {
		for(ISticker c : stickers) {
			if(c != null && c.equals(color)) {
				return true;
			}
		}
		return false;
	}
	
	protected void rearrangeStickers(int[] newIndexes) {
		ISticker[] newSides = new ISticker[6];
		for(int i = 0; i < 6; i++) {
			newSides[i] = stickers[newIndexes[i]];
		}
		stickers = newSides;
	}
	
	public void rotate(Move move, Point3D center) throws PointException {
		rotate(move.getBasicMove(), center);
	}

	public void rotate(BasicMove move, Point3D center) throws PointException {
		switch(move) {
			case LEFT: rearrangeStickers(new int[] {5, 1, 4, 3, 0, 2});
						location.rotate(move, center, 90);
						break;
			case RIGHT: rearrangeStickers(new int[] {4, 1, 5, 3, 2, 0});
						location.rotate(move, center, 270);
						break;
			case UP: rearrangeStickers(new int[] {1, 2, 3, 0, 4, 5});
						location.rotate(move, center, 90);
						break;
			case DOWN: rearrangeStickers(new int[] {3, 0, 1, 2, 4, 5});
						location.rotate(move, center, 270);
						break;
			case FRONT: rearrangeStickers(new int[] {0, 5, 2, 4, 1, 3});
						location.rotate(move, center, 90);
						break;
			case BACK: rearrangeStickers(new int[] {0, 4, 2, 5, 3, 1});
						location.rotate(move, center, 270);
						break;
			
			case LEFT_INVERTED: rotate(BasicMove.RIGHT, center); break;
			case RIGHT_INVERTED: rotate(BasicMove.LEFT, center); break;
			case UP_INVERTED: rotate(BasicMove.DOWN, center); break;
			case DOWN_INVERTED: rotate(BasicMove.UP, center); break;
			case FRONT_INVERTED: rotate(BasicMove.BACK, center); break;
			case BACK_INVERTED: rotate(BasicMove.FRONT, center); break;
		}
	}

	protected boolean isUniqueColor(ISticker color) {
		for(ISticker c : stickers) {
			if(c != null && c.equals(color)) {
				return false;
			}
		}
		return true;
	}
	
	public void setSticker(SideType side, ISticker color) throws FieldException {
		setSticker(side.getIndex(), color);
	}
	
	public void setSticker(int index, ISticker color)
			throws FieldException {
		if(type == null) {
			throw new FieldException("Type of field has not been set");
		}
		else if(!isUniqueColor(color)) {
			throw new FieldException("Color " + color + " is not unique");
		}
		else {
			if(color == null) {
				if(stickers[index] != null) {
					stickersCount--;
				}
				stickers[index] = color;
			}
			else {
				if(stickers[index] == null) {
					if(stickersCount == type.getSidesCount()) {
						throw new FieldException("Count of sides for was exceeded");
					}
					else {
						color.setParent(this);
						stickers[index] = color;
						stickersCount++;
					}
				}
				else {
					color.setParent(this);
					stickers[index] = color;
				}
			}
		}
	}
	
	public ISticker getSticker(int index) {
		return stickers[index];
	}
	
	public ISticker getSticker(SideType side) {
		return getSticker(side.getIndex());
	}
	
	public boolean hasSticker(SideType side) {
		return getSticker(side) != null;
	}
	
	public ISticker[] getStickers() {
		return stickers;
	}
	
	public FieldType getType() {
		return type;
	}
	
	public int getStickersCount() {
		return stickersCount;
	}

	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("Field of type ");
		switch(type) {
			case CORNER: buffer.append("corner"); break;
			case EDGE: buffer.append("edge"); break;
			case FLAT: buffer.append("flat"); break;
			default: buffer.append("unknown"); break;
		}
		buffer.append(" with sides: [");
		SideType[] values = SideType.values();
		for(SideType val : values) {
			ISticker color = getSticker(val);
			if(color != null) {
				buffer.append(val.name());
				buffer.append(": ");
				buffer.append(color);
				buffer.append(" ");
			}
		}
		buffer.append("]");
		buffer.append("  location: ");
		buffer.append(location);
		return buffer.toString();
	}
	
	public boolean isEmpty() {
		for(ISticker c : stickers) {
			if(c != null) {
				return false;
			}
		}
		return true;
	}

	public Point3D getLocation() {
		return location;
	}

	public void setLocation(Point3D location) {
		this.location = location;
	}
	
	public boolean equals(Object other) {
		if(other == null) {
			return false;
		}
		if(other instanceof IField) {
			if(stickersCount != ((IField) other).getStickersCount()) {
				return false;
			}
			
			for(int i = 0; i < 6; i++) {
				if(stickers[i] == null && ((IField) other).getStickers()[i] != null) {
					return false;
				}
				if(stickers[i] != null && ((IField) other).getStickers()[i] == null) {
					return false;
				}
				if(stickers[i] != null && ((IField) other).getStickers()[i] != null
						&& !stickers[i].equals(((IField) other).getStickers()[i])) {
					return false;
				}
			}
			
			return type.equals(((IField) other).getType())
					&& location.equals(((IField) other).getLocation());
		}
		else {
			return false;
		}
	}
	
	public boolean isValid() {
		List<ISticker> nonBlackStickers = new ArrayList<ISticker>();
		for(ISticker sticker : stickers) {
			if(sticker != null) {
				if(!CubeColor.UNKNOWN.getColor().equals(sticker.getColor())) {
					for(ISticker s : nonBlackStickers) {
						if(s.getColor().equals(sticker)) {
							return false;
						}
					}
					nonBlackStickers.add(sticker);
				}
			}
		}
		
		switch(type) {
			case CORNER:
				return nonBlackStickers.size() == 3;
			case EDGE:
				return nonBlackStickers.size() == 2;
			case FLAT:
				return nonBlackStickers.size() == 1; 
			default:
				return false;
		}
	}
}
