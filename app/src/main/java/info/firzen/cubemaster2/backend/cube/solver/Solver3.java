package info.firzen.cubemaster2.backend.cube.solver;

import info.firzen.cubemaster2.backend.cube.Cube;
import info.firzen.cubemaster2.backend.cube.Move;
import info.firzen.cubemaster2.backend.cube.enums.BasicMove;
import info.firzen.cubemaster2.backend.cube.enums.FieldType;
import info.firzen.cubemaster2.backend.cube.enums.RotationAxis;
import info.firzen.cubemaster2.backend.cube.enums.SideType;
import info.firzen.cubemaster2.backend.cube.geom.Point3D;
import info.firzen.cubemaster2.backend.cube.interfaces.IField;
import info.firzen.cubemaster2.backend.cube.interfaces.IRgb;
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

public class Solver3 extends SolverPattern {

	public Solver3(Cube cube) {
		super(cube);
	}

	public List<Move> getSolution() throws UnknownException, PointException,
	UnsupportedCubeSize, ParseException, SolveException, FilterCharsError {
		if(cube.getSize() == 3) {
			cube.clearUndo();
			solveFirstLayer();
			solveSecondLayer();
			solveThirdLayer();
			
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

	private boolean beginCrossComplete() {
		ISticker color = cube.getField(1, 1, 0).getSticker(SideType.UP);

		return color == cube.getField(0, 1, 2).getSticker(SideType.DOWN)
				&& color == cube.getField(1, 0, 2).getSticker(SideType.DOWN)
				&& color == cube.getField(2, 1, 2).getSticker(SideType.DOWN)
				&& color == cube.getField(1, 2, 2).getSticker(SideType.DOWN);
	}

	private List<IField> filterFieldsByTypeAndSide(FieldType type,
			SideType side) throws UnknownException {
		List<IField> fields = cube.getFieldsAtLevel(new Move(side));

		List<IField> output = new ArrayList<IField>();
		for(IField field : fields) {
			if(field.getType().equals(type)) {
				output.add(field);
			}
		}
		return output;
	}
	
	private List<IField> filterFieldsByType(FieldType type) 
			throws UnknownException {
		List<IField> fields = cube.getFields();
		List<IField> output = new ArrayList<IField>();
		for(IField field : fields) {
			if(field.getType().equals(type)) {
				output.add(field);
			}
		}
		return output;
	}

	private List<IField> filterFieldsByColor(List<IField> fields, ISticker color) {
		List<IField> output = new ArrayList<IField>();
		for(IField field : fields) {
			if(field.containsSticker(color)) {
				output.add(field);
			}
		}
		return output;
	}

	// -------------------------------------------------------------------------
	// První vrstva
	// -------------------------------------------------------------------------
	
	public void solveFirstLayer() throws ParseException, PointException,
			UnknownException, SolveException, FilterCharsError {
		solveDownCross();
		solveUpCross();
		solveUpCorners();
	}
	
	public void solveUpCross() throws PointException, ParseException,
			FilterCharsError, UnknownException, SolveException {
		rotateToDownCross();
		
		int solved = 0;
		int count = 0;
		while(solved != 4) {
			ISticker center = cube.getFieldColor(SideType.FRONT, 1, 1);
			ISticker edge = cube.getFieldColor(SideType.FRONT, 1, 2);
			
			if(center == edge) {
				cube.doMoves("F F D");
				solved++;
			}
			else {
				cube.doMoves("U U1");
			}
			count++;
			
			if(count > 30) {
				throw new SolveException("Cannot solve up cross!");
			}
		}
	}
	
	private List<ISticker> getRightCornerColors() throws UnknownException {
		List<ISticker> output = new ArrayList<ISticker>();
		output.add(cube.getFieldColor(SideType.UP, 1, 1));
		output.add(cube.getFieldColor(SideType.FRONT, 1, 0));
		output.add(cube.getFieldColor(SideType.RIGHT, 1, 0));
		return output;
	}
	
	private IField getCornerWithColors(List<ISticker> colors) {
		List<IField> fields = cube.getFields();
		for(IField field : fields) {
			boolean rightCorner = true;
			for(ISticker color : colors) {
				if(!field.getType().equals(FieldType.CORNER)
						|| !field.containsSticker(color)) {
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
	
	public void solveUpCorners() throws UnknownException, SolveException,
			PointException, ParseException, FilterCharsError {
		rotateToUpCross();
		
		for(int i = 0; i < 4; i++) {
			List<ISticker> cornerColors = getRightCornerColors();
			IField corner = getCornerWithColors(cornerColors);
			Point3D location = corner.getLocation();

			if(location.getZ() == 0) {
				if(location.getX() == 0) {
					if(location.getY() == 0) {
						cube.doMoves("B D D Bi");
					}
					else if(location.getY() == 2) {
						cube.doMoves("L D Li");
					}
					else {
						throw new SolveException("Upper corners solving error!");
					}
				}
				else if(location.getX() == 2) {
					if(location.getY() == 0) {
						cube.doMoves("Bi Di B");
					}
				}
				else {
					throw new SolveException("Upper corners solving error!");
				}
			}
			else if(location.getZ() == 2) {
				if(location.getX() == 0) {
					if(location.getY() == 0) {
						cube.doMoves("D D");
					}
					else if(location.getY() == 2) {
						cube.doMoves("D");
					}
					else {
						throw new SolveException("Upper corners solving error!");
					}
				}
				else if(location.getX() == 2) {
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

			int count = 0;
			IField rightCorner = cube.getField(2, 2, 0);
			while(rightCorner.getSticker(SideType.UP) != cornerColors.get(0)
					|| rightCorner.getSticker(SideType.FRONT) != cornerColors.get(1)
					|| rightCorner.getSticker(SideType.RIGHT) != cornerColors.get(2)) {
				cube.doMoves("Ri Di R D");
				rightCorner = cube.getField(2, 2, 0);
				count++;
				
				if(count > 30) {
					throw new SolveException("Upper corners solving error!");
				}
			}
			
			cube.rotateCube(BasicMove.UP);
		}
	}
	
	public void solveDownCross() throws ParseException, PointException,
			UnknownException, SolveException, FilterCharsError {
		// výběr barvy, která se bude skládat (vezme se ploché pole nahoře)
		ISticker startColor = cube.getFieldColor(SideType.UP, 1, 1);

		int count1 = 0;
		while(!beginCrossComplete()) {
			int count2 = 0;
			for(;;) {
				List<IField> edges =
						filterFieldsByColor(filterFieldsByTypeAndSide(FieldType.EDGE,
								SideType.FRONT), startColor);
				List<IField> edgesForCross = new ArrayList<IField>();

				for(IField f : edges) {
					if(!f.hasSticker(SideType.DOWN) 
							|| f.getSticker(SideType.DOWN) != startColor) {
						edgesForCross.add(f);
					}
				}

				if(edgesForCross.isEmpty()) {
					break;
				}
				else {
					IField field = edgesForCross.get(0);
					Point3D location = field.getLocation();

					if(location.getX() == 1) {
						if(location.getZ() == 0) {
							if(field.getSticker(SideType.FRONT).equals(startColor)) {
								doMovesAndProtectDown("F Ri", startColor);
							}
							else {
								doMovesAndProtectDown("F F", startColor);
							}
						}
						else if(location.getZ() == 2) {
							doMovesAndProtectDown("Fi Ri", startColor);
						}
					}
					else if(location.getX() == 0) {
						if(field.getSticker(SideType.FRONT).equals(startColor)) {
							doMovesAndProtectDown("L", startColor);
						}
						else {
							doMovesAndProtectDown("Fi", startColor);
						}
					}
					else {
						if(field.getSticker(SideType.FRONT).equals(startColor)) {
							doMovesAndProtectDown("Ri", startColor);
						}
						else {
							doMovesAndProtectDown("F", startColor);
						}								
					}
				}
				
				if(count2 > 30) {
					throw new SolveException("Cannot solve down cross!");
				}
			}

			cube.rotateCube(BasicMove.DOWN);
			count1++;
			
			if(count1 > 30) {
				throw new SolveException("Cannot solve down cross!");
			}
		}

	}

	private List<IField> getCrossFields(SideType side) throws UnknownException {		
		return filterFieldsByTypeAndSide(FieldType.EDGE, side);
	}
	
	private int getCrossFieldsCount(ISticker color) throws UnknownException {
		List<IField> crossFields = getCrossFields(SideType.DOWN);
		
		int count = 0;
		for(IField field : crossFields) {
			if(field.getSticker(SideType.DOWN).equals(color)) {
				count++;
			}
		}
		
		return count;
	}

	private void doMovesAndProtectDown(String moves, ISticker protectedColor)
			throws ParseException, FilterCharsError, PointException,
			UnknownException, SolveException {		
		List<Move> parsedMoves = Move.parseMoves(moves);

		boolean finished = false;
		int size = parsedMoves.size();
		for(int j = 0; j < size; j++) {
			Move move = parsedMoves.get(j);
			int protectedFields = getCrossFieldsCount(protectedColor);
			
			// XXX
			for(int i = 0; i < 20; i++) {
				cube.doMove(move);
		
				int newFieldsCount = getCrossFieldsCount(protectedColor);
	
				if((j + 1 == size && (newFieldsCount > protectedFields))
						|| ((j + 1 != size) && (newFieldsCount >= protectedFields))) {
					finished = true;
					break;
				}
				else {
					cube.doMove(Move.getInvertedMove(move));
					cube.doMoves("D");
				}
			}
		}

		if(!finished) {
			throw new SolveException("Cross solving failed!");
		}
	}
	
	// -------------------------------------------------------------------------
	// Druhá vrstva
	// -------------------------------------------------------------------------

	public void solveSecondLayer() throws PointException, UnknownException,
			SolveException, ParseException, FilterCharsError {
		placeFirstLayerToDown();
		
		int count = 0;
		while(!rotateToUnsolvedEdge()) {
			List<ISticker> colors = new ArrayList<ISticker>();
			colors.add(cube.getFieldColor(SideType.FRONT, 1, 1));
			colors.add(cube.getFieldColor(SideType.RIGHT, 1, 1));

			IField edge = getEdgeWithColors(colors);
			Point3D location = edge.getLocation();

			if(location.getZ() == 0) {
				// umístění dílku na stranu FRONT
				if(location.getX() == 0) {
					cube.doMoves("Ui");
				}
				else if(location.getX() == 1) {
					if(location.getY() == 0) {
						cube.doMoves("U U");
					}
				}
				else {
					cube.doMoves("U");
				}

				// zařazení dílku z FRONT na správné místo
				if(cube.getFieldColor(SideType.FRONT, 1, 0)
						== cube.getFieldColor(SideType.FRONT, 1, 1)) {
					cube.doMoves("U R Ui Ri Ui Fi U F");
				}
				else {
					cube.doMoves("Ui");
					cube.rotateCube(BasicMove.UP);
					cube.doMoves("Ui Li U L U F Ui Fi");
				}
			}
			else if(location.getZ() == 1) {
				if(location.getY() == 0) {
					if(location.getX() == 0) {
						cube.rotateCube(BasicMove.UP);
						cube.rotateCube(BasicMove.UP);
						cube.doMoves("U R Ui Ri Ui Fi U F");
						cube.rotateCube(BasicMove.UP_INVERTED);
						cube.rotateCube(BasicMove.UP_INVERTED);
					}
					else if(location.getX() == 2) {
						cube.rotateCube(BasicMove.UP);
						cube.doMoves("U R Ui Ri Ui Fi U F");
						cube.rotateCube(BasicMove.UP_INVERTED);
					}
				}
				else if(location.getY() == 2) {
					if(location.getX() == 0) {
						cube.rotateCube(BasicMove.UP_INVERTED);
						cube.doMoves("U R Ui Ri Ui Fi U F");
						cube.rotateCube(BasicMove.UP);					
					}
					else if(location.getX() == 2) {
						cube.doMoves("U R Ui Ri Ui Fi U F");					
					}
				}
				else {
					throw new SolveException("Solving second layer failed!");
				}

				solveSecondLayer();
			}
			else {
				throw new SolveException("Solving second layer failed!");
			}
			count++;
			
			if(count > 30) {
				throw new SolveException("Solving second layer failed!");
			}
		}
	}
	
	/**
	 * Rotuje kostkou, dokud nenalezne nějakou nesloženou hranu vpravo.
	 * Pokud je už druhá vrstva složená, vrací true, pokud ještě není, vrací
	 * false.
	 * @return boolean
	 * @throws UnknownException
	 * @throws PointException
	 */
	private boolean rotateToUnsolvedEdge() throws UnknownException,
			PointException {
		int rotations = 0;
		while(cube.getFieldColor(SideType.FRONT, 2, 1)
				== cube.getFieldColor(SideType.FRONT, 1, 1)
				&& cube.getFieldColor(SideType.RIGHT, 0, 1)
				== cube.getFieldColor(SideType.RIGHT, 1, 1)) {
			cube.rotateCube(BasicMove.UP);
			if(rotations >= 4) {
				return true;
			}
			rotations++;
		}
		return false;
	}
	
	private IField getEdgeWithColors(List<ISticker> colors)
			throws UnknownException {
		List<IField> edges = filterFieldsByType(FieldType.EDGE);
		for(IField edge : edges) {
			boolean ok = true;
			for(ISticker color : colors) {
				if(!edge.containsSticker(color)) {
					ok = false;
					break;
				}
			}
			if(ok) {
				return edge;
			}
		}
		return null;
	}
	
	// -------------------------------------------------------------------------
	// Třetí vrstva
	// -------------------------------------------------------------------------

	public void solveThirdLayer() throws PointException, ParseException,
			UnknownException, FilterCharsError, SolveException {
		makeUpCross();
		orientEdges();
		solveEdges();
		solveCorners();
		finalizeCorners();
		finalizeLayers();
	}
	
	public void finalizeCorners() throws PointException, ParseException,
			FilterCharsError, UnknownException, SolveException {
		for(int i = 0; i < 4; i++) {
			List<ISticker> cornerColors = getRightCornerColors();
			IField rightCorner = cube.getField(2, 2, 0);
			int count = 0;
			while(rightCorner.getSticker(SideType.UP) != cornerColors.get(0)
					|| rightCorner.getSticker(SideType.FRONT) != cornerColors.get(1)
					|| rightCorner.getSticker(SideType.RIGHT) != cornerColors.get(2)) {
				cube.doMoves("Ri Di R D");
				rightCorner = cube.getField(2, 2, 0);
				
				count++;
				if(count > 30) {
					throw new SolveException("Cannot finalize corners!");
				}
			}
			cube.doMoves("U");
		}
	}
	
	public int solvedLayersCount() throws UnknownException {
		final int size = cube.getSize();
		List<IRgb> uniqueColors = new ArrayList<IRgb>();
		
		for(int i = 0; i < size; i++) {
			IRgb color = cube.getFieldColor(SideType.FRONT, 1, i).getColor();
			if(!uniqueColors.contains(color)) {
				uniqueColors.add(color);
			}
		}
		
		return size - uniqueColors.size() + 1;
	}
	
	public void finalizeLayers() throws PointException, UnknownException,
			SolveException {
		final int size = cube.getSize();

		int solved = solvedLayersCount();
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < 4; j++) {
				cube.doMove(new Move(BasicMove.UP, i));
				if(solvedLayersCount() > solved) {
					solved = solvedLayersCount();
					break;
				}
			}
		}
		
		if(solved != size) {
			throw new SolveException("Finalize layers failed!");
		}
	}
	
	public void solveCorners() throws UnknownException, PointException,
			ParseException, FilterCharsError, SolveException {
		boolean rightCorner = false;
		int count = 0;
		while(!areAllCornersRight()) {
			if(!rightCorner) {
				rightCorner = rotateRightCornerToFront();
			}
			cube.doMoves("U R Ui Li U Ri Ui L");
			count++;
			
			if(count > 30) {
				throw new SolveException("Solving corners failed!");
			}
		}
	}
	
	private boolean rotateRightCornerToFront() throws UnknownException,
			PointException {
		for(int i = 0; i < 4; i++) {
			if(getCornerWithColors(getRightCornerColors()).getLocation()
					.equals(new Point3D(2, 2, 0))) {
				return true;
			}
			cube.rotateCube(BasicMove.UP);
		}
		return false;
	}
	
	private boolean areAllCornersRight() throws PointException,
			UnknownException {
		// tady to MUSÍ být s resultem, protože jinak se kostka nezrotuje vždy 4x,
		// a tudíž se mění pozice strany FRONT!
		boolean result = true;
		for(int i = 0; i < 4; i++) {
			if(!getCornerWithColors(getRightCornerColors()).getLocation()
				.equals(new Point3D(2, 2, 0))) {
				result = false;
			}
			cube.rotateCube(BasicMove.UP);
		}
		return result;
	}
	
	public void solveEdges() throws UnknownException, PointException,
			ParseException, FilterCharsError, SolveException {
		int count = 0;
		
		while(!areAllSidesOriented()) {
			rotateToOrientedFront();

			if(!isSideOriented(SideType.LEFT) && !isSideOriented(SideType.BACK)
					&& isSideOriented(SideType.RIGHT)) {
				cube.rotateCube(BasicMove.UP_INVERTED);
				cube.doMoves("R U Ri U R U U Ri");
				rotateToOrientedFront();
			}
			else if(!isSideOriented(SideType.RIGHT) && !isSideOriented(SideType.BACK)
					&& isSideOriented(SideType.LEFT)) {
				cube.rotateCube(BasicMove.UP);
				cube.rotateCube(BasicMove.UP);
				cube.doMoves("R U Ri U R U U Ri");
				rotateToOrientedFront();
			}
			else if(!isSideOriented(SideType.RIGHT) && !isSideOriented(SideType.LEFT)
					&& isSideOriented(SideType.BACK)) {
				cube.rotateCube(BasicMove.UP);
				cube.rotateCube(BasicMove.UP);
				cube.doMoves("R U Ri U R U U Ri");
				cube.rotateCube(BasicMove.UP_INVERTED);
				cube.rotateCube(BasicMove.UP_INVERTED);
				rotateToOrientedFront();
				cube.rotateCube(BasicMove.UP_INVERTED);
				cube.doMoves("R U Ri U R U U Ri");
				cube.rotateCube(BasicMove.UP);
				rotateToOrientedFront();
				cube.rotateCube(BasicMove.UP);
				cube.rotateCube(BasicMove.UP);
				cube.doMoves("R U Ri U R U U Ri");
				rotateToOrientedFront();
			}
			else {
				cube.rotateCube(BasicMove.UP_INVERTED);
				cube.doMoves("R U Ri U R U U Ri");
				cube.rotateCube(BasicMove.UP);
				rotateToOrientedFront();
				solveEdges();
			}
			
			count++;
			
			if(count > 30) {
				throw new SolveException("Cannot solve edges!");
			}
		}
	}
	
	public void orientEdges() throws UnknownException, PointException,
			ParseException, FilterCharsError {
		int maxOriented = 0;
		int rotations = 0;
		for(int i = 0; i < 4; i++) {
			cube.doMoves("U");
			int currentCount = getOrientedEdgesCount();
			if(currentCount > maxOriented) {
				maxOriented = currentCount;
				rotations = i;
			}
		}
		for(int i = 0; i < rotations; i++) {
			cube.doMoves("Ui");
		}
	}
	
	private void rotateToOrientedFront() throws UnknownException,
			PointException, ParseException, FilterCharsError, SolveException {
		int count = 0;
		while(!isSideOriented(SideType.FRONT)) {
			cube.doMoves("U");
			count++;
			
			if(count > 10) {
				throw new SolveException("Cannot rotate to oriented front side!");
			}
		}
	}
	
	private boolean areAllSidesOriented() throws UnknownException {
		return isSideOriented(SideType.FRONT) && isSideOriented(SideType.LEFT)
				&& isSideOriented(SideType.RIGHT) && isSideOriented(SideType.BACK);
	}
	
	private boolean isSideOriented(SideType side) throws UnknownException {
		ISticker start = cube.getFieldColor(side, 1, 0);
		for(int j = 1; j < 2; j++) {
			if(cube.getFieldColor(side, 1, j) != start) {
				return false;
			}
		}
	
		return true;
	}
	
	private int getOrientedEdgesCount() throws UnknownException {
		int count = 0;
		SideType[] sides = new SideType[] {SideType.FRONT, SideType.RIGHT,
				SideType.LEFT, SideType.BACK};
		for(SideType side : sides) {
			if(isSideOriented(side)) {
				count++;
			}
		}
		return count++;
	}
	
	public void makeUpCross() throws PointException, ParseException,
			FilterCharsError, UnknownException, SolveException {
		int count = 0;
		while(!hasSideCross(SideType.UP)) {
			int i = 0;
			while(!hasReversedL(SideType.UP) && i < 4) {
				cube.rotateCube(BasicMove.UP);
				i++;
			}
			cube.doMoves("F R U Ri Ui Fi");
			count++;
			
			if(count > 30) {
				throw new SolveException("Cannot make up cross!");
			}
		}
	}
	
	private boolean hasReversedL(SideType side) throws UnknownException {
		ISticker first = cube.getFieldColor(side, 1, 1);
		return cube.getFieldColor(side, 1, 0) == first
				&& cube.getFieldColor(side, 0, 1) == first;
	}
	
	/*
	private boolean hasLine(SideType side) throws ImpossibleException {
		CubeColor first = cube.getFieldColor(side, 1, 1);
		boolean isHere = true;
		for(int i = 0; i < 3; i++) {
			if(cube.getFieldColor(side, i, 1) != first) {
				isHere = false;
			}
		}
		if(isHere) {
			return true;
		}
		for(int i = 0; i < 3; i++) {
			if(cube.getFieldColor(side, 1, i) != first) {
				isHere = false;
			}
		}
		return isHere;
	}
	*/
	
	private boolean hasSideCross(SideType side) throws UnknownException {
		ISticker first = cube.getFieldColor(side, 1, 1);
		return cube.getFieldColor(side, 0, 1) == first
				&& cube.getFieldColor(side, 1, 0) == first
				&& cube.getFieldColor(side, 2, 1) == first
				&& cube.getFieldColor(side, 1, 2) == first;
	}
	
	
	// -------------------------------------------------------------------------
	// Solving phases detection
	// -------------------------------------------------------------------------

	private boolean areStickersEqual(ISticker ... stickers) {
		if(stickers != null && stickers.length > 0) {
			ISticker control = stickers[0];
			for(ISticker s : stickers) {
				if(!control.equals(s)) {
					return false;
				}
			}
			return true;
		}
		else {
			return true;
		}
	}
	
	private boolean hasUpFrontCrossVerticalLine() throws UnknownException {
		ISticker s1 = cube.getFieldColor(SideType.FRONT, 1, 0);
		ISticker s2 = cube.getFieldColor(SideType.FRONT, 1, 1);
		return s1.equals(s2);
	}
	
	private boolean hasUpCross() throws UnknownException, PointException {
		boolean result = true;
		for(int i = 0; i < 4; i++) {
			if(!hasUpFrontCrossVerticalLine()) {
				result = false;
			}
			cube.rotateCube(BasicMove.UP);
		}
		
		if(!result) {
			return false;
		}
		
		return areStickersEqual(cube.getFieldColor(SideType.UP, 1, 1),
				cube.getFieldColor(SideType.UP, 1, 0),
				cube.getFieldColor(SideType.UP, 0, 1),
				cube.getFieldColor(SideType.UP, 2, 1),
				cube.getFieldColor(SideType.UP, 1, 2));		
	}
	
	private boolean hasDownCross() throws UnknownException {
		ISticker up = cube.getFieldColor(SideType.UP, 1, 1);
		
		ISticker s1 = cube.getFieldColor(SideType.DOWN, 1, 0);
		ISticker s2 = cube.getFieldColor(SideType.DOWN, 0, 1);
		ISticker s3 = cube.getFieldColor(SideType.DOWN, 2, 1);
		ISticker s4 = cube.getFieldColor(SideType.DOWN, 1, 2);
		
		return up.equals(s1) && up.equals(s2) && up.equals(s3) && up.equals(s4);
	}
	
	public void rotateToUpCross() throws PointException, UnknownException {		
		for(int i = 0; i < 4; i++) {
			if(hasUpCross()) {
				return;
			}
			
			cube.rotateCube(BasicMove.RIGHT);
		}
		
		for(int i = 0; i < 4; i++) {			
			if(hasUpCross()) {
				return;
			}
			
			cube.rotateCube(BasicMove.FRONT);			
		}
	}
	
	public void rotateToDownCross() throws PointException, UnknownException {
		for(int i = 0; i < 4; i++) {
			if(hasDownCross()) {
				return;
			}
			
			cube.rotateCube(BasicMove.RIGHT);
		}
		
		for(int i = 0; i < 4; i++) {
			if(hasDownCross()) {
				return;
			}
			
			cube.rotateCube(BasicMove.FRONT);			
		}
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
	
	public SideType getCompletedLayers(int ... levels) throws UnknownException,
			ParseException {
		SideType found = null;
		for(int level : levels) {
			SideType s = getCompletedLayer(level);
			if(s == null) {
				return null;
			}
			else {
				if(found == null) {
					found = s;
				}
				else if(RotationAxis.parseSideType(found)
						!= RotationAxis.parseSideType(s)) {
					return null;
				}
			}
		}
		return found;
	}
	
	private SideType getCompletedLayer(int level) throws UnknownException {
		SideType[] sides = SideType.values();
		for(SideType side : sides) {
			if(isLayerSolved(side, level)) {
				return side;
			}
		}
		
		return null;
	}
	
	public SideType getCompletedLayer() throws UnknownException {
		return getCompletedLayer(0);
	}
	
	public void placeFirstLayerToUp() throws PointException,
			UnknownException {
		SideType firstLayer = getCompletedLayer();

		if(firstLayer != null) {
			switch(firstLayer) {
			case BACK:
				cube.rotateCube(BasicMove.RIGHT_INVERTED); break;
			case FRONT:
				cube.rotateCube(BasicMove.RIGHT); break;
			case LEFT:
				cube.rotateCube(BasicMove.FRONT); break;
			case RIGHT:
				cube.rotateCube(BasicMove.FRONT_INVERTED); break;
			case DOWN:
				cube.rotateCube(BasicMove.RIGHT);
				cube.rotateCube(BasicMove.RIGHT);
				break;
			default:
				break;
			}
		}
	}

	public void placeFirstLayerToDown() throws PointException,
			UnknownException {
		SideType firstLayer = getCompletedLayer();

		if(firstLayer != null) {
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
	}
	
	public void placeSecondLayerToDown() throws PointException,
			UnknownException, ParseException {
		SideType firstLayer = getCompletedLayers(0, 1);

		if(firstLayer != null) {
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
	}

	public void placeSecondLayerToUp() throws PointException,
			UnknownException, ParseException {
		SideType firstLayer = getCompletedLayers(0, 1);

		if(firstLayer != null) {
			switch(firstLayer) {
			case BACK:
				cube.rotateCube(BasicMove.RIGHT_INVERTED); break;
			case FRONT:
				cube.rotateCube(BasicMove.RIGHT); break;
			case LEFT:
				cube.rotateCube(BasicMove.FRONT); break;
			case RIGHT:
				cube.rotateCube(BasicMove.FRONT_INVERTED); break;
			case DOWN:
				cube.rotateCube(BasicMove.RIGHT);
				cube.rotateCube(BasicMove.RIGHT);
				break;
			default:
				break;
			}
		}
	}
	
	/**
	 * Checks if there are some opposite sides where one has some color C
	 * in its center and second one has cross with missing center composed
	 * from fields of color C.
	 * @return boolean
	 * @throws UnknownException 
	 * @throws PointException 
	 */
	public boolean isThereDownCross() {
		try {
			backup();
			rotateToDownCross();
			boolean result = hasDownCross();
			restore();
			return result;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public boolean isThereUpCross() {
		try {
			backup();
			rotateToUpCross();
			boolean result = hasUpCross();
			restore();
			return result;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public boolean isFirstLayerSolved() {
		try {
			backup();
			SideType side = getCompletedLayer();

			if(side == null) {
				restore();
				return false;
			}

			placeFirstLayerToUp();

			boolean result = isLayerSolved(SideType.UP, 0) && hasUpCross();
			restore();
			return result;
		}
		catch (Exception e) {
			return false;
		}
	}
	
	public boolean isSecondLayerSolved() {
		try {
			backup();

			SideType side = getCompletedLayer();

			if(side == null) {
				restore();
				return false;
			}

			placeSecondLayerToUp();

			boolean result = isLayerSolved(SideType.UP, 1)
					&& isLayerSolved(SideType.UP, 0) && hasUpCross();

			restore();

			return result;
		}
		catch (Exception e) {
			return false;
		}
	}

	public boolean isUpCrossSolved() {
		try {
			boolean second = isSecondLayerSolved();
			if(second) {
				backup();
				placeSecondLayerToUp();
				boolean result = hasSideCross(SideType.UP);
				restore();
				return result;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e) {
			return false;
		}
	}

	public boolean areEdgesSolved() throws UnknownException, PointException,
	ParseException {
		try {
			backup();
			placeSecondLayerToUp();
			boolean result = areAllSidesOriented();
			restore();
			return result;
		}
		catch (Exception e) {
			return false;
		}
	}

	public boolean areCornersSolved() throws PointException, UnknownException {
		try {
			for(int i = 0; i < 4; i++) {
				if(areAllCornersRight()) {
					return true;
				}
				cube.rotateCube(BasicMove.RIGHT);
			}

			for(int i = 0; i < 4; i++) {
				if(areAllCornersRight()) {
					return true;
				}
				cube.rotateCube(BasicMove.FRONT);			
			}

			return false;
		}
		catch (Exception e) {
			return false;
		}
	}
}

