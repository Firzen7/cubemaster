package info.firzen.cubemaster2.backend.cube.color;

import info.firzen.cubemaster2.backend.cube.abstracts.AbstractRgb;

public class Rgb extends AbstractRgb {
	public Rgb() {
		
	}
	
	/*
	public Rgb(int color) {
		Rgb r = Rgb.parseInt(color);
		red = r.getRed();
		green = r.getGreen();
		blue = r.getBlue();
	}
	*/
	
	public Rgb(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	/*
	public static Rgb parseInt(int color) {
		Rgb output = new Rgb();
		output.setRed(Color.red(color));
		output.setGreen(Color.green(color));
		output.setBlue(Color.blue(color));
		return output;
	}
	*/
	
}
