package info.firzen.cubemaster2.backend.tests;

import info.firzen.cubemaster2.backend.cube.enums.BasicMove;
import info.firzen.cubemaster2.backend.cube.enums.SideType;

import org.junit.Test;

public class MovesTest {
	@Test
	public void testSideTypeRotation() {
		SideType side = SideType.UP;
		
		SideType newSide = side.rotate(BasicMove.UP);
		
		System.out.println(side + " vs " + newSide);
	}

}
