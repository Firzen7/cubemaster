package info.firzen.cubemaster2.gui;

import info.firzen.cubemaster2.backend.cube.Move;
import info.firzen.cubemaster2.backend.cube.solver.Shortener;
import info.firzen.cubemaster2.backend.exceptions.UnknownException;
import info.firzen.cubemaster2.other.Action;

import java.util.ArrayList;
import java.util.List;

public class LayerSolution {
	private List<Move> layerMoves = new ArrayList<Move>();
	private Action actionBefore;
	private Action actionAfter;
	
	public LayerSolution(List<Move> moves, Action actionBefore) throws UnknownException {
		addLayerMoves(moves);
		setActionBefore(actionBefore);
	}
	
	public LayerSolution(List<Move> moves) throws UnknownException {
		addLayerMoves(moves);
	}
	
	public List<Move> getLayerMoves() {
		return layerMoves;
	}
	
	public void addLayerMoves(List<Move> layerMoves) throws UnknownException {
		Shortener s = new Shortener(layerMoves);
		List<Move> shortened = s.getShortenedMoves();
		for(Move move : shortened) {
			this.layerMoves.add(move.getCopy());
		}
	}
	
	public Action getActionBefore() {
		return actionBefore;
	}
	
	public void setActionBefore(Action actionBefore) {
		this.actionBefore = actionBefore;
	}
	
	public Action getActionAfter() {
		return actionAfter;
	}
	
	public void setActionAfter(Action actionAfter) {
		this.actionAfter = actionAfter;
	}
	
	public void startActionBefore() {
		if(actionBefore != null) {
			actionBefore.run();
		}
	}
	
	public void startActionAfter() {
		if(actionAfter != null) {
			actionAfter.run();
		}
	}
	
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("Moves: ");
		buffer.append(layerMoves);
		buffer.append(", beforeAction: ");
		if(actionBefore != null) {
			buffer.append("yes");
		}
		else {
			buffer.append("no");			
		}
		buffer.append(", afterAction: ");
		if(actionAfter != null) {
			buffer.append("yes");
		}
		else {
			buffer.append("no");			
		}
		return buffer.toString();
	}
}
