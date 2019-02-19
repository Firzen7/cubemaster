package info.firzen.cubemaster2.backend.cube.solver;

import info.firzen.cubemaster2.backend.cube.Move;

import java.util.List;

public interface Solver {
	public List<Move> getSolution() throws Exception;
}
