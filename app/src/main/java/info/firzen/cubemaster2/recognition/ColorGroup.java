package info.firzen.cubemaster2.recognition;

import info.firzen.cubemaster2.cube.CubeColor;

import java.util.ArrayList;
import java.util.List;

public class ColorGroup {
	private List<IndexedColor> colors = new ArrayList<IndexedColor>();
	private CubeColor detectedColor = null;
	
	public double getSimilarity(Rgb color) {
		return color.distance(getAverageColor());
	}
	
	// XXX
	public boolean isSimilar(Rgb color) {
		return getSimilarity(color) < 40;
	}
	
	public Rgb getAverageColor() {
		long red = 0, green = 0, blue = 0;
		
		for(IndexedColor icolor : colors) {
			Rgb color = icolor.getColor();
			red += color.getRed();
			green += color.getGreen();
			blue += color.getBlue();
		}
		
		int count = colors.size();
		Rgb out = new Rgb((int) ((double)red / (double)count),
				(int) ((double)green / (double)count),
				(int) ((double)blue / (double)count));
		
		return out;
	}

	public void addColor(int index, Rgb color) {
		if(colors == null) {
			colors = new ArrayList<IndexedColor>();
		}
		colors.add(new IndexedColor(index, color));
	}
	
	public List<IndexedColor> getColors() {
		return colors;
	}

	public void setColors(List<IndexedColor> colors) {
		this.colors = colors;
	}
	
	public CubeColor getDetectedColor() {
		return detectedColor;
	}

	public void setDetectedColor(CubeColor detectedColor) {
		this.detectedColor = detectedColor;
	}
	
	public IndexedColor getMostAccurateColor() {
		if(detectedColor != null && colors != null && !colors.isEmpty()) {
			IndexedColor accurate = colors.get(0);
			double minDistance = detectedColor.getColor().distance(accurate.getColor());
			for(IndexedColor icolor : colors) {
				if(icolor.getColor().distance(detectedColor.getColor())
						< minDistance) {
					accurate = icolor;
					minDistance = icolor.getColor().distance(detectedColor.getColor());
				}
			}
			
			return accurate;
		}
		else {
			return null;
		}
	}

	public String toString() {
		return colors.toString();
	}
}

