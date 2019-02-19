package info.firzen.cubemaster2.gui;

import info.firzen.cubemaster2.backend.cube.enums.SideType;
import info.firzen.cubemaster2.backend.cube.enums.Sticker;
import info.firzen.cubemaster2.backend.cube.interfaces.IField;
import info.firzen.cubemaster2.backend.cube.interfaces.IRgb;
import info.firzen.cubemaster2.backend.cube.interfaces.ISticker;
import info.firzen.cubemaster2.backend.exceptions.ParseException;
import info.firzen.cubemaster2.backend.exceptions.UnknownException;
import info.firzen.cubemaster2.cube.CubeColor;

import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.World;

public class Sticker3D extends Object3D implements ISticker {
	private static final long serialVersionUID = 1L;
	
	private int id;
	private IRgb color;
	private IField parent;
	
	private FrameBuffer buffer;
	private World world;
	
	public Sticker3D(int arg0, World world, FrameBuffer buffer) {
		super(arg0);
		setWorld(world);
		setBuffer(buffer);
	}
	
	private Matrix getRotationMatrixX(float angle) {
		Matrix m = new Matrix();
		m.setRow(0, 1, 0, 0, 0);
		m.setRow(1, 0, (float) Math.cos(angle), (float) -Math.sin(angle), 0);
		m.setRow(2, 0, (float) Math.sin(angle), (float) Math.cos(angle), 0);
		m.setRow(3, 0, 0, 0, 1);
		return m;
	}
	
	private Matrix getRotationMatrixY(float angle) {
		Matrix m = new Matrix();
		m.setRow(0, (float) Math.cos(angle), 0, (float) Math.sin(angle), 0);
		m.setRow(1, 0, 1, 0, 0);
		m.setRow(2, (float) -Math.sin(angle), 0, (float) Math.cos(angle), 0);
		m.setRow(3, 0, 0, 0, 1);
		return m;
	}

	private Matrix getRotationMatrixZ(float angle) {
		Matrix m = new Matrix();
		m.setRow(0, (float) Math.cos(angle), (float) -Math.sin(angle), 0, 0);
		m.setRow(1, (float) Math.sin(angle), (float) Math.cos(angle), 0, 0);
		m.setRow(2, 0, 0, 1, 0);
		m.setRow(3, 0, 0, 0, 1);
		return m;
	}
	
	public void rotateWholeX(float angle) {
		Matrix m = getRotationMatrix();
		m.matMul(getRotationMatrixX(angle));
		setRotationMatrix(m);
	}
	
	public void rotateWholeY(float angle) {
		Matrix m = getRotationMatrix();
		m.matMul(getRotationMatrixY(angle));
		setRotationMatrix(m);
	}

	public void rotateWholeZ(float angle) {
		Matrix m = getRotationMatrix();
		m.matMul(getRotationMatrixZ(angle));
		setRotationMatrix(m);
	}
	
	@Override
	public void rotateX(float angle) {
		rotateWholeX(angle);
	}
	
	@Override
	public void rotateY(float angle) {
		rotateWholeY(angle);		
	}

	@Override
	public void rotateZ(float angle) {
		rotateWholeZ(angle);
	}
	
	public FrameBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(FrameBuffer buffer) {
		this.buffer = buffer;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public IField getParent() {
		return parent;
	}

	public void setParent(IField parent) {
		this.parent = parent;
	}

	public IRgb getColor() {
		return color;
	}

	public void setColor(IRgb color) {
		this.color = color;
	}
	
	public void setColor(CubeColor tcolor) {
		setColor(tcolor.getColor());
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public SideType getSide() throws UnknownException {
		SideType[] values = SideType.values();
		for(SideType val : values) {
			ISticker sticker = parent.getSticker(val);
			if(this.equals(sticker)) {
				return val;
			}
		}

		throw new UnknownException("Cannot determine side!");
	}
	
	public Sticker getSticker() throws ParseException {
		Sticker output = Sticker.parseColor(this.getColor());
		return output;
	}

	public boolean equals(Object other) {
		if(other != null && other instanceof Sticker3D) {
			Sticker3D s = (Sticker3D) other;
			return s.getId() == getId();
		}
		else {
			return false;
		}
	}
	
	public String toString() {
		if(parent != null) {
			return getId() + " " + getColor() + " " +
				getParent().getLocation();
		}
		else {
			return getId() + " " + getColor() + " (no parent)";
		}
	}
}
