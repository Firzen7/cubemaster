package info.firzen.cubemaster2.backend.tests;

import static org.junit.Assert.fail;
import info.firzen.cubemaster2.backend.cube.Cube;
import info.firzen.cubemaster2.backend.cube.Move;

import java.util.List;

import org.junit.Test;

public class CubeTest {

	@Test
	public void testSetSize() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFieldsAtLevel() {
		fail("Not yet implemented");
	}

	@Test
	public void testDoMoves() throws Exception {
		for(int i = 2; i < 10; i++) {
			int cubeSize = i;
			Cube cubeOrig = new Cube(cubeSize);
			Cube cube = new Cube(cubeSize);
			List<Move> moves = Move.getRandomMoves(cubeSize, 1000);
			List<Move> invertedMoves = Move.getInvertedMoves(moves);
			
			cube.doMoves(moves);
			cube.doMoves(invertedMoves);
			
			if(!cube.equals(cubeOrig)) {
				System.out.println("Moves was: " + moves);
				System.out.println(cube);
				fail();
			}
		}
	}

	@Test
	public void testGetFieldIntIntInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFieldPoint3D() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFields() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFieldColorIntIntIntSideType() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFieldColorPoint3DSideType() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetFieldsCount() {
		fail("Not yet implemented");
	}
	
	@Test
	public void testEquals() throws Exception {
		for(int i = 0; i < 100; i++) {
			Cube cube1 = new Cube(i);
			Cube cube2 = new Cube(i);
			if(!cube1.equals(cube2)) {
				fail();
			}
		}
	}
}
