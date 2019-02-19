package info.firzen.cubemaster2.other;

import info.firzen.cubemaster2.cube.CubeColor;

import java.util.ArrayList;
import java.util.List;

public class DataHolder {
	private CubeColor pickerColor = CubeColor.UNKNOWN;
	private List<CubeColor> colors = new ArrayList<CubeColor>();
	private String lastException = "";
	private boolean helpShown = false;

	private static final DataHolder holder = new DataHolder();
	
	public static DataHolder getInstance() {
		return holder;
	}
	
	public List<CubeColor> getColors() {
		return colors;
	}
	
	public void setColors(List<CubeColor> colors) {
		this.colors = colors;
	}

	public CubeColor getPickerColor() {
		return pickerColor;
	}

	public void setPickerColor(CubeColor pickerColor) {
		this.pickerColor = pickerColor;
	}
	
	public void resetLastException() {
		setLastException("");
	}

	public String getLastException() {
		return lastException;
	}

	public void setLastException(String lastException) {
		this.lastException = lastException;
	}
	
	public void setLastException(Exception e) {
		this.lastException = Useful.getStackTrace(e);
	}

	public boolean isHelpShown() {
		return helpShown;
	}

	public void setHelpShown(boolean helpShown) {
		this.helpShown = helpShown;
	}
}