package info.firzen.cubemaster2.backend.cube.solver;

import java.util.List;

import info.firzen.cubemaster2.backend.cube.Cube;
import info.firzen.cubemaster2.backend.cube.Move;
import info.firzen.cubemaster2.backend.exceptions.PointException;
import info.firzen.cubemaster2.backend.exceptions.UnknownException;

public abstract class SolverPattern implements Solver {
	protected Cube cube = null;
	
	private List<Move> undoBackup;
	private List<Move> redoBackup;
	
	public SolverPattern(Cube cube) {
		setCube(cube);
	}
	
	public Cube getCube() {
		return cube;
	}

	public void setCube(Cube cube) {
		this.cube = cube;
	}
	
	protected void backup() {
		undoBackup = cube.getUndoMoves();
		redoBackup = cube.getRedoMoves();
		cube.clearUndo();
		cube.clearRedo();
	}
	
	protected void restore() throws PointException, UnknownException {
		while(cube.undoPossible()) {
			cube.undo();
		}
		cube.setUndoMoves(undoBackup);
		cube.setRedoMoves(redoBackup);
	}
}
