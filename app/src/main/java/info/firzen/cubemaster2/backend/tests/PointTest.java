package info.firzen.cubemaster2.backend.tests;

import static org.junit.Assert.fail;
import info.firzen.cubemaster2.backend.cube.geom.Point;

import org.junit.Test;

public class PointTest {
	@Test
	public void testRotate() {
		for(int i = 0; i < 1000000; i++) {
			double x = Math.random() * 1000;
			double y = Math.random() * 1000;
			Point original = new Point(x, y);
			Point point = new Point(x, y);
			Point center = new Point(Math.random() * 1000,
					Math.random() * 1000);
			int angle = 0;
			while(angle % 360 != 0) {
				double rnd = Math.random() * 360;
				if(rnd >= 0.0 && rnd < 0.25) {
					point.rotate(center, 90);					
				}
				else if(rnd >= 0.25 && rnd < 0.5) {
					point.rotate(center, 180);					
				}
				else if(rnd >= 0.5 && rnd < 0.75) {
					point.rotate(center, 270);					
				}
				else {
					point.rotate(center, 360);					
				}
			}
			if(point.getX() != original.getX()
					|| point.getY() != original.getY()) {
				System.out.println("original: " + original);
				System.out.println("rotated: " + point);
				fail();
			}
		}		
	}
}
