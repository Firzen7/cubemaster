package info.firzen.cubemaster2.backend.tests;

import static org.junit.Assert.fail;
import info.firzen.cubemaster2.backend.other.Useful;

import org.junit.Test;

public class UsefulTest {

	@Test
	public void testIsCorner() {
		if(!Useful.isCorner(0, 0, 0, 1)) {
			fail();
		}
		
		for(int size = 0; size < 100; size++) {
			for(int i = 0; i < 1; i++) {
				for(int j = 0; j < 1; j++) {
					for(int k = 0; k < 1; k++) {
						if(!Useful.isCorner(i * size, j * size, k * size, size)) {
							fail();
						}
					}
				}
			}
		}
		
		for(int size = 0; size < 100; size++) {
			for(int i = 1; i < size - 1; i++) {
				for(int j = 1; j < size - 1; j++) {
					for(int k = 1; k < size - 1; k++) {
						if(Useful.isCorner(i, j, k, size)) {
							fail();
						}
					}
				}
			}
		}
	}
	
	@Test
	public void complexTest() {

		for(int size = 0; size < 100; size++) {
			int inner = 0;
			
			for(int i = 0; i < size; i++) {
				for(int j = 0; j < size; j++) {
					for(int k = 0; k < size; k++) {
//						System.out.print("[" + i + ", " + j + ", " + k + "]");
						
						if(Useful.isCorner(i, j, k, size)) {
//							System.out.println(" corner");
						}
						else if(Useful.isEdge(i, j, k, size)) {
//							System.out.println(" edge");
						}
						else if(Useful.isFlat(i, j, k, size)) {
//							System.out.println(" flat");
						}
						else {
							inner++;
						}
					}
				}
			}
			
			if(size >= 3 && inner != Math.pow(size - 2, 3)) {
				System.out.println("size: " + (size - 2));
				System.out.println(Math.pow(size - 2, 3) + " == " + inner);
				fail();
			}
		}
	}
}
