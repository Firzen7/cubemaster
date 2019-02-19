package info.firzen.cubemaster2.recognition;

public class Hsv {
	private int hue = 0;
	private int saturation = 0;
	private int value = 0;

	
	public int getHue() {
		return hue;
	}
	
	public void setHue(int hue) {
		this.hue = hue;
	}
	
	public int getSaturation() {
		return saturation;
	}
	
	public void setSaturation(int saturation) {
		this.saturation = saturation;
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public static Hsv parseInt(int color) {
		Rgb r = Rgb.parseInt(color);
		return Hsv.parseRgb(r);
	}
	
	public static Hsv parseRgb(Rgb color) {
		Hsv output = new Hsv();
		
	    double maximum = color.getMax();
	    double minimum = color.getMin();
	    
	    int r = color.getRed();
	    int g = color.getGreen();
	    int b = color.getBlue();
	    
	    double h = 0;
	    double s = 0;
	    double v = 0;
	    
	    if(maximum == minimum) {
	        h = 0;
	    }
	    
	    if(maximum == r) {
	        if(g >= b)
	            h = 60 * ((g - b) / (maximum - minimum));
	        else
	        	h = 60 * ((g - b) / (maximum - minimum)) + 360;
	    }
	    
	    if(maximum == g) {
	    	h = 60 * ((b - r) / (maximum - minimum)) + 120;
	    }
	    
	    if(maximum == b) {
	    	h = 60 * ((r - g) / (maximum - minimum)) + 240;
	    }
	    
	    if(h > 360) {
	        h = h - 360;
	    }
	    //h = h / 360;
	    
	    if(maximum == 0) {
	        s = 0;
	    }
	    else {
	        s = (maximum - minimum) / maximum;
	    }
	    
	    v = maximum;
	    
	    output.setHue((int)h);
	    output.setSaturation((int)(s * 255));
	    output.setValue((int)v);
	    
	    return output;
	}
	
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("[");
		buffer.append(hue);
		buffer.append(", ");
		buffer.append(saturation);
		buffer.append(", ");
		buffer.append(value);
		buffer.append("]");
		return buffer.toString();
	}
}
