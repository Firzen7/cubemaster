package info.firzen.cubemaster2.backend.tests;

import static org.junit.Assert.fail;
import info.firzen.cubemaster2.backend.cube.Cube;
import info.firzen.cubemaster2.backend.cube.Move;
import info.firzen.cubemaster2.backend.cube.enums.BasicMove;
import info.firzen.cubemaster2.backend.cube.enums.SideType;
import info.firzen.cubemaster2.backend.cube.interfaces.ISticker;
import info.firzen.cubemaster2.backend.cube.solver.Shortener;
import info.firzen.cubemaster2.backend.cube.solver.Solver2;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class Solver2Test {

	private static final int cubesCount = 500;

	private List<Cube> getRandomCubes() throws Exception {
		return getRandomCubes(cubesCount);
	}
	
	private List<Cube> getRandomCubes(int count) throws Exception {
		final int cubeSize = 2;
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
	public void testSolveUpCorners() throws Exception {
		List<Cube> cubes = getRandomCubes();		
		for (Cube cube : cubes) {
			Solver2 solver = new Solver2(cube);

			solver.solveUpCorners();

			SideType[] sides = new SideType[] { SideType.FRONT, SideType.BACK,
					SideType.RIGHT, SideType.LEFT };
			for (SideType side : sides) {
				ISticker controlColor = cube.getFieldColor(side, 0, 0);
				for (int i = 0; i < 2; i++) {
					if (controlColor != cube.getFieldColor(side, i, 0)) {
						fail();
					}
				}
			}
		}
	}

	@Test
	public void testSolveCube() throws Exception {
		List<Cube> cubes = getRandomCubes();		
		for (Cube cube : cubes) {
			Solver2 solver = new Solver2(cube);

			solver.solveUpCorners();

			SideType[] sides = new SideType[] { SideType.FRONT, SideType.BACK,
					SideType.RIGHT, SideType.LEFT };
			for (SideType side : sides) {
				ISticker controlColor = cube.getFieldColor(side, 0, 0);
				for (int i = 0; i < 2; i++) {
					if (controlColor != cube.getFieldColor(side, i, 0)) {
						fail();
					}
				}
			}

			cube.clearUndo();
			solver.rearrangeDownCorners();

			cube.clearUndo();
			solver.finnishSolving();

			if (!cube.isSolved()) {
				fail();
			}
		}
	}

	@Test
	public void testSolvingContinue() {
		try {
			List<Cube> cubes = getRandomCubes();		
			for (Cube cube : cubes) {
				Solver2 solver = new Solver2(cube);
				BasicMove[] bmoves = BasicMove.values();

				cube.rotateCube(bmoves[(int) ((Math.random() * bmoves.length) - 1)]);

				solver.solveUpCorners();

				cube.rotateCube(bmoves[(int) ((Math.random() * bmoves.length) - 1)]);

				solver.rearrangeDownCorners();

				cube.rotateCube(bmoves[(int) ((Math.random() * bmoves.length) - 1)]);

				solver.finnishSolving();

				if (!cube.isSolved()) {
					fail();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testDetectSecondLayer() {
		try {
			List<Cube> cubes = getRandomCubes();		
			for (Cube cube : cubes) {
				Solver2 solver = new Solver2(cube);

				solver.solveUpCorners();
				cube.clearUndo();

				solver.rearrangeDownCorners();

				BasicMove[] bmoves = BasicMove.values();
				cube.rotateCube(bmoves[(int) ((Math.random() * bmoves.length) - 1)]);

				if (!solver.isSecondLayerOriented()) {
					fail();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testDetectFirstLayer() {
		try {
			List<Cube> cubes = getRandomCubes();		
			for (Cube cube : cubes) {
				Solver2 solver = new Solver2(cube);

				solver.solveUpCorners();
				cube.clearUndo();

				BasicMove[] bmoves = BasicMove.values();
				cube.rotateCube(bmoves[(int) ((Math.random() * bmoves.length) - 1)]);

				solver.rearrangeDownCorners();
				solver.finnishSolving();

				if (!cube.isSolved()) {
					cube.undo(cube.getUndoMoves().size());
					System.out.println("FAIL: " + cube);

					fail();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testShorten() throws Exception {
		for (int a = 0; a < 100; a++) {

			Cube cube = new Cube(2);
			Cube test = new Cube(2);

			List<Move> rnd = Move.getRandomMoves(2, 50);
			cube.doMoves(rnd);
			cube.clearUndo();
			test.doMoves(rnd);
			test.clearUndo();

			Solver2 solver = new Solver2(cube);
			List<Move> solution = solver.getSolution();

			Shortener s = new Shortener(solution);
			List<Move> shortened = s.getShortenedMoves();

			test.doMoves(shortened);

			if (!test.isSolved()) {
				fail();
			}
		}
	}
}
