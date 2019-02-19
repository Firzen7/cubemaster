package info.firzen.cubemaster2.gui;

import info.firzen.cubemaster2.backend.cube.Cube;
import info.firzen.cubemaster2.backend.cube.Field;
import info.firzen.cubemaster2.backend.cube.Move;
import info.firzen.cubemaster2.backend.cube.abstracts.AbstractCube;
import info.firzen.cubemaster2.backend.cube.enums.BasicMove;
import info.firzen.cubemaster2.backend.cube.enums.FieldType;
import info.firzen.cubemaster2.backend.cube.enums.MoveDirection;
import info.firzen.cubemaster2.backend.cube.enums.SideType;
import info.firzen.cubemaster2.backend.cube.geom.Point;
import info.firzen.cubemaster2.backend.cube.geom.Point3D;
import info.firzen.cubemaster2.backend.cube.interfaces.IField;
import info.firzen.cubemaster2.backend.cube.interfaces.ISticker;
import info.firzen.cubemaster2.backend.exceptions.CreateException;
import info.firzen.cubemaster2.backend.exceptions.FieldException;
import info.firzen.cubemaster2.backend.exceptions.FilterCharsError;
import info.firzen.cubemaster2.backend.exceptions.ParseException;
import info.firzen.cubemaster2.backend.exceptions.PointException;
import info.firzen.cubemaster2.backend.exceptions.UnknownException;
import info.firzen.cubemaster2.backend.other.Useful;
import info.firzen.cubemaster2.cube.CubeColor;
import info.firzen.cubemaster2.other.Constants;

import java.util.ArrayList;
import java.util.List;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Object3D;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.Texture;
import com.threed.jpct.TextureInfo;
import com.threed.jpct.TextureManager;
import com.threed.jpct.World;

public class Cube3D extends AbstractCube {
	private FrameBuffer buffer;
	private World world;
	private RGBColor backgroundColor;

	private int currentStickerId = 1;

	public Cube3D(int size, World world, FrameBuffer buffer)
			throws FieldException, CreateException, ParseException,
			UnknownException {
		setWorld(world);
		setBuffer(buffer);
		setSize(size);
		try {
			initTextures();
		}
		catch (Exception e) {
			// ignore all texture exceptions
		}
		initCube();
	}
	
	public Cube3D(Cube cube, World world, FrameBuffer buffer)
			throws FieldException, CreateException, ParseException {
		setWorld(world);
		setBuffer(buffer);
		setSize(cube.getSize());
		try {
			initTextures();
		}
		catch (Exception e) {
			// ignore all texture exceptions
		}
		initCube(cube);
	}
	
	public void initTextures() {
		CubeColor[] textures = CubeColor.values();
		for(CubeColor texture : textures) {
			if(TextureManager.getInstance().containsTexture(texture.name()))
				TextureManager.getInstance().removeTexture(texture.name());			
		}
		
		TextureManager.getInstance().addTexture(CubeColor.RED.name(),
				new Texture(32, 32, CubeColor.RED.getRGBColor()));
		TextureManager.getInstance().addTexture(CubeColor.GREEN.name(),
				new Texture(32, 32, CubeColor.GREEN.getRGBColor()));
		TextureManager.getInstance().addTexture(CubeColor.BLUE.name(),
				new Texture(32, 32, CubeColor.BLUE.getRGBColor()));
		TextureManager.getInstance().addTexture(CubeColor.YELLOW.name(),
				new Texture(32, 32, CubeColor.YELLOW.getRGBColor()));
		TextureManager.getInstance().addTexture(CubeColor.ORANGE.name(),
				new Texture(32, 32, CubeColor.ORANGE.getRGBColor()));
		TextureManager.getInstance().addTexture(CubeColor.WHITE.name(),
				new Texture(32, 32, CubeColor.WHITE.getRGBColor()));
		TextureManager.getInstance().addTexture(CubeColor.UNKNOWN.name(),
				new Texture(32, 32, CubeColor.UNKNOWN.getRGBColor()));
		TextureManager.getInstance().addTexture(CubeColor.BLACK.name(),
				new Texture(32, 32, CubeColor.BLACK.getRGBColor()));
	}
	
	private void initCube(Cube cube) throws FieldException, ParseException {
		List<IField> fields = cube.getFields();
		for(IField field : fields) {
			this.addField(getField3D((Field) field));
		}
		setUndoMoves(cube.getUndoMoves());
		setRedoMoves(cube.getRedoMoves());
	}
	
	private void initCube() throws FieldException, CreateException,
			ParseException {
		for(int z = 0; z < size; z++) {
			for(int y = 0; y < size; y++) {
				for(int x = 0; x < size; x++) {
					if(Useful.isCorner(x, y, z, size)) {
							Field3D corner = getCornerFromCoords(x, y, z);
							fields.add(corner);
					}
					else if(Useful.isEdge(x, y, z, size)) {
							Field3D edge = getEdgeFromCoords(x, y, z);
							fields.add(edge);
					}
					else if(Useful.isFlat(x, y, z, size)) {
						Field3D flat = getFlatFromCoords(x, y, z);
						fields.add(flat);
					}
				}
			} 
		}
	}
	
	private Field3D getField3D(Field field) throws FieldException, ParseException {
		Point3D loc = field.getLocation();
		Field3D output = new Field3D(field.getType(), (int) loc.getX(),
				(int) loc.getY(), (int) loc.getZ());
		
		for(int i = 0; i < 6; i++) {
			ISticker sticker = field.getSticker(i);
			if(sticker != null) {
				output.setSticker(i, getSticker((int) loc.getX(), (int) loc.getY(),
						(int) loc.getZ(), i, CubeColor.parseRgb(sticker.getColor())));
			}
			else {
				output.setSticker(i, getSticker((int) loc.getX(), (int) loc.getY(),
						(int) loc.getZ(), i, true));
			}
		}
		
		return output;
	}
	
	private Sticker3D getSticker(int x, int y, int z, int index, boolean black)
			throws ParseException {
		return getSticker(new SimpleVector(x * 2.1 - size + 1, y * 2.1 - size + 1,
				z * 2.1 - size + 1), index, true);
	}
	
	private Sticker3D getSticker(int x, int y, int z, int index) throws ParseException {
		return getSticker(new SimpleVector(x * 2.1 - size + 1, y * 2.1 - size + 1,
				z * 2.1 - size + 1), index, false);
	}
	
	private Sticker3D getSticker(int x, int y, int z, int index, CubeColor color)
			throws ParseException {
		return getSticker(new SimpleVector(x * 2.1 - size + 1, y * 2.1 - size + 1,
				z * 2.1 - size + 1), index, color);
	}
	
	private Sticker3D getSticker(SimpleVector move, int index, CubeColor tcolor)
			throws ParseException {
		Sticker3D box = new Sticker3D(12, world, buffer);
		box.setId(currentStickerId);
		currentStickerId++;
		
		TextureInfo color = new TextureInfo(TextureManager.getInstance()
				.getTextureID(tcolor.name()));
		
		SideType side = SideType.parseIndex(index);
		
		SimpleVector upperLeftFront = new SimpleVector(-1 + move.x,-1 + move.y,-1 + move.z);
		SimpleVector upperRightFront = new SimpleVector(1 + move.x,-1 + move.y,-1 + move.z);
		SimpleVector lowerLeftFront = new SimpleVector(-1 + move.x,1 + move.y,-1 + move.z);
		SimpleVector lowerRightFront = new SimpleVector(1 + move.x,1 + move.y,-1 + move.z);

		SimpleVector upperLeftBack = new SimpleVector(-1 + move.x, -1 + move.y, 1 + move.z);
		SimpleVector upperRightBack = new SimpleVector(1 + move.x, -1 + move.y, 1 + move.z);
		SimpleVector lowerLeftBack = new SimpleVector(-1 + move.x, 1 + move.y, 1 + move.z);
		SimpleVector lowerRightBack = new SimpleVector(1 + move.x, 1 + move.y, 1 + move.z);

		switch(side) {
			case BACK:
				box.addTriangle(upperLeftBack, upperLeftFront, upperRightBack, color);
				box.addTriangle(upperRightBack, upperLeftFront, upperRightFront, color);
				break;
			case DOWN:
				box.addTriangle(upperLeftBack, upperRightBack, lowerLeftBack, color);
				box.addTriangle(upperRightBack, lowerRightBack, lowerLeftBack, color);
				break;
			case FRONT:
				box.addTriangle(lowerLeftBack, lowerRightBack, lowerLeftFront, color);
				box.addTriangle(lowerRightBack, lowerRightFront, lowerLeftFront, color);
				break;
			case LEFT:
				box.addTriangle(upperLeftFront, upperLeftBack, lowerLeftFront, color);
				box.addTriangle(upperLeftBack, lowerLeftBack, lowerLeftFront, color);
				break;
			case RIGHT:
				box.addTriangle(upperRightFront, lowerRightFront, upperRightBack, color);
				box.addTriangle(upperRightBack, lowerRightFront, lowerRightBack, color);
				break;
			case UP:
				box.addTriangle(upperLeftFront, lowerLeftFront, upperRightFront, color);
				box.addTriangle(upperRightFront, lowerLeftFront, lowerRightFront, color);
				break;
			default:
				break;
		}

		box.build();
		
		float center = (float) (Math.pow(2, size - 1) * 0.025);
		box.setColor(tcolor);
		box.setRotationPivot(new SimpleVector(center, center, center));
		box.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
		box.strip();
		
		return box;
	}
	
	private Sticker3D getSticker(SimpleVector move, int index, boolean blackColor)
			throws ParseException {
		SideType side = SideType.parseIndex(index);
		CubeColor color = CubeColor.BLACK;
		
		if(!blackColor) {
			switch(side) {
				case BACK:
					color = CubeColor.RED; break;
				case DOWN:
					color = CubeColor.YELLOW; break;
				case FRONT:
					color = CubeColor.ORANGE; break;
				case LEFT:
					color = CubeColor.BLUE; break;
				case RIGHT:
					color = CubeColor.GREEN; break;
				case UP:
					color = CubeColor.WHITE; break;
				default:
					break;
			}
		}
		
		return getSticker(move, index, color);
	}
	
	private void addBlackStickers(Field3D field) throws FieldException, ParseException {
		Point3D loc = field.getLocation();
		for(int i = 0; i < 6; i++) {
			if(field.getSticker(i) == null) {
				field.setSticker(i, getSticker((int) loc.getX(), (int) loc.getY(),
						(int) loc.getZ(), i, true));
			}
		}
	}
	
	private Field3D getFlatFromCoords(int x, int y, int z)
			throws CreateException, FieldException, ParseException {
		Field3D flat = new Field3D(FieldType.FLAT, x, y, z);
		
		if(x > 0 && x < size - 1 && y > 0 && y < size - 1) {
			if(z == 0) {
				flat.setSticker(5, getSticker(x, y, z, 5));
			}
			else if(z == size - 1) {
				flat.setSticker(4, getSticker(x, y, z, 4));
			}
			else {
				throw new CreateException("These coords are not edge!");
			}
		}
		else if(z > 0 && z < size - 1 && x > 0 && x < size - 1) {
			if(y == size - 1) {
				flat.setSticker(0, getSticker(x, y, z, 0));
			}
			else if(y == 0) {
				flat.setSticker(2, getSticker(x, y, z, 2));
			}
			else {
				throw new CreateException("These coords are not edge!");
			}
		}
		else if(z > 0 && z < size - 1 && y > 0 && y < size - 1) {
			if(x == 0) {
				flat.setSticker(3, getSticker(x, y, z, 3));
			}
			else if(x == size - 1) {
				flat.setSticker(1, getSticker(x, y, z, 1));
			}
			else {
				throw new CreateException("These coords are not edge!");
			}
		}
		else {
			throw new CreateException("These coords are not edge!");
		}
		
		addBlackStickers(flat);
		return flat;
	}
	
	private Field3D getEdgeFromCoords(int x, int y, int z)
			throws FieldException, CreateException, ParseException {
		Field3D edge = new Field3D(FieldType.EDGE, x, y, z);
		
		if(x > 0 && x < size - 1) {
			if(y == 0) {
				if(z == 0) {
					edge.setSticker(5, getSticker(x, y, z, 5));
					edge.setSticker(2, getSticker(x, y, z, 2));
				}
				else if(z == size - 1) {
					edge.setSticker(4, getSticker(x, y, z, 4));
					edge.setSticker(2, getSticker(x, y, z, 2));
				}
				else {
					throw new CreateException("These coords are not edge!");
				}
			} else if(y == size - 1) {
				if(z == 0) {
					edge.setSticker(5, getSticker(x, y, z, 5));
					edge.setSticker(0, getSticker(x, y, z, 0));
				}
				else if(z == size - 1) {
					edge.setSticker(4, getSticker(x, y, z, 4));
					edge.setSticker(0, getSticker(x, y, z, 0));
				}
				else {
					throw new CreateException("These coords are not edge!");
				}
			}
			else {
				throw new CreateException("These coords are not edge!");
			}
		}
		else if(y > 0 && y < size - 1) {
			if(x == 0) {
				if(z == 0) {
					edge.setSticker(5, getSticker(x, y, z, 5));
					edge.setSticker(3, getSticker(x, y, z, 3));
				}
				else if(z == size - 1) {
					edge.setSticker(3, getSticker(x, y, z, 3));
					edge.setSticker(4, getSticker(x, y, z, 4));
				}
				else {
					throw new CreateException("These coords are not edge!");
				}
			} else if(x == size - 1) {
				if(z == 0) {
					edge.setSticker(5, getSticker(x, y, z, 5));
					edge.setSticker(1, getSticker(x, y, z, 1));
				}
				else if(z == size - 1) {
					edge.setSticker(4, getSticker(x, y, z, 4));
					edge.setSticker(1, getSticker(x, y, z, 1));
				}
				else {
					throw new CreateException("These coords are not edge!");
				}
			}
			else {
				throw new CreateException("These coords are not edge!");
			}
		}
		else if(z > 0 && z < size - 1) {
			if(y == 0) {
				if(x == 0) {
					edge.setSticker(3, getSticker(x, y, z, 3));
					edge.setSticker(2, getSticker(x, y, z, 2));
				}
				else if(x == size - 1) {
					edge.setSticker(1, getSticker(x, y, z, 1));
					edge.setSticker(2, getSticker(x, y, z, 2));
				}
				else {
					throw new CreateException("These coords are not edge!");
				}
			} else if(y == size - 1) {
				if(x == 0) {
					edge.setSticker(3, getSticker(x, y, z, 3));
					edge.setSticker(0, getSticker(x, y, z, 0));
				}
				else if(x == size - 1) {
					edge.setSticker(1, getSticker(x, y, z, 1));
					edge.setSticker(0, getSticker(x, y, z, 0));
				}
				else {
					throw new CreateException("These coords are not edge!");
				}
			}
			else {
				throw new CreateException("These coords are not edge!");
			}
		}
		else {
			throw new CreateException("These coords are not edge!");
		}
		
		addBlackStickers(edge);
		return edge;
	}
	
	private Field3D getCornerFromCoords(int x, int y, int z)
			throws FieldException, ParseException, CreateException {
		Field3D corner = new Field3D(FieldType.CORNER, x, y, z);
		
		if(x == 0 && y == 0 && z == 0) {
			corner.setSticker(5, getSticker(x, y, z, 5));
			corner.setSticker(2, getSticker(x, y, z, 2));
			corner.setSticker(3, getSticker(x, y, z, 3));
		}
		else if(x == size - 1 && y == 0 && z == 0) {
			corner.setSticker(5, getSticker(x, y, z, 5));
			corner.setSticker(2, getSticker(x, y, z, 2));
			corner.setSticker(1, getSticker(x, y, z, 1));
		}
		else if(x == 0 && y == size - 1 && z == 0) {
			corner.setSticker(5, getSticker(x, y, z, 5));
			corner.setSticker(3, getSticker(x, y, z, 3));
			corner.setSticker(0, getSticker(x, y, z, 0));
		}
		else if(x == size - 1 && y == size - 1 && z == 0) {
			corner.setSticker(5, getSticker(x, y, z, 5));
			corner.setSticker(0, getSticker(x, y, z, 0));
			corner.setSticker(1, getSticker(x, y, z, 1));
		}
		else if(x == 0 && y == 0 && z == size - 1) {
			corner.setSticker(3, getSticker(x, y, z, 3));
			corner.setSticker(2, getSticker(x, y, z, 2));
			corner.setSticker(4, getSticker(x, y, z, 4));
		}
		else if(x == size - 1 && y == 0 && z == size - 1) {
			corner.setSticker(2, getSticker(x, y, z, 2));
			corner.setSticker(4, getSticker(x, y, z, 4));
			corner.setSticker(1, getSticker(x, y, z, 1));
		}
		else if(x == 0 && y == size - 1 && z == size - 1) {
			corner.setSticker(4, getSticker(x, y, z, 4));
			corner.setSticker(3, getSticker(x, y, z, 3));
			corner.setSticker(0, getSticker(x, y, z, 0));
		}
		else if(x == size - 1 && y == size - 1 && z == size - 1) {
			corner.setSticker(0, getSticker(x, y, z, 0));
			corner.setSticker(4, getSticker(x, y, z, 4));
			corner.setSticker(1, getSticker(x, y, z, 1));
		}
		else {
			throw new CreateException("These coords are not corner!");
		}

		addBlackStickers(corner);
		return corner;
	}

	public void doMove(Sticker3D from, Sticker3D to) throws Exception {
		if(from != null && to != null && from != to && from.getSide() == to.getSide()) {
			SideType side = from.getSide();
			
			Point3D locFrom = from.getParent().getLocation();
			Point3D locTo = to.getParent().getLocation();
			
			doMove(side, locFrom.convertTo2D(side), locTo.convertTo2D(side));
		}
	}
	
	public void doMove(SideType side, Point from, Point to) throws UnknownException,
			PointException {
		Point vector = Point.minus(to, from);
		
		if(isValidMoveVector(vector)) {
			MoveDirection direction = MoveDirection.parseVector(vector);
			BasicMove bmove = determineBasicMove(direction, side);

			if(isVectorInverted(vector)) {
				bmove = BasicMove.getInvertedMove(bmove);
			}
			
			Move move = new Move(bmove, (int) getMoveLevelFromMoveVector(from, vector));
			
			doMove(move);
		}
	}
	
	private BasicMove determineBasicMove(MoveDirection direction, SideType side)
			throws UnknownException {
		switch(side) {
			case BACK:
				switch(direction) {
					case HORIZONTAL: return BasicMove.UP;
					case VERTICAL: return BasicMove.LEFT_INVERTED;
				}
				break;
			case FRONT:
				switch(direction) {
					case HORIZONTAL: return BasicMove.UP_INVERTED;
					case VERTICAL: return BasicMove.LEFT;
				}
				break;
			case LEFT:
				switch(direction) {
					case HORIZONTAL: return BasicMove.UP_INVERTED;
					case VERTICAL: return BasicMove.BACK;
				}
				break;
			case RIGHT:
				switch(direction) {
					case HORIZONTAL: return BasicMove.UP;
					case VERTICAL: return BasicMove.BACK_INVERTED;
				}
				break;
			case DOWN:
				switch(direction) {
					case HORIZONTAL: return BasicMove.BACK;
					case VERTICAL: return BasicMove.LEFT_INVERTED;
				}
				break;
			case UP:
				switch(direction) {
					case HORIZONTAL: return BasicMove.BACK_INVERTED;
					case VERTICAL: return BasicMove.LEFT;
				}
				break;
		}
		
		throw new UnknownException();
	}
	
	public ISticker getSticker(SideType side, Point3D loc) throws UnknownException {
		IField field = getField(loc);
		return field.getSticker(side);
	}
	
	public ISticker getSticker(SideType side, int x, int y) throws UnknownException {
		IField field = getField(side, x, y);
		return field.getSticker(side);
	}
	
	/**
	 * Removes sticker from cube. Please keep in mind that jpct is not thread
	 * safe, and so you must call this method only from rendering thread!
	 * @param side SideType
	 * @param loc Point3D
	 * @throws UnknownException
	 * @throws FieldException 
	 */
	public void removeSticker(SideType side, Point3D loc)
			throws UnknownException, FieldException {
		Sticker3D sticker = (Sticker3D) getSticker(side, loc);
		if(sticker != null) {
			world.removeObject(sticker);
		}
	}
	
	/**
	 * Removes sticker from cube. Please keep in mind that jpct is not thread
	 * safe, and so you must call this method only from rendering thread!
	 * @param side SideType
	 * @param x int
	 * @param y int
	 * @throws UnknownException
	 * @throws FieldException 
	 */
	public void removeSticker(SideType side, int x, int y)
			throws UnknownException, FieldException {
		Sticker3D sticker = (Sticker3D) getSticker(side, x, y);
		if(sticker != null) {
			world.removeObject(sticker);
		}
	}
	
	/**
	 * Sets sticker color on cube. Please keep in mind that jpct is not thread
	 * safe, and so you must call this method only from rendering thread!
	 * @param color TextureColor
	 * @param side SideType
	 * @param x int
	 * @param y int
	 * @throws UnknownException
	 * @throws FieldException
	 * @throws ParseException
	 */
	public void setStickerColor(CubeColor color, SideType side, int x, int y)
			throws UnknownException, FieldException, ParseException {
		System.out.println("SETTING COLOR: " + side + ", " + x + ", " + y);
		
		Field3D field = (Field3D) getField(side, x, y);
		Point3D location = field.getLocation();

		Sticker3D sticker = getSticker((int) location.getX(), (int) location.getY(),
				(int) location.getZ(), side.getIndex(), color);
		
		removeSticker(side, x, y);
		field = (Field3D) getField(side, x, y);

		field.setSticker(side, sticker);

		setField(field);

		world.addObject(sticker);
	}
	
	public void setStickerColor(CubeColor color, SideType side, Point3D loc)
			throws UnknownException, FieldException, ParseException {
		switch(side) {
			case FRONT: setStickerColor(color, side, (int) loc.getX(), (int) loc.getZ());
			break;
			case UP: setStickerColor(color, side, (int) loc.getX(), (int) loc.getY());
			break;
			case LEFT: setStickerColor(color, side, (int) loc.getY(), (int) loc.getZ());
			break;
			case RIGHT: setStickerColor(color, side, (int) (size - 1 - loc.getY()),
					(int) loc.getZ());
				break;
			case BACK: setStickerColor(color, side, (int) (size - 1 - loc.getX()),
					(int) loc.getZ());
				break;
			case DOWN: setStickerColor(color, side, (int) (size - 1 - loc.getX()),
					(int) (size - 1 - loc.getY()));
				break;
			default:
				throw new ParseException("Cannot parse side type " + side);
		}
	}
	
	public synchronized void removeField(IField field) {
		fields.remove(field);
	}
	
	public void setField(IField field) {
		Point3D loc = field.getLocation();
		removeField(getField(loc));
		fields.add(field);
	}
	
	private double getMoveLevelFromMoveVector(Point startPoint, Point vector) {
		if(vector.getX() == 0) {
			return startPoint.getX();
		}
		else {
			return startPoint.getY();
		}
	}
	
	public List<Field3D> getFieldsFromSide(SideType side) throws UnknownException {
		List<Field3D> output = new ArrayList<Field3D>();
		
		for(int y = 0; y < size; y++) {
			for(int x = 0; x < size; x++) {
				output.add((Field3D) getField(side, x, y));
			}
		}
		
		return output;
	}
	
	public List<Sticker3D> getStickersFromSide(SideType side) throws UnknownException {
		List<Sticker3D> output = new ArrayList<Sticker3D>();
		
		for(int y = 0; y < size; y++) {
			for(int x = 0; x < size; x++) {
				output.add((Sticker3D) getSticker(side, x, y));
			}
		}
		
		return output;
	}
	
	private boolean isValidMoveVector(Point vector) {
		return (vector.getX() == 0 && vector.getY() != 0)
				|| (vector.getX() != 0 && vector.getY() == 0);
	}
	
	private boolean isVectorInverted(Point vector) {
		return (vector.getX() < 0 || vector.getY() < 0);
	}
	
	public void doMoves(String str) throws PointException, ParseException,
			FilterCharsError {
		List<Move> moves = Move.parseMoves(str);
		doMoves(moves);
	}

	public void doMove(Move move) throws PointException {
		doMove(move, true);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public FrameBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(FrameBuffer buffer) {
		this.buffer = buffer;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public RGBColor getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(RGBColor backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public Cube getCube() throws Exception {
		Cube output = new Cube();
		output.setSize(size);
		
		for(IField field : fields) {
			output.addField(((Field3D) field).getField());			
		}
		
		for(Move move : undoMoves) {
			output.addUndoMove(move.getCopy());
		}

		for(Move move : redoMoves) {
			output.addRedoMove(move.getCopy());
		}
		
		return output;
	}

	@Override
	protected void initCube(int size) throws Exception {

	}

	@Override
	public void doMove(Move move, boolean undo) throws PointException {
		List<IField> fields = this.fields;
		
		if(!move.isWholeCube()) {
			fields = getFieldsAtLevel(move);
		}

		Point3D sum = new Point3D();
		for(IField field : fields) {
			sum = Point3D.plus(sum, field.getLocation());
		}

		sum = Point3D.divide(sum, fields.size());

		for(IField field : fields) {
			field.rotate(move, sum);
		}

		boolean rotate = true;
		while(rotate) {
			for(IField field : fields) {
				rotate = ((Field3D) field).rotateStep(move);
			}

			try {
				Thread.sleep(Constants.renderingSleepTime);
			} catch (InterruptedException e) {

			}
		}

		for(IField field : fields) {
			((Field3D) field).resetRotations();
		}

		if(undo) {
			addUndoMove(move);
			clearRedo();
		}
	}
}
