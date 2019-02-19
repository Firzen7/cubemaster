package info.firzen.cubemaster2.backend.cube.enums;

import info.firzen.cubemaster2.backend.cube.color.Rgb;
import info.firzen.cubemaster2.backend.cube.interfaces.IField;
import info.firzen.cubemaster2.backend.cube.interfaces.IRgb;
import info.firzen.cubemaster2.backend.cube.interfaces.ISticker;
import info.firzen.cubemaster2.backend.exceptions.ParseException;
import info.firzen.cubemaster2.backend.exceptions.UnknownException;

public enum Sticker implements ISticker {
	ONE("white", new Rgb(255, 255, 255), 1),
	TWO("red", new Rgb(255, 0, 0), 2),
	THREE("green", new Rgb(0, 255, 0), 3),
	FOUR("blue", new Rgb(0, 0, 255), 4),
	FIVE("yellow", new Rgb(255, 255, 0), 5),
	SIX("orange", new Rgb(255, 128, 0), 6),
	EMPTY("empty", new Rgb(-1, -1, -1), 0),
	UNKNOWN("unknown", new Rgb(-1, -1, -1), -1);
	
	private IRgb color;
	private String name;
	private int id;
	private IField parent;
	
	Sticker(String name, IRgb color, int id) {
		this.name = name;
		this.color = color;
		this.setId(id);
	}
	
	public static Sticker parseString(String str) {
		Sticker[] values = Sticker.values();
		for(Sticker col : values) {
			if(col.toString().equals(str)) {
				return col;
			}
		}
		return null;
	}

	public IRgb getColor() {
		return color;
	}

	public void setColor(IRgb color) {
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static Sticker parseName(String name) {
		Sticker[] values = Sticker.values();
		for(Sticker val : values) {
			if(val.getName().equals(name)) {
				return val;
			}
		}

		return null;
	}
	
	public static Sticker parseColor(IRgb color) throws ParseException {
		Sticker[] values = Sticker.values();
		for(Sticker val : values) {
			if(val.getColor().equals(color)) {
				return val;
			}
		}
		
		throw new ParseException("Cannot parse color " + color + "!");
	}
	
	public String toString() {
		return name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public IField getParent() {
		return parent;
	}

	@Override
	public void setParent(IField parent) {
		this.parent = parent;
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
}
