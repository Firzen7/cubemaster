package info.firzen.cubemaster2.backend.tests;

import static org.junit.Assert.fail;
import info.firzen.cubemaster2.backend.cube.Field;
import info.firzen.cubemaster2.backend.cube.Move;
import info.firzen.cubemaster2.backend.cube.enums.BasicMove;
import info.firzen.cubemaster2.backend.cube.enums.Sticker;
import info.firzen.cubemaster2.backend.cube.enums.FieldType;
import info.firzen.cubemaster2.backend.cube.geom.Point3D;
import info.firzen.cubemaster2.backend.exceptions.FieldException;
import info.firzen.cubemaster2.backend.exceptions.PointException;

import org.junit.Test;

public class FieldTest {

	@Test
	public void testContainsColor() throws FieldException {
		Field flat = new Field(FieldType.FLAT);
		flat.setSticker(0, Sticker.ONE);
		boolean res = flat.containsSticker(Sticker.TWO);
		if(res) {
			fail();
		}
		
		Field edge = new Field(FieldType.EDGE);
		edge.setSticker(0, Sticker.ONE);
		edge.setSticker(1, Sticker.TWO);
		boolean res2 = edge.containsSticker(Sticker.TWO);
		if(!res2) {
			fail();
		}

		Field corner = new Field(FieldType.CORNER);
		corner.setSticker(0, Sticker.ONE);
		corner.setSticker(1, Sticker.TWO);
		corner.setSticker(2, Sticker.SIX);
		boolean res3 = corner.containsSticker(Sticker.FIVE);
		if(res3) {
			fail();
		}
	}

	@Test
	public void testRotateBasicMove() throws FieldException, PointException {
		Field field = new Field(FieldType.FLAT);
		field.setSticker(0, Sticker.TWO);
		Point3D center = new Point3D();
		
		Sticker sides[]
			= new Sticker[] {Sticker.TWO, null, null, null, null, null};
		
		field.rotate(BasicMove.RIGHT, center);
		field.rotate(BasicMove.FRONT, center);
		field.rotate(BasicMove.FRONT, center);
		field.rotate(BasicMove.RIGHT_INVERTED, center);
		field.rotate(BasicMove.LEFT, center);
		field.rotate(BasicMove.UP_INVERTED, center);
		field.rotate(BasicMove.BACK, center);
		field.rotate(BasicMove.DOWN, center);
		
		for(int i = 0; i < 6; i++) {
			if(sides[i] != field.getStickers()[i]) {
				fail();
			}
		}
		
		// --------------------------------------------------------------------
		
		Field field2 = new Field(FieldType.EDGE);
		field2.setSticker(0, Sticker.TWO);
		field2.setSticker(5, Sticker.ONE);
		
		Sticker sides2[]
			= new Sticker[] {Sticker.TWO, null, null, null, null, Sticker.ONE};
		
		field2.rotate(BasicMove.RIGHT, center);
		field2.rotate(BasicMove.FRONT, center);
		field2.rotate(BasicMove.FRONT, center);
		field2.rotate(BasicMove.RIGHT_INVERTED, center);
		field2.rotate(BasicMove.LEFT, center);
		field2.rotate(BasicMove.UP_INVERTED, center);
		field2.rotate(BasicMove.BACK, center);
		field2.rotate(BasicMove.DOWN, center);
		
		for(int i = 0; i < 6; i++) {
			if(sides2[i] != field2.getStickers()[i]) {
				fail();
			}
		}
		
		// --------------------------------------------------------------------
		
		Field field3 = new Field(FieldType.CORNER);
		field3.setSticker(0, Sticker.TWO);
		field3.setSticker(5, Sticker.ONE);
		field3.setSticker(1, Sticker.THREE);
		
		Sticker sides3[]
			= new Sticker[] {Sticker.TWO, Sticker.THREE,
				null, null, null, Sticker.ONE};
		
		field3.rotate(BasicMove.RIGHT, center);
		field3.rotate(BasicMove.FRONT, center);
		field3.rotate(BasicMove.FRONT, center);
		field3.rotate(BasicMove.RIGHT_INVERTED, center);
		field3.rotate(BasicMove.LEFT, center);
		field3.rotate(BasicMove.UP_INVERTED, center);
		field3.rotate(BasicMove.BACK, center);
		field3.rotate(BasicMove.DOWN, center);
		
		for(int i = 0; i < 6; i++) {
			if(sides3[i] != field3.getStickers()[i]) {
				fail();
			}
		}
	}

	@Test
	public void testRotateMove() throws FieldException, PointException {
		Field field3 = new Field(FieldType.CORNER);
		field3.setSticker(0, Sticker.TWO);
		field3.setSticker(5, Sticker.ONE);
		field3.setSticker(1, Sticker.THREE);
		Point3D center = new Point3D();
		
		Sticker sides3[]
			= new Sticker[] {Sticker.TWO, Sticker.THREE,
				null, null, null, Sticker.ONE};
		
		field3.rotate(new Move(BasicMove.RIGHT, 2), center);
		field3.rotate(new Move(BasicMove.FRONT), center);
		field3.rotate(new Move(BasicMove.FRONT), center);
		field3.rotate(new Move(BasicMove.RIGHT_INVERTED, 0), center);
		field3.rotate(new Move(BasicMove.LEFT, 1), center);
		field3.rotate(new Move(BasicMove.UP_INVERTED, 4), center);
		field3.rotate(new Move(BasicMove.BACK, 12), center);
		field3.rotate(new Move(BasicMove.DOWN, 5), center);
		
		for(int i = 0; i < 6; i++) {
			if(sides3[i] != field3.getStickers()[i]) {
				fail();
			}
		}
	}

	@Test
	public void testSetSide() throws FieldException {
		Field corner = new Field(FieldType.CORNER);
		// první
		corner.setSticker(0, Sticker.ONE);
		// druhá
		corner.setSticker(1, Sticker.TWO);
		
		try {
			corner.setSticker(2, Sticker.TWO);
			fail();
		} catch (FieldException e) {
			
		}
		
		corner.setSticker(1, Sticker.FIVE);
		
		try {
			corner.setSticker(1, Sticker.ONE);
			fail();
		} catch (FieldException e) {
			
		}
		
		// třetí
		corner.setSticker(3, Sticker.THREE);
		corner.setSticker(3, Sticker.FOUR);
		corner.setSticker(3, Sticker.THREE);
		
		// -1
		corner.setSticker(3, null);
		corner.setSticker(5, null);
		corner.setSticker(4, null);
		
		try {
			corner.setSticker(-1, Sticker.THREE);
			fail();
		} catch (Exception e) {
			
		}
		
		try {
			corner.setSticker(6, Sticker.THREE);
			fail();
		} catch (Exception e) {
			
		}
		
		// třetí
		corner.setSticker(4, Sticker.FOUR);
		
		try {
			corner.setSticker(5, Sticker.THREE);
			fail();
		} catch (FieldException e) {
			
		}
	}

	@Test
	public void testEquals() throws FieldException {
		Field field = new Field(FieldType.CORNER);
		field.setSticker(0, Sticker.TWO);
		field.setSticker(5, Sticker.ONE);
		field.setSticker(1, Sticker.THREE);
		
		Field field2 = new Field(FieldType.CORNER);
		field2.setSticker(0, Sticker.TWO);
		field2.setSticker(5, Sticker.ONE);
		field2.setSticker(1, Sticker.THREE);
		
		if(!field.equals(field2)) {
			fail();
		}
		
		field.setSticker(1, Sticker.FOUR);
		
		if(field.equals(field2)) {
			fail();
		}
		
		field = new Field(FieldType.EDGE);
		field.setSticker(0, Sticker.TWO);
		field.setSticker(5, Sticker.ONE);
		
		if(field.equals(field2)) {
			fail();
		}
		
		if(field.equals(null)) {
			fail();
		}

		if(field.equals(15)) {
			fail();
		}
		
		if(!field.equals(field)) {
			fail();
		}
		
		field = new Field(FieldType.CORNER);
		field.setSticker(0, Sticker.TWO);
		field.setSticker(1, Sticker.THREE);
		
		if(field.equals(field2)) {
			fail();
		}
		
		field2.setSticker(5, null);
		
		if(!field.equals(field2)) {
			fail();
		}
	}

}
