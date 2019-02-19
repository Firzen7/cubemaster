package info.firzen.cubemaster2.backend.cube.interfaces;

import info.firzen.cubemaster2.backend.cube.Move;
import info.firzen.cubemaster2.backend.cube.enums.BasicMove;
import info.firzen.cubemaster2.backend.cube.enums.FieldType;
import info.firzen.cubemaster2.backend.cube.enums.SideType;
import info.firzen.cubemaster2.backend.cube.geom.Point3D;
import info.firzen.cubemaster2.backend.exceptions.PointException;

import java.util.List;

public interface ICube {
	public void doMove(Move move) throws PointException;
	public void doMoves(List<Move> moves) throws Exception;
	public void doMoves(String str) throws Exception;
	public void doMove(BasicMove move) throws Exception;
	
	public IField getField(SideType side, int x, int y) throws Exception;
	public IField getField(int x, int y, int z);
	public IField getField(Point3D location);
	public List<IField> getFields();
	public ISticker getFieldColor(int x, int y, int z, SideType side);
	public ISticker getFieldColor(SideType side, int x, int y) throws Exception;
	public ISticker getFieldColor(Point3D location, SideType side);
	
	public List<IField> getFieldsByType(FieldType type);
	public List<IField> getFieldsByColor(IRgb color);
	public List<IField> getFieldsByColors(List<IRgb> colors);
	public List<IField> getFieldsByColors(IRgb ... colors);
	
	public void setSize(int size) throws Exception;
	public int getSize();
	public int getFieldsCount();
	
	public void clearUndo();
	public void clearRedo();
	public void undo(int count) throws Exception;
	public void undo() throws Exception;
	public void redo(int count) throws Exception;
	public void redo() throws Exception;
	public boolean undoPossible();
	public boolean redoPossible();
	
	public boolean isSolved() throws Exception;
}
