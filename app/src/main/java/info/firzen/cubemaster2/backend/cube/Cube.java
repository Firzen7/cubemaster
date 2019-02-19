package info.firzen.cubemaster2.backend.cube;

import info.firzen.cubemaster2.backend.cube.abstracts.AbstractCube;
import info.firzen.cubemaster2.backend.cube.enums.SideType;
import info.firzen.cubemaster2.backend.cube.geom.Point3D;
import info.firzen.cubemaster2.backend.cube.interfaces.IField;
import info.firzen.cubemaster2.backend.cube.interfaces.ISticker;
import info.firzen.cubemaster2.backend.exceptions.PointException;
import info.firzen.cubemaster2.backend.other.Useful;

import java.util.List;

public class Cube extends AbstractCube {
	public Cube() {
		
	}
	
	public Cube(int size) throws Exception {
		setSize(size);
		initCube(size);
	}
	
	private String getColorCode(ISticker color) {
		if(color == null) {
			return "!";
		}
		else {
			return color.getName().substring(0, 1);
		}
	}
	
	// -------------------------------------------------------------------------
	// More complex methods
	// -------------------------------------------------------------------------
	
	protected void initCube(int size) throws Exception {
		for(int z = 0; z < size; z++) {
			for(int y = 0; y < size; y++) {
				for(int x = 0; x < size; x++) {
					if(Useful.isCorner(x, y, z, size)) {
						Field corner = Useful.getCornerFromCoords(x, y, z, size);
						fields.add(corner);
					}
					else if(Useful.isEdge(x, y, z, size)) {
						Field edge = Useful.getEdgeFromCoords(x, y, z, size);
						fields.add(edge);
					}
					else if(Useful.isFlat(x, y, z, size)) {
						Field flat = Useful.getFlatFromCoords(x, y, z, size);
						fields.add(flat);
					}
				}
			} 
		}
	}
	
	public void doMove(Move move) throws PointException {
		doMove(move, true);
	}
	
	public void doMove(Move move, boolean undo) throws PointException {
		List<IField> fields = this.fields;
	
		if(!move.isWholeCube()) {
			fields = getFieldsAtLevel(move);
		}
		
		Point3D sum = new Point3D();
		for(IField field : fields) {
			sum = Point3D.plus(sum, field.getLocation());
		}

		sum = Point3D.divide(sum, fields.size());

		for(IField field : fields) {
			field.rotate(move, sum);
		}

		if(undo) {
			addUndoMove(move);
			clearRedo();
		}
	}

	// -------------------------------------------------------------------------
	// Overriding and printing methods
	// -------------------------------------------------------------------------
	
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		
		buffer.append("UP: \n");
		
		for(int y = 0; y < size; y++) {
			for(int x = 0; x < size; x++) {
				buffer.append(getColorCode(getFieldColor(x, y, 0, SideType.UP)));
				buffer.append(" ");
			}
			buffer.append("\n");
		}
		
		buffer.append("\nDOWN: \n");
		for(int y = 0; y < size; y++) {
			for(int x = 0; x < size; x++) {
				buffer.append(getColorCode(getFieldColor(size - x - 1, size - y - 1,
						size - 1, SideType.DOWN)));
				buffer.append(" ");
			}
			buffer.append("\n");
		}
		
		buffer.append("\nFRONT: \n");
		for(int z = 0; z < size; z++) {
			for(int x = 0; x < size; x++) {
				buffer.append(getColorCode(getFieldColor(x, size - 1, z, SideType.FRONT)));
				buffer.append(" ");
			}
			buffer.append("\n");
		}
		
		buffer.append("\nBACK: \n");
		for(int z = 0; z < size; z++) {
			for(int x = 0; x < size; x++) {
				buffer.append(getColorCode(getFieldColor(size - x - 1, 0, z,
						SideType.BACK)));
				buffer.append(" ");
			}
			buffer.append("\n");
		}
		
		buffer.append("\nLEFT: \n");

		for(int z = 0; z < size; z++) {
			for(int y = 0; y < size; y++) {
				buffer.append(getColorCode(getFieldColor(0, y, z, SideType.LEFT)));
				buffer.append(" ");
			}
			buffer.append("\n");
		}
		
		buffer.append("\nRIGHT: \n");
		for(int z = 0; z < size; z++) {
			for(int y = 0; y < size; y++) {
				buffer.append(getColorCode(getFieldColor(size - 1, size - y - 1,
						z, SideType.RIGHT)));
				buffer.append(" ");
			}
			buffer.append("\n");
		}
		
		return buffer.toString();
	}
	
	public boolean equals(Object other) {
		if(other == null) {
			return false;
		}
		if(other instanceof Cube) {
			if(this.getSize() != ((Cube)other).getSize()
					|| this.getFieldsCount() != ((Cube)other).getFieldsCount()) {
				return false;
			}
			
			List<IField> otherFields = ((Cube)other).getFields();
			int size = getFieldsCount();
			for(int i = 0; i < size; i++) {
				if(!fields.get(i).equals(otherFields.get(i))) {
					return false;
				}
			}
			return true;
		}
		else {
			return false;
		}
	}
	
	public String sideToString(SideType side) {
		StringBuilder buffer = new StringBuilder();
		
		List<IField> fields = null;
		try {
			fields = getFieldsAtLevel(new Move(side));
		} catch (Exception e) {
			// ok
		}
		
		int size = getSize();
		for(int i = 0; i < size; i++) {
			for(int j = 0; j < size; j++) {
				buffer.append(getColorCode(fields.get(size * i + j).getSticker(side)));
				buffer.append(" ");
			}
			buffer.append("\n");
		}
		
		return buffer.toString();
	}
}
