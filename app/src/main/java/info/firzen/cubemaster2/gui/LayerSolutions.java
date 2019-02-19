package info.firzen.cubemaster2.gui;

import java.util.LinkedList;
import java.util.Queue;

public class LayerSolutions {
	private Queue<LayerSolution> solutions = new LinkedList<LayerSolution>();

	public LayerSolution getNextSolution() {
		LayerSolution solution = solutions.poll();
		return solution;
	}
	
	public void addSolution(LayerSolution solution) {
		solutions.add(solution);
	}
	
	public void reset() {
		solutions.clear();
	}
}
