package info.firzen.cubemaster2.gui;

import info.firzen.cubemaster2.backend.cube.Field;
import info.firzen.cubemaster2.backend.cube.Move;
import info.firzen.cubemaster2.backend.cube.abstracts.AbstractField;
import info.firzen.cubemaster2.backend.cube.enums.BasicMove;
import info.firzen.cubemaster2.backend.cube.enums.FieldType;
import info.firzen.cubemaster2.backend.cube.enums.SideType;
import info.firzen.cubemaster2.backend.cube.geom.Point3D;
import info.firzen.cubemaster2.backend.cube.interfaces.ISticker;
import info.firzen.cubemaster2.backend.exceptions.FieldException;
import info.firzen.cubemaster2.backend.exceptions.ParseException;
import info.firzen.cubemaster2.backend.exceptions.PointException;
import info.firzen.cubemaster2.cube.CubeColor;
import info.firzen.cubemaster2.other.Constants;

import java.util.ArrayList;
import java.util.List;

public class Field3D extends AbstractField {
	private int rotationsRemaining = -1;
	
	public Field3D(FieldType type, int x, int y, int z) {
		stickers = new Sticker3D[6];
		this.type = type;
		setLocation(new Point3D(x, y, z));
	}
	
	public boolean rotateStep(Move move) throws PointException {
		return rotateStep(move.getBasicMove());
	}
	
	private boolean rotateStep(BasicMove move) throws PointException {
		final int parts = Constants.rotateStepCount;
		
		if(rotationsRemaining == -1) {
			rotationsRemaining = parts;
		}

		final float partialAngle = getTotalAngle(move) / (float) parts;
		
		if(rotationsRemaining > 0) {
			rotate(move, partialAngle);
			rotationsRemaining--;
			return true;
		}
		else {
			return false;
		}
	}
	
	public void rotate(BasicMove move, float angle) throws PointException {
		switch(move) {
			case LEFT: rotateX((float) Math.toRadians(angle));
						break;
			case RIGHT: rotateX((float) Math.toRadians(angle));
						break;
			case UP: rotateZ((float) Math.toRadians(angle));
						break;
			case DOWN: rotateZ((float) Math.toRadians(angle));
						break;
			case FRONT: rotateY((float) Math.toRadians(angle));
						break;
			case BACK: rotateY((float) Math.toRadians(angle));
						break;
			
			case LEFT_INVERTED: rotate(BasicMove.RIGHT, angle); break;
			case RIGHT_INVERTED: rotate(BasicMove.LEFT, angle); break;
			case UP_INVERTED: rotate(BasicMove.DOWN, angle); break;
			case DOWN_INVERTED: rotate(BasicMove.UP, angle); break;
			case FRONT_INVERTED: rotate(BasicMove.BACK, angle); break;
			case BACK_INVERTED: rotate(BasicMove.FRONT, angle); break;
		}
	}
	
	public void rotateX(float angle) {
		for(ISticker sticker : stickers) {
			if(sticker != null) {
				((Sticker3D) sticker).rotateX(angle);
			}
		}
	}
	
	public void rotateY(float angle) {
		for(ISticker sticker : stickers) {
			if(sticker != null) {
				((Sticker3D) sticker).rotateY(angle);
			}
		}
	}

	public void rotateZ(float angle) {
		for(ISticker sticker : stickers) {
			if(sticker != null) {
				((Sticker3D) sticker).rotateZ(angle);
			}
		}
	}
	
	/**
	 * Vrátí úhel ve stupních, o který je třeba rotovat dílek při daném tahu
	 * na kostce.
	 * @param move BasicMove
	 * @return float
	 */
	private float getTotalAngle(BasicMove move) {
		switch(move) {
			case LEFT: return -90f;
			case RIGHT: return 90f;
			case UP: return -90f;
			case DOWN: return 90f;
			case FRONT: return 90f;
			case BACK: return -90f;
	
			case LEFT_INVERTED: return getTotalAngle(BasicMove.RIGHT);
			case RIGHT_INVERTED: return getTotalAngle(BasicMove.LEFT);
			case UP_INVERTED: return getTotalAngle(BasicMove.DOWN);
			case DOWN_INVERTED: return getTotalAngle(BasicMove.UP);
			case FRONT_INVERTED: return getTotalAngle(BasicMove.BACK);
			case BACK_INVERTED: return getTotalAngle(BasicMove.FRONT);
			
			default: return 0f;
		}
	}

	@Override
	public Point3D getLocation() {
		return location;
	}

	@Override
	public void setLocation(Point3D location) {
		this.location = location;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
	}
	
	public void resetRotations() {
		rotationsRemaining = -1;
	}
	
	public void setSticker(int index, ISticker color) throws FieldException {
		if(type == null) {
			throw new FieldException("Type of field has not been set");
		}
		else if(!isUniqueColor(color)) {
			throw new FieldException("Color " + color + " is not unique");
		}
		else {
			if(color == null) {
				stickers[index] = color;
				stickersCount--;
			}
			else {
				if(stickers[index] == null) {
					color.setParent(this);
					stickers[index] = color;
					stickersCount++;
				}
				else {
					color.setParent(this);
					stickers[index] = color;
				}
			}
		}
	}
	
	public void removeSticker(SideType side) throws FieldException {
		setSticker(side, null);
	}
	
	public Field getField() throws FieldException, ParseException {
		Field output = new Field(this.getType());
		output.setLocation(location.getCopy());
		int i = 0;
		for(ISticker sticker : stickers) {
			if(!sticker.getColor().equals(CubeColor.BLACK.getColor()))
			{
				output.setSticker(i, ((Sticker3D) sticker).getSticker());
			}
			i++;
		}
		return output;
	}

	@Override
	public boolean isValid() {
		List<ISticker> nonBlackStickers = new ArrayList<ISticker>();
		for(ISticker sticker : stickers) {
			if(sticker != null) {
				if(!CubeColor.BLACK.getColor().equals(sticker.getColor())) {
					for(ISticker s : nonBlackStickers) {
						if(s.getColor().equals(sticker.getColor())) {
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
