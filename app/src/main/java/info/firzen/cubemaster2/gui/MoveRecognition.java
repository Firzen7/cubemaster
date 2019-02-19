package info.firzen.cubemaster2.gui;

import info.firzen.cubemaster2.backend.cube.enums.SideType;
import info.firzen.cubemaster2.backend.cube.geom.Point3D;
import info.firzen.cubemaster2.backend.exceptions.UnknownException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MoveRecognition {
	private List<Sticker3D> selectedStickers = new ArrayList<Sticker3D>();
	private List<Sticker3D> preparedStickers = new ArrayList<Sticker3D>();
	
	public void addSticker(Sticker3D sticker) {
		if(sticker != null && !selectedStickers.contains(sticker)) {
			selectedStickers.add(sticker);
		}
	}
	
	public boolean isReady() throws UnknownException {
		return (getFrom() != null && getTo() != null);
	}
	
	private void getStickersForMove() throws UnknownException {
		if(selectedStickers.size() >= 2) {
			Sticker3D from = selectedStickers.get(0);
			Sticker3D to = null;
			Point3D vector = selectedStickers.get(0).getParent().getLocation();
			getStickersFromOneSide();
			
			for(Sticker3D sticker : preparedStickers) {
				vector = Point3D.minus(sticker.getParent().getLocation(), vector);
				from = to;
				to = sticker;
				if(isValidMoveVector(vector) && from != null && to != null) {
					preparedStickers.clear();
					preparedStickers.add(from);
					preparedStickers.add(to);
					break;
				}
			}
		}
	}
	
	private void getStickersFromOneSide() throws UnknownException {
		Map<SideType, Integer> stats = new HashMap<SideType, Integer>();
		for(Sticker3D sticker : selectedStickers) {
			SideType side = sticker.getSide();
			if(stats.get(side) == null) {
				stats.put(side, 1);
			}
			else {
				stats.put(side, stats.get(side) + 1);
			}
		}
		
		int max = 0;
		SideType winnerSide = null;
		for(Entry<SideType, Integer> entry : stats.entrySet()) {
			if(entry.getValue() > max) {
				max = entry.getValue();
				winnerSide = entry.getKey();
			}
		}
		
		preparedStickers.clear();
		for(Sticker3D sticker : selectedStickers) {
			if(sticker.getSide() == winnerSide) {
				preparedStickers.add(sticker);
			}
		}
	}
	
	public Sticker3D getFrom() throws UnknownException {
		getStickersForMove();
		if(preparedStickers.size() == 2) {
			return preparedStickers.get(0);
		}
		else {
			return null;
		}
	}

	public Sticker3D getTo() throws UnknownException {
		getStickersForMove();
		if(preparedStickers.size() == 2) {
			return preparedStickers.get(1);
		}
		else {
			return null;
		}
	}
	
	private boolean isValidMoveVector(Point3D vector) {
		return (vector.getX() == 0 && vector.getY() == 0 && vector.getZ() != 0)
				|| (vector.getX() == 0 && vector.getY() != 0 && vector.getZ() == 0)
				|| (vector.getX() != 0 && vector.getY() == 0 && vector.getZ() == 0);
	}
	
	public void reset() {
		selectedStickers.clear();
		preparedStickers.clear();
	}
}
