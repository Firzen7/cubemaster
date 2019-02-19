package info.firzen.cubemaster2.backend.cube.interfaces;

public interface IRgb {
	public int getRed();
	public int getGreen();
	public int getBlue();
	
	public int getMax();
	public int getMin();
	
	public double distance(IRgb other);
	public boolean equals(Object other);
}
