package info.firzen.cubemaster2.backend.cube.interfaces;

import info.firzen.cubemaster2.backend.cube.Move;
import info.firzen.cubemaster2.backend.cube.enums.FieldType;
import info.firzen.cubemaster2.backend.cube.enums.SideType;
import info.firzen.cubemaster2.backend.cube.geom.Point3D;
import info.firzen.cubemaster2.backend.exceptions.FieldException;
import info.firzen.cubemaster2.backend.exceptions.PointException;

public interface IField {
	public Point3D getLocation();
	public void setLocation(Point3D location);
	public void setSticker(SideType side, ISticker color) throws FieldException;
	public void setSticker(int index, ISticker color) throws FieldException;
	public ISticker getSticker(int index);
	public ISticker getSticker(SideType side);
	public boolean hasSticker(SideType side);
	public ISticker[] getStickers();
	public FieldType getType();
	public int getStickersCount();
	public boolean isEmpty();
	public void rotate(Move move, Point3D center) throws PointException;
	public boolean containsSticker(ISticker color);
	
	public boolean isValid();
}
