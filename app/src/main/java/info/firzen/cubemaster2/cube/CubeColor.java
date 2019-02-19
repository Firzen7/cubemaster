package info.firzen.cubemaster2.cube;

import com.threed.jpct.RGBColor;

import info.firzen.cubemaster2.backend.cube.interfaces.IRgb;
import info.firzen.cubemaster2.backend.exceptions.ParseException;
import info.firzen.cubemaster2.other.Constants;
import info.firzen.cubemaster2.recognition.Rgb;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

public enum CubeColor {
	WHITE("p_white", new Rgb(255, 255, 255)),
	RED("p_red", new Rgb(255, 0, 0)),
	BLUE("p_blue", new Rgb(0, 0, 255)),
	GREEN("p_green", new Rgb(0, 255, 0)),
	YELLOW("p_yellow", new Rgb(255, 255, 0)),
	ORANGE("p_orange", new Rgb(255, 128, 0)),
	UNKNOWN("p_unknown", new Rgb(80, 80, 80)),
	BLACK("p_black", new Rgb(30, 30, 30));
	
	private Rgb color;
	private String settingKey;
	
	CubeColor(String settingKey, Rgb color) {
		this.color = color;
		this.settingKey = settingKey;
	}
	
	public static CubeColor parseString(String str) {
		CubeColor[] values = CubeColor.values();
		for(CubeColor col : values) {
			if(col.toString().equals(str)) {
				return col;
			}
		}
		return null;
	}
	
	public int getAndroidColor() {
		return Color.rgb(color.getRed(), color.getGreen(), color.getBlue());
	}
	
	public static CubeColor parseRgb(IRgb rgb) throws ParseException {
		CubeColor[] values = values();
		for(CubeColor val : values) {
			if(val.getColor().equals(rgb)) {
				return val;
			}
		}
		
		throw new ParseException("Cannot parse color " + rgb);
	}
	
	public String toString() {
		return this.name();
	}

	public IRgb getColor() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(
				Constants.baseContext);
		
		return Rgb.parseInt(prefs.getInt(settingKey, getAndroidColor()));
	}
	
	public RGBColor getRGBColor() {
		IRgb c = getColor();
		return new RGBColor(c.getRed(), c.getGreen(), c.getBlue());
	}

	public void setColor(Rgb patternColor) {
		this.color = patternColor;
	}
}
