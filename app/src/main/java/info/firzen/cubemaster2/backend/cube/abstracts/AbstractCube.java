package info.firzen.cubemaster2.backend.cube.abstracts;

import info.firzen.cubemaster2.backend.cube.Move;
import info.firzen.cubemaster2.backend.cube.enums.BasicMove;
import info.firzen.cubemaster2.backend.cube.enums.FieldType;
import info.firzen.cubemaster2.backend.cube.enums.SideType;
import info.firzen.cubemaster2.backend.cube.geom.Point3D;
import info.firzen.cubemaster2.backend.cube.interfaces.ICube;
import info.firzen.cubemaster2.backend.cube.interfaces.IField;
import info.firzen.cubemaster2.backend.cube.interfaces.IRgb;
import info.firzen.cubemaster2.backend.cube.interfaces.ISticker;
import info.firzen.cubemaster2.backend.exceptions.FilterCharsError;
import info.firzen.cubemaster2.backend.exceptions.ParseException;
import info.firzen.cubemaster2.backend.exceptions.PointException;
import info.firzen.cubemaster2.backend.exceptions.UnknownException;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCube implements ICube {
	protected int size;
	protected List<IField> fields = new ArrayList<IField>();
	protected List<Move> undoMoves = new ArrayList<Move>();
	protected List<Move> redoMoves = new ArrayList<Move>();
	
	// -------------------------------------------------------------------------
	// Abstract methods
	// -------------------------------------------------------------------------

	protected abstract void initCube(int size) throws Exception;
	public abstract void doMove(Move move, boolean undo) throws PointException;
	public abstract void doMove(Move move) throws PointException;

	// -------------------------------------------------------------------------
	// Getters and setters
	// -------------------------------------------------------------------------
	
	public List<Move> getUndoMoves() {
		List<Move> moves = new ArrayList<Move>();
		for(Move move : undoMoves) {
			moves.add(move.getCopy());
		}
		return moves;
	}

	public void setUndoMoves(List<Move> undoMoves) {
		this.undoMoves = undoMoves;
	}

	public List<Move> getRedoMoves() {
		List<Move> moves = new ArrayList<Move>();
		for(Move move : redoMoves) {
			moves.add(move.getCopy());
		}
		return moves;
	}

	public void setRedoMoves(List<Move> redoMoves) {
		this.redoMoves = redoMoves;
	}

	public void setSize(int size) throws Exception {
		this.size = size;
	}

	public IField getField(SideType side, int x, int y)
			throws UnknownException {
		switch(side) {
			case BACK: return getField(size - x - 1, 0, y);
			case DOWN: return getField(size - x - 1, size - y - 1, size - 1);
			case FRONT: return getField(x, size - 1, y);
			case LEFT: return getField(0, x, y);
			case RIGHT: return getField(size - 1, size - x - 1, y);
			case UP: return getField(x, y, 0);
			default:
				throw new UnknownException("Impossible just happened!");
		}
	}
	
	public IField getField(int x, int y, int z) {
		return getField(new Point3D(x, y, z));
	}
	
	public IField getField(Point3D location) {
		for(IField field : fields) {
			if(field.getLocation().equals(location)) {
				return field;
			}
		}
		return null;
	}
	
	public List<IField> getFields() {
		return fields;
	}
	
	public ISticker getFieldColor(int x, int y, int z, SideType side) {
		return getFieldColor(new Point3D(x, y, z), side);
	}
	
	public ISticker getFieldColor(SideType side, int x, int y)
			throws UnknownException {
		IField field = getField(side, x, y);
		if(field == null) {
			return null;
		}
		else {
			return field.getSticker(side);
		}
	}
	
	public ISticker getFieldColor(Point3D location, SideType side) {
		IField field = getField(location);
		if(field == null) {
			return null;
		}
		else {
			return field.getSticker(side);
		}
	}
	
	public int getSize() {
		return size;
	}
	
	public int getFieldsCount() {
		return fields.size();
	}
	
	public void setField(IField field) {
		Point3D loc = field.getLocation();
		removeField(getField(loc));
		fields.add(field);
	}
	
	public void removeField(IField field) {
		fields.remove(field);
	}
	
	public void addField(IField field) {
		fields.add(field);
	}
	
	// -------------------------------------------------------------------------
	// Undo and redo
	// -------------------------------------------------------------------------

	public void addUndoMove(Move move) {
		undoMoves.add(move);
	}
	
	public void addRedoMove(Move move) {
		redoMoves.add(move);
	}
	
	public void clearUndo() {
		undoMoves.clear();
	}

	public void clearRedo() {
		redoMoves.clear();
	}
	
	public void undo(int count) throws PointException, UnknownException {
		for(int i = 0; i < count; i++) {
			if(undoPossible()) {
				undo();
			}
			else {
				break;
			}
		}
	}
	
	public void undo() throws PointException, UnknownException {
		if(!undoMoves.isEmpty()) {
			Move move = undoMoves.get(undoMoves.size() - 1);
			doMove(Move.getInvertedMove(move), false);
			undoMoves.remove(undoMoves.size() - 1);
			addRedoMove(move);
		}
	}
	
	public void redo(int count) throws PointException {
		for(int i = 0; i < count; i++) {
			if(redoPossible()) {
				redo();
			}
			else {
				break;
			}
		}
	}
	
	public void redo() throws PointException {
		if(!redoMoves.isEmpty()) {
			Move move = redoMoves.get(redoMoves.size() - 1);
			doMove(move, false);
			redoMoves.remove(redoMoves.size() - 1);
			addUndoMove(move);
		}
	}
	
	public boolean undoPossible() {
		return !undoMoves.isEmpty();
	}

	public boolean redoPossible() {
		return !redoMoves.isEmpty();
	}
	
	// -------------------------------------------------------------------------
	// More complex methods
	// -------------------------------------------------------------------------
	
	public void rotateCube(BasicMove bmove) throws PointException {
		doMove(new Move(bmove, true));
	}
	
	public List<IField> getFieldsAtLevel(Move move) {
		List<IField> output = new ArrayList<IField>();
		SideType side = SideType.parseBasicMove(move.getBasicMove());
		int level = move.getLevel();
		if(side == SideType.DOWN || side == SideType.RIGHT
				|| side == SideType.FRONT) {
			level = size - level - 1;
		}
		
		if(side == SideType.UP || side == SideType.DOWN) {
			for(int y = 0; y < size; y++) {
				for(int x = 0; x < size; x++) {
					IField f = getField(x, y, level);
					if(f != null) {
						output.add(f);
					}
				}
			}
		}
		else if(side == SideType.LEFT || side == SideType.RIGHT) {
			for(int z = 0; z < size; z++) {
				for(int y = 0; y < size; y++) {
					IField f = getField(level, y, z);
					if(f != null) {
						output.add(f);
					}
				}
			}
		}
		else if(side == SideType.FRONT || side == SideType.BACK) {
			for(int z = 0; z < size; z++) {
				for(int x = 0; x < size; x++) {
					IField f = getField(x, level, z);
					if(f != null) {
						output.add(f);
					}
				}
			}
		}
		
		return output;
	}

	public void doMoves(List<Move> moves) throws PointException {
		for(Move m : moves) {
			doMove(m);
		}
	}
	
	public void doMoves(String str) throws PointException, ParseException,
			FilterCharsError {
		List<Move> moves = Move.parseMoves(str);
		for(Move m : moves) {
			doMove(m);
		}
	}
	
	public void doMove(BasicMove move) throws PointException {
		this.doMove(new Move(move));
	}
	
	public List<IField> getFieldsByColor(IRgb color) {
		return getFieldsByColor(color, fields);
	}

	protected List<IField> getFieldsByColor(IRgb color, List<IField> fields) {
		List<IField> output = new ArrayList<IField>();
		
		for(IField field : fields) {
			ISticker[] stickers = field.getStickers();
			for(ISticker sticker : stickers) {
				if(sticker != null && color.equals(sticker.getColor())) {
					output.add(field);
					break;
				}
			}
		}
		
		return output;
	}
	
	public List<IField> getFieldsByColors(List<IRgb> colors) {
		List<IField> output = fields;
		
		for(IRgb color : colors) {
			output = getFieldsByColor(color, output);
		}
		
		return output;
	}
	
	public List<IField> getFieldsByColors(IRgb ... colors) {
		return getFieldsByColors(colors);
	}
	
	public boolean isSideSolved(SideType side) throws UnknownException {
		int size = getSize();
		
		ISticker sideColor = null;
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				ISticker actualColor = getFieldColor(side, i, j);
				if(sideColor == null) {
					sideColor = actualColor;
				}
				if(!actualColor.equals(sideColor)) {
					return false;
				}
			}
		}

		return true;
	}

	public boolean isSolved() throws UnknownException {
		SideType[] sides = new SideType[] {SideType.UP, SideType.DOWN,
				SideType.FRONT, SideType.BACK, SideType.LEFT, SideType.RIGHT};

		for(SideType side : sides) {
			if(!isSideSolved(side)) {
				return false;
			}
		}
		
		return true;
	}

	// FIXME wrong
//	public boolean isValid() {
//		for(IField field : fields) {
//			if(!field.isValid()) {
//				return false;
//			}
//		}
//		
//		List<IField> f1 = getFieldsByColor(Sticker.ONE.getColor());
//		List<IField> f2 = getFieldsByColor(Sticker.TWO.getColor());
//		List<IField> f3 = getFieldsByColor(Sticker.THREE.getColor());
//		List<IField> f4 = getFieldsByColor(Sticker.FOUR.getColor());
//		List<IField> f5 = getFieldsByColor(Sticker.FIVE.getColor());
//		List<IField> f6 = getFieldsByColor(Sticker.SIX.getColor());
//		
//		return f1.size() == f2.size() && f2.size() == f3.size()
//				&& f3.size() == f4.size() && f4.size() == f5.size()
//				&& f5.size() == f6.size();
//	}
	
	public List<IField> getFieldsByType(FieldType type) {
		List<IField> output = new ArrayList<IField>();
		
		for(IField field : fields) {
			if(type.equals(field.getType())) {
				output.add(field);
			}
		}
		
		return output;
	}
}
