package info.firzen.cubemaster2.recognition;

import info.firzen.cubemaster2.backend.cube.abstracts.AbstractRgb;

import java.util.List;

import android.graphics.Color;

public class Rgb extends AbstractRgb {
	public Rgb() {
		
	}
	
	public Rgb(int color) {
		Rgb r = Rgb.parseInt(color);
		red = r.getRed();
		green = r.getGreen();
		blue = r.getBlue();
	}
	
	public Rgb(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	public static Rgb getAverageColor(List<Rgb> colors) {
		return getAverageColor(colors.toArray(new Rgb[0]));
	}
	
	public static Rgb getAverageColor(Rgb ... colors) {
		Rgb output = new Rgb(0, 0, 0);
		for(Rgb color : colors) {
			output.setRed(output.getRed() + color.getRed());
			output.setGreen(output.getGreen() + color.getGreen());
			output.setBlue(output.getBlue() + color.getBlue());
		}
		int size = colors.length;
		output.setRed(output.getRed() / size);
		output.setGreen(output.getGreen() / size);
		output.setBlue(output.getBlue() / size);
		return output;
	}
	
	public static Rgb parseInt(int color) {
		Rgb output = new Rgb();
		output.setRed(Color.red(color));
		output.setGreen(Color.green(color));
		output.setBlue(Color.blue(color));
		return output;
	}
}
