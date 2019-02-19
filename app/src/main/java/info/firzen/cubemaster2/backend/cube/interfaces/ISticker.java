package info.firzen.cubemaster2.backend.cube.interfaces;

import info.firzen.cubemaster2.backend.cube.enums.SideType;
import info.firzen.cubemaster2.backend.exceptions.UnknownException;


public interface ISticker {
	public IRgb getColor();
	public void setColor(IRgb color);
	public String getName();
	public void setName(String name);
	public IField getParent();
	public void setParent(IField parent);

	public SideType getSide() throws UnknownException;
}
