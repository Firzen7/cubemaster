package info.firzen.cubemaster2.backend.tests;

import static org.junit.Assert.fail;
import info.firzen.cubemaster2.backend.cube.Cube;
import info.firzen.cubemaster2.backend.cube.Move;
import info.firzen.cubemaster2.backend.cube.enums.BasicMove;
import info.firzen.cubemaster2.backend.cube.enums.SideType;
import info.firzen.cubemaster2.backend.cube.geom.Point;
import info.firzen.cubemaster2.backend.cube.interfaces.ISticker;
import info.firzen.cubemaster2.backend.cube.solver.Shortener;
import info.firzen.cubemaster2.backend.cube.solver.Solver3;
import info.firzen.cubemaster2.backend.exceptions.UnknownException;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class Solver3Test {

	private static final int cubesCount = 500;

	private List<Cube> getRandomCubes() throws Exception {
		return getRandomCubes(cubesCount);
	}
	
	private List<Cube> getRandomCubes(int count) throws Exception {
		final int cubeSize = 3;
		final int movesCount = 50;
		List<Cube> cubes = new ArrayList<Cube>();
		
		for(int i = 0; i < count; i++) {
			Cube cube = new Cube(cubeSize);
			
			int moves = (int) (((double) i / (double) count) * (double) movesCount);

			List<Move> rnd = Move.getRandomMoves(cubeSize, moves);
			cube.doMoves(rnd);
			cube.clearUndo();
			
			cubes.add(cube);
		}
		
		return cubes;
	}

	@Test
	public void beginCrossTest() throws Exception {
		List<Cube> cubes = getRandomCubes();		
		for (Cube cube : cubes) {
			Solver3 solver = new Solver3(cube);

			try {
				solver.solveDownCross();
			} catch (Exception e) {
				e.printStackTrace();
				fail();
			}

			ISticker color = cube.getField(1, 1, 0).getSticker(SideType.UP);

			if(color != cube.getField(0, 1, 2).getSticker(SideType.DOWN)) {
				fail();
			}

			if(color != cube.getField(1, 0, 2).getSticker(SideType.DOWN)) {
				fail();
			}

			if(color != cube.getField(2, 1, 2).getSticker(SideType.DOWN)) {
				fail();
			}

			if(color != cube.getField(1, 2, 2).getSticker(SideType.DOWN)) {
				fail();
			}
		}
	}

	@Test
	public void upCrossTest() throws Exception {
		List<Cube> cubes = getRandomCubes();		
		for (Cube cube : cubes) {

			ISticker color = cube.getFieldColor(SideType.UP, 1, 1);

			Solver3 solver = new Solver3(cube);

			solver.solveDownCross();
			solver.solveUpCross();

			if(cube.getFieldColor(SideType.UP, 1, 0) != color
					|| cube.getFieldColor(SideType.UP, 1, 2) != color
					|| cube.getFieldColor(SideType.UP, 0, 1) != color
					|| cube.getFieldColor(SideType.UP, 2, 1) != color) {
				fail();
			}
		}
	}

	@Test
	public void solveUpCornersTest() throws Exception {
		List<Cube> cubes = getRandomCubes();		
		for (Cube cube : cubes) {

			Solver3 solver = new Solver3(cube);

			solver.solveDownCross();
			solver.solveUpCross();

			
			solver.solveUpCorners();

			ISticker upColor = cube.getFieldColor(SideType.UP, 0, 0);
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < 3; j++) {
					if(upColor != cube.getFieldColor(SideType.UP, i, j)) {
						fail();
					}
				}
			}

			SideType[] sides = new SideType[] {SideType.FRONT, SideType.BACK,
					SideType.RIGHT, SideType.LEFT};
			for(SideType side : sides) {
				ISticker controlColor = cube.getFieldColor(side, 1, 1);
				for(int i = 0; i < 3; i++) {
					if(controlColor != cube.getFieldColor(side, i, 0)) {
						fail();
					}
				}
			}
		}
	}
	
	@Test
	public void solveSecondLayerTest() throws Exception {
		List<Cube> cubes = getRandomCubes();		
		for (Cube cube : cubes) {

			Solver3 solver = new Solver3(cube);

			solver.solveDownCross();
			solver.solveUpCross();
			solver.solveUpCorners();

			cube.rotateCube(BasicMove.RIGHT);
			cube.rotateCube(BasicMove.RIGHT);

			solver.solveSecondLayer();

			for(int i = 0; i < 4; i++) {
				ISticker first = cube.getFieldColor(SideType.FRONT, 0, 1);
				for(int j = 1; j < 3; j++) {
					if(cube.getFieldColor(SideType.FRONT, j, 1) != first) {
						fail();
					}
				}
				cube.rotateCube(BasicMove.UP);
			}
		}
	}
	
	@Test
	public void orientEdgesTest() throws Exception {
		List<Cube> cubes = getRandomCubes();		
		for (Cube cube : cubes) {

			Solver3 solver = new Solver3(cube);

			solver.solveFirstLayer();

			cube.rotateCube(BasicMove.RIGHT);
			cube.rotateCube(BasicMove.RIGHT);

			solver.solveSecondLayer();
			
			solver.makeUpCross();
			solver.orientEdges();
			solver.solveEdges();

			for(int i = 0; i < 4; i++) {
				testColorsOfSide(cube, SideType.FRONT, new Point(1, 1),
						new Point(1, 0), new Point(0, 1), new Point(2, 1));
				cube.rotateCube(BasicMove.UP);
			}

		}
	}
	
	@Test
	public void solveCubeTest() throws Exception {
		List<Cube> cubes = getRandomCubes();		
		for (Cube cube : cubes) {

			Solver3 solver = new Solver3(cube);

			solver.solveFirstLayer();

			cube.rotateCube(BasicMove.RIGHT);
			cube.rotateCube(BasicMove.RIGHT);

			solver.solveSecondLayer();
			solver.solveThirdLayer();

			SideType[] sides = SideType.values();
			for(SideType side : sides) {
				testColorsOfSide(cube, side, new Point(1, 1),
						new Point(0, 0), new Point(0, 1), new Point(0, 2),
						new Point(1, 0), new Point(1, 2), new Point(2, 0),
						new Point(2, 1), new Point(2, 2));
			}
		}
	}
	
	private void testColorsOfSide(Cube cube, SideType side,	ISticker color,
			Point ... coords) throws UnknownException {
		for(Point pt : coords) {
			if(!cube.getFieldColor(side, (int)pt.getX(), (int)pt.getY()).equals(color)) {
				System.out.println("Tested side: " + side);
				System.out.println(cube);
				fail();
				break;
			}
		}
	}
	
	private void testColorsOfSide(Cube cube, SideType side, Point startPos,
			Point ... coords) throws UnknownException {
		testColorsOfSide(cube, side, cube.getFieldColor(side,
				(int)startPos.getX(), (int)startPos.getY()), coords);
	}
	
	@Test
	public void testShorten() throws Exception {
		List<Cube> cubes = getRandomCubes();		
		for (Cube test : cubes) {			
			Solver3 solver = new Solver3(test);
			List<Move> solution = solver.getSolution();
			test.undo(test.getUndoMoves().size());
			
			Shortener s = new Shortener(solution);
			List<Move> shortened = s.getShortenedMoves();

			test.doMoves(shortened);

			if(!test.isSolved()) {
				System.out.println("solution:  " + solution);				
				System.out.println("shortened: " + shortened);				
				System.out.println(test);
				fail();
			}
		}
	}
	
//	@Test
//	public void testDetectDownCross() throws Exception {
//		List<Cube> cubes = getRandomCubes();		
//		for (Cube cube : cubes) {
//
//			Solver3 solver = new Solver3(cube);
//			solver.solveDownCross();
//			
//			BasicMove[] bmoves = BasicMove.values();
//			cube.rotateCube(bmoves[(int) ((Math.random() * bmoves.length) - 1)]);
//			
//			if(!solver.isThereDownCross()) {
//				fail();
//			}
//		}		
//	}

//	@Test
//	public void testDetectUpCross() throws Exception {
//		List<Cube> cubes = getRandomCubes();		
//		for (Cube cube : cubes) {
//
//			Solver3 solver = new Solver3(cube);
//			solver.solveDownCross();
//			solver.solveUpCross();
//
//			BasicMove[] bmoves = BasicMove.values();
//			cube.rotateCube(bmoves[(int) ((Math.random() * bmoves.length) - 1)]);
//
//			if(!solver.isThereUpCross()) {
//				fail();
//			}
//		}		
//	}

//	@Test
//	public void testDetectFirstLayer() throws Exception {
//		List<Cube> cubes = getRandomCubes();		
//		for (Cube cube : cubes) {
//			
//			Solver3 solver = new Solver3(cube);
//			solver.solveDownCross();
//			solver.solveUpCross();
//			solver.solveUpCorners();
//
//			BasicMove[] bmoves = BasicMove.values();
//			cube.rotateCube(bmoves[(int) ((Math.random() * bmoves.length) - 1)]);
//
//			if(!solver.isFirstLayerSolved()) {
//				System.out.println("FIRST LAYER DETECT FAIL\n\n" + cube);
//				fail();
//			}
//		}		
//	}
	
//	@Test
//	public void testDetectSecondLayer() throws Exception {
//		List<Cube> cubes = getRandomCubes();		
//		for (Cube cube : cubes) {
//
//			Solver3 solver = new Solver3(cube);
//			solver.solveFirstLayer();
//			solver.solveSecondLayer();
//
//			// FIXME někdy falešná detekce, bylo by dobré opravit 
////			BasicMove[] bmoves = BasicMove.values();
////			cube.rotateCube(bmoves[(int) ((Math.random() * bmoves.length) - 1)]);
//
//			if(!solver.isSecondLayerSolved()) {
//				System.out.println("SECOND LAYER DETECT FAIL\n\n" + cube);
//				fail();
//			}
//		}
//	}
	
//	@Test
//	public void testDetectFinalUpCross() throws Exception {
//		List<Cube> cubes = getRandomCubes();		
//		for (Cube cube : cubes) {
//			Solver3 solver = new Solver3(cube);
//			solver.solveFirstLayer();
//			solver.solveSecondLayer();
//			solver.makeUpCross();
//
//			BasicMove[] bmoves = BasicMove.values();
//			cube.rotateCube(bmoves[(int) ((Math.random() * bmoves.length) - 1)]);
//
//			if(!solver.isUpCrossSolved()) {
//				System.out.println("UP CROSS DETECT FAIL\n\n" + cube);
//				fail();
//			}
//		}
//	}
	
//	@Test
//	public void testSolvingContinue() throws Exception {
//		List<Cube> cubes = getRandomCubes();		
//		for (Cube cube : cubes) {
//
//			Solver3 solver = new Solver3(cube);
//			BasicMove[] bmoves = BasicMove.values();
//
//			solver.solveDownCross();
//			
//			cube.rotateCube(bmoves[(int) ((Math.random() * bmoves.length) - 1)]);
//
//			solver.solveUpCross();
//			
//			cube.rotateCube(bmoves[(int) ((Math.random() * bmoves.length) - 1)]);
//
//			solver.isFirstLayerSolved();
//			solver.solveUpCorners();
//
//			cube.rotateCube(bmoves[(int) ((Math.random() * bmoves.length) - 1)]);
//			
//			solver.solveSecondLayer();
//
//			if(!solver.isSecondLayerSolved()) {
//				System.out.println("SOLVING CONTINUE FAIL\n\n" + cube);
//				fail();
//			}
//		}
//	}
}
