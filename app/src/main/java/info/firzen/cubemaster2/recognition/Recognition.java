package info.firzen.cubemaster2.recognition;

import info.firzen.cubemaster2.backend.exceptions.ParseException;
import info.firzen.cubemaster2.cube.CubeColor;
import info.firzen.cubemaster2.other.Constants;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;

public class Recognition {
	private static final int colorTolerance = 300;
	
	public Bitmap cutImage(Bitmap input, int x, int y, int width, int height) {
		if(x + width > input.getWidth()) {
			return input;
		}
		if(x + height > input.getHeight()) {
			return input;
		}
		
		return Bitmap.createBitmap(input, x, y, width, height);
	}
	
	public Rgb averageColor(Bitmap image, int x, int y, int areaWidth,
			int areaHeight) {

		int width = image.getWidth();
		int height = image.getHeight();
		
		double r = 0;
		double g = 0;
		double b = 0;
		
		double count = 0;
		
		if(areaWidth + x > width) areaWidth = width - x;
		if(areaHeight + y > height) areaHeight = height - y;
		if(x < 0) x = 0;
		if(y < 0) y = 0;
		
		for(int i = x; i < x + areaWidth; i++) {
			for(int j = y; j < y + areaHeight; j++) {				
				int px = 0;
				px = image.getPixel(i, j);

				try {
					Rgb pixel = Rgb.parseInt(px);
					
					if(Hsv.parseRgb(pixel).getValue() > 20) {
						r += (double)pixel.getRed();
						g += (double)pixel.getGreen();
						b += (double)pixel.getBlue();
						count++;
					}
				}
				catch (Exception e) {
					// XXX ignore
				}
			}
		}
		
		return new Rgb((int)(r / count), (int)(g / count), (int)(b / count));
	}
	
	public List<Rgb> sortColorsByDistance(Rgb pattern, List<Rgb> colors) {
		int size = colors.size();
		for(int j = 0; j < size - 1; j++) {
			double minDistance = pattern.distance(colors.get(j));
			int index = j;
			Rgb nearestColor = colors.get(j);
			
			for(int i = j; i < size; i++) {
				Rgb actual = colors.get(i);
				if(pattern.distance(actual) < minDistance) {
					minDistance = pattern.distance(actual);
					nearestColor = actual;
					index = i;
				}
			}
			
			Rgb temp = colors.get(j);
			colors.set(j, nearestColor);
			colors.set(index, temp);
		}
		
		return colors;
	}
	
	public List<Rgb> getCubeColors() {
		CubeColor patterns[] = new CubeColor[] {CubeColor.RED, CubeColor.BLUE,
				CubeColor.GREEN, CubeColor.ORANGE, CubeColor.WHITE,
				CubeColor.YELLOW};
		
		List<Rgb> colors = new ArrayList<Rgb>();
		for(CubeColor p : patterns) {
			colors.add((Rgb) p.getColor());
		}
		
		return colors;
	}
	
	public CubeColor identifyColor(Rgb color, int colorChoice) {
		List<Rgb> colors = getCubeColors();
		List<Rgb> sorted = sortColorsByDistance(color, colors);

		Rgb bestMatch = sorted.get(0);

		CubeColor out = null;
		try {
			if(bestMatch.distance(color) < colorTolerance) {
				out = CubeColor.parseRgb(sorted.get(colorChoice));				
			}
			else {
				out = CubeColor.UNKNOWN;
			}
		} catch (ParseException e) {
			out = CubeColor.UNKNOWN;
		}
		
		return out;
	}
	
	public CubeColor identifyColor(Rgb color) {
		return identifyColor(color, 0);
	}
	
	public double getDetectedDistance(Rgb color) {
		return color.distance(identifyColor(color).getColor());
	}
	
	private List<Rgb> getShootColors(Bitmap image) {
		List<Rgb> averages = new ArrayList<Rgb>();
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		int step2 = (int)((double)width / (double)Constants.cubeSize);
		int step1 = (int)((double)height / (double)Constants.cubeSize);
		
		int border = (int)(0.3 * (double)step1);
		
		for(int i = 0; i < Constants.cubeSize; i++) {
			for(int j = 0; j < Constants.cubeSize; j++) {
				Rgb average = averageColor(image, j * step2 + border,
						i * step1 + border,
						step2 - (border * 2) - 5, step1 - (border * 2) - 5);
				averages.add(average);
			}
		}
		
		return averages;
	}
	
	public List<CubeColor> detectCubeSide(Bitmap image) {		
		List<Rgb> colors = getShootColors(image);
		List<ColorGroup> groups = getGroups(colors);
		
		int size = Constants.cubeSize * Constants.cubeSize;
		List<CubeColor> output = new ArrayList<CubeColor>();
		for(int i = 0; i < size; i++) {
			output.add(CubeColor.UNKNOWN);
		}
		
		for(ColorGroup group : groups) {
			List<IndexedColor> icolors = group.getColors();
			CubeColor detected = CubeColor.UNKNOWN;
			for(IndexedColor icolor : icolors) {
				if(CubeColor.UNKNOWN != identifyColor(icolor.getColor())) {
					detected = identifyColor(icolor.getColor());
					break;
				}
			}

			group.setDetectedColor(detected);
		}
		
		for(ColorGroup group : groups) {
			for(IndexedColor icolor : group.getColors()) {
				output.set(icolor.getIndex(), group.getDetectedColor());
			}
		}

		return output;
	}
	
	private List<ColorGroup> getGroups(List<Rgb> colors) {
		List<ColorGroup> groups = new ArrayList<ColorGroup>();
		
		int size = colors.size();
		for(int i = 0; i < size; i++) {
			Rgb color = colors.get(i);
			
			Double bestSimilarity = null;
			ColorGroup mostSimilar = null;
			for(ColorGroup group : groups) {
				if(group.isSimilar(color)) {
					if(bestSimilarity == null || mostSimilar == null
							|| group.getSimilarity(color) < bestSimilarity) {
						mostSimilar = group;
						bestSimilarity = group.getSimilarity(color);
					}
				}
			}

			if(bestSimilarity == null || mostSimilar == null) {
				ColorGroup g = new ColorGroup();
				g.addColor(i, color);
				groups.add(g);
			}
			else {
				mostSimilar.addColor(i, color);
			}
		}
		
		return groups;
	}	
}
