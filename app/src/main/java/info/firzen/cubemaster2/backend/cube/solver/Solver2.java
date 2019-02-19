package info.firzen.cubemaster2.backend.cube.solver;

import info.firzen.cubemaster2.backend.cube.Cube;
import info.firzen.cubemaster2.backend.cube.Move;
import info.firzen.cubemaster2.backend.cube.enums.BasicMove;
import info.firzen.cubemaster2.backend.cube.enums.FieldType;
import info.firzen.cubemaster2.backend.cube.enums.SideType;
import info.firzen.cubemaster2.backend.cube.enums.Sticker;
import info.firzen.cubemaster2.backend.cube.geom.Point3D;
import info.firzen.cubemaster2.backend.cube.interfaces.IField;
import info.firzen.cubemaster2.backend.cube.interfaces.ISticker;
import info.firzen.cubemaster2.backend.exceptions.FilterCharsError;
import info.firzen.cubemaster2.backend.exceptions.ParseException;
import info.firzen.cubemaster2.backend.exceptions.PointException;
import info.firzen.cubemaster2.backend.exceptions.SolveException;
import info.firzen.cubemaster2.backend.exceptions.UnknownException;
import info.firzen.cubemaster2.backend.exceptions.UnsupportedCubeSize;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solver2 extends SolverPattern {

	public Solver2(Cube cube) {
		super(cube);
	}

	public List<Move> getSolution() throws Exception {
		if(cube.getSize() == 2) {
			cube.clearUndo();
			solveUpCorners();
			rearrangeDownCorners();
			finnishSolving();
			
			if(!cube.isSolved()) {
				throw new SolveException("Unknown error while solving!");
			}
			else {
				return cube.getUndoMoves();
			}
		}
		else {
			throw new UnsupportedCubeSize();
		}
	}

	private List<ISticker> getRightCornerColors() throws UnknownException {
		List<ISticker> output = new ArrayList<ISticker>();
		output.add(cube.getFieldColor(SideType.UP, 0, 1));
		output.add(cube.getFieldColor(SideType.FRONT, 0, 0));
		return output;
	}
	
	private IField getCornerWithColors(List<ISticker> colors) {
		List<IField> fields = cube.getFields();
		for(IField field : fields) {
			boolean rightCorner = true;
			for(ISticker color : colors) {
				if(!field.getType().equals(FieldType.CORNER)
						|| !field.containsSticker(color)
						|| field.getLocation().equals(new Point3D(0, 1, 0))) {
					rightCorner = false;
					break;
				}
			}
			if(rightCorner) {
				return field;
			}
		}
		return null;
	}

	// -------------------------------------------------------------------------
	// První vrstva
	// -------------------------------------------------------------------------
	
	public void solveUpCorners() throws UnknownException, SolveException,
			PointException, ParseException, FilterCharsError {
		for(int i = 0; i < 4; i++) {
			List<ISticker> cornerColors = getRightCornerColors();
			IField corner = getCornerWithColors(cornerColors);
			Point3D location = corner.getLocation();

			if(location.getZ() == 0) {
				if(location.getX() == 0) {
					if(location.getY() == 0) {
						cube.doMoves("B D D Bi");
					}
					else if(location.getY() == 1) {
						cube.doMoves("L D Li");
					}
					else {
						throw new SolveException("Upper corners solving error!");
					}
				}
				else if(location.getX() == 1) {
					if(location.getY() == 0) {
						cube.doMoves("Bi Di B");
					}
				}
				else {
					throw new SolveException("Upper corners solving error!");
				}
			}
			else if(location.getZ() == 1) {
				if(location.getX() == 0) {
					if(location.getY() == 0) {
						cube.doMoves("D D");
					}
					else if(location.getY() == 1) {
						cube.doMoves("D");
					}
					else {
						throw new SolveException("Upper corners solving error!");
					}
				}
				else if(location.getX() == 1) {
					if(location.getY() == 0) {
						cube.doMoves("Di");
					}
				}
				else {
					throw new SolveException("Upper corners solving error!");
				}
			}
			else {
				throw new SolveException("Upper corners solving error!");
			}

			IField rightCorner = cube.getField(1, 1, 0);
			
			int count = 0;
			while(rightCorner.getSticker(SideType.UP) != cornerColors.get(0)
					|| rightCorner.getSticker(SideType.FRONT) != cornerColors.get(1)) {
				cube.doMoves("Ri Di R D");
				rightCorner = cube.getField(1, 1, 0);
				count++;
				if(count > 10) {
					throw new SolveException("Upper corners solving error!");
				}
			}

			cube.rotateCube(BasicMove.UP);
		}
	}
	
	// -------------------------------------------------------------------------
	// Druhá vrstva
	// -------------------------------------------------------------------------
	// L Ui Ri U Li Ui R U U
	
	// před tímto krokem se kostka musí otočit vzhůru nohama!
	public void rearrangeDownCorners() throws UnknownException,
			PointException, ParseException, SolveException, FilterCharsError {
		placeFirstLayerToDown();
		
		rotateToHighestCornersGroupCount();
		
		ISticker frontColor = cube.getFieldColor(1, 1, 1, SideType.FRONT);
		
		if(cube.getField(SideType.UP, 1, 0).containsSticker(frontColor)) {
			swapCorners(SideType.BACK);
			swapCorners(SideType.LEFT);
		}
		else if(cube.getField(SideType.UP, 0, 0).containsSticker(frontColor)) {
			swapCorners(SideType.LEFT);
		}
		
		cube.rotateCube(BasicMove.UP);
		
		if(getRightCornerColorMatch() != 2) {
			swapCorners(SideType.RIGHT);
		}
	}
	
	/**
	 * Rotuje horní vrstvu kostky tak dlouho, až se pravý dolní roh barevně shoduje
	 * se stranami Front a Right.
	 * @throws UnknownException
	 * @throws PointException
	 */
	private void rotateToHighestCornersGroupCount() throws UnknownException,
			PointException {
		int count = 0;
		int index = 0;
		for(int i = 0; i < 4; i++) {
			int currentCount = getRightCornerColorMatch();
			if(currentCount > count) {
				count = currentCount;
				index = i;
			}
			cube.doMove(new Move(BasicMove.UP));
		}
		for(int i = 0; i < index; i++) {
			cube.doMove(new Move(BasicMove.UP));
		}
	}
	
	/**
	 * Určuje, v kolika barvách se shoduje pravý dolní roh (z pohledu shora)
	 * se stranami Front a Right.
	 * @return int
	 * @throws UnknownException
	 * @throws PointException
	 */
	private int getRightCornerColorMatch() throws UnknownException,
			PointException {
		int count = 0;
		
		IField rightField = cube.getField(SideType.FRONT, 1, 0);
		
		if(rightField.containsSticker(cube.getFieldColor(SideType.FRONT, 1, 1))) {
			count++;
		}
		if(rightField.containsSticker(cube.getFieldColor(SideType.RIGHT, 0, 1))) {
			count++;
		}

		return count;
	}
	
	private void swapCorners(SideType side) throws PointException,
			ParseException, FilterCharsError, SolveException {
		String moves = "L Ui Ri U Li Ui R U U";
		switch(side) {
			case BACK:
				cube.rotateCube(BasicMove.UP);
				cube.doMoves(moves);
				cube.rotateCube(BasicMove.UP_INVERTED);
				break;
			case FRONT:
				cube.rotateCube(BasicMove.UP_INVERTED);
				cube.doMoves(moves);
				cube.rotateCube(BasicMove.UP);
				break;
			case LEFT:
				cube.rotateCube(BasicMove.UP_INVERTED);
				cube.rotateCube(BasicMove.UP_INVERTED);
				cube.doMoves(moves);
				cube.rotateCube(BasicMove.UP);
				cube.rotateCube(BasicMove.UP);
				break;
			case RIGHT:
				cube.doMoves(moves);
				break;
			default:
				throw new SolveException("Cannot switch these corners!");
		}
	}
	
	private Sticker getLastColor() throws UnknownException {
		List<Sticker> colors = new ArrayList<Sticker>();
		colors.addAll(Arrays.asList(new Sticker[] {Sticker.ONE,
				Sticker.TWO, Sticker.THREE, Sticker.FOUR, Sticker.FIVE,
				Sticker.SIX}));

		colors.remove(cube.getFieldColor(SideType.DOWN, 0, 0));
		colors.remove(cube.getFieldColor(SideType.FRONT, 0, 1));
		colors.remove(cube.getFieldColor(SideType.BACK, 0, 1));
		colors.remove(cube.getFieldColor(SideType.LEFT, 0, 1));
		colors.remove(cube.getFieldColor(SideType.RIGHT, 0, 1));

		if(colors.size() == 1) {
			return colors.get(0);
		}
		else {
			throw new UnknownException();
		}
	}
	
	private boolean isRightCornerSolved(Sticker lastColor)
			throws UnknownException {
		return cube.getFieldColor(SideType.UP, 1, 1) == lastColor;
	}
	
	public void finnishSolving() throws UnknownException, SolveException,
			PointException, ParseException, FilterCharsError {
		placeFirstLayerToDown();
		
		Sticker lastColor = getLastColor();
		
		for(int i = 0; i < 4; i++) {
			int count = 0;
			while(!isRightCornerSolved(lastColor)) {
				cube.doMoves("Ri Di R D");
				count++;
				
				if(count > 10) {
					throw new SolveException("Cannot finnish solving!");
				}
			}
			cube.doMove(new Move(BasicMove.UP));
		}
	}
	
	// -------------------------------------------------------------------------
	// Solving phases detection
	// -------------------------------------------------------------------------

	private boolean isRightCornerSolved() throws Exception {
		return getRightCornerColorMatch() == 2;
	}
	
	public void placeFirstLayerToDown() throws PointException,
			UnknownException {
		SideType firstLayer = getCompletedLayer();
		
		switch(firstLayer) {
			case BACK:
				cube.rotateCube(BasicMove.RIGHT); break;
			case FRONT:
				cube.rotateCube(BasicMove.RIGHT_INVERTED); break;
			case LEFT:
				cube.rotateCube(BasicMove.FRONT_INVERTED); break;
			case RIGHT:
				cube.rotateCube(BasicMove.FRONT); break;
			case UP:
				cube.rotateCube(BasicMove.RIGHT);
				cube.rotateCube(BasicMove.RIGHT);
				break;
			default:
				break;
		}
	}
	
	public boolean isSecondLayerOriented()
			throws Exception {
		backup();
		
		placeFirstLayerToDown();
		
		for(int i = 0; i < 4; i++) {
			if(!isRightCornerSolved()) {
				return false;
			}
			cube.rotateCube(BasicMove.UP);
		}
		
		restore();
		
		return true;
	}
	
	private boolean isLayerSolved(SideType side, int level) throws UnknownException {
		if(cube.isSideSolved(side)) {
			List<SideType> sides = Arrays.asList(SideType.values());

			for(SideType verticalSide : sides) {
				if(!verticalSide.equals(side)
						&& !verticalSide.equals(side.getOppositeSide())) {
					ISticker actual = null;
					List<IField> sideFields = cube.getFieldsAtLevel(new Move(side, level));
					
					for(IField field : sideFields) {
						if(field.hasSticker(verticalSide)) {
							if(actual == null) {
								actual = field.getSticker(verticalSide);
							}
							if(actual != null
								&& !actual.equals(field.getSticker(verticalSide))) {
								return false;
							}
						}
					}
				}
			}
			
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isFirstLayerComplete() throws UnknownException {
		return getCompletedLayer() != null;
	}

	public SideType getCompletedLayer() throws UnknownException {
		SideType[] sides = SideType.values();
		for(SideType side : sides) {
			if(isLayerSolved(side, 0)) {
				return side;
			}
		}
		
		return null;
	}
}
