package info.firzen.cubemaster2.backend.cube;

import info.firzen.cubemaster2.backend.cube.abstracts.AbstractField;
import info.firzen.cubemaster2.backend.cube.enums.FieldType;
import info.firzen.cubemaster2.backend.cube.enums.Sticker;
import info.firzen.cubemaster2.backend.cube.geom.Point3D;

public class Field extends AbstractField {
	public Field(FieldType type) {
		stickers = new Sticker[6];
		this.type = type;
	}
	
	public Field(FieldType type, Point3D location) {
		stickers = new Sticker[6];
		this.type = type;
		setLocation(location);
	}
	
	public Field(FieldType type, int x, int y, int z) {
		stickers = new Sticker[6];
		this.type = type;
		setLocation(new Point3D(x, y, z));
	}
}
