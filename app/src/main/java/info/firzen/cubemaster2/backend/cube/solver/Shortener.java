package info.firzen.cubemaster2.backend.cube.solver;

import info.firzen.cubemaster2.backend.cube.Move;
import info.firzen.cubemaster2.backend.exceptions.UnknownException;

import java.util.ArrayList;
import java.util.List;

public class Shortener {
	private List<Move> moves = new ArrayList<Move>();
	
	public Shortener(List<Move> moves) {
		setMoves(moves);
	}
	
	public List<Move> getShortenedMoves() throws UnknownException {
		return withoutNegations(withoutRepetitions(moves));
	}
	
	public List<List<Move>> getGroups(List<Move> moves) {
		List<List<Move>> output = new ArrayList<List<Move>>();
		int size = moves.size();
		for(int i = 0; i < size; i++) {
			Move m = moves.get(i);
			
			List<Move> group = new ArrayList<Move>();
			int j = 0;
			Move next = m;
			while(m.getBasicMove().equals(next.getBasicMove())
					&& next.getLevel() == j && i + j < size) {
				group.add(next);
				j++;
				
				if(i + j < size) {
					next = moves.get(i + j);
				}
			}

			if(!group.isEmpty()) {
				output.add(group);
			}
		}
		
		return output;
	}
	
	public List<Move> withoutNegations(List<Move> moves) {
		List<Move> output = new ArrayList<Move>();
		
		int size = moves.size();
		for(int i = 0; i < size; i++) {
			Move actual = moves.get(i);

			int oppositeSize = 0;
			
			for(int j = i; j < size; j++) {
				int end = j * 2 - i;
				if(end > size) {
					end = size;
				}
				
				if(areMovesOpposite(moves.subList(i, j), moves.subList(j, end))) {
					oppositeSize = (j - i) * 2 - 1;
					break;
				}
			}
			
			if(oppositeSize == 0) {
				output.add(actual);
			}
			else {
				i += oppositeSize;
			}
		}
		
		return output;
	}
	
	private boolean areMovesOpposite(List<Move> moves1, List<Move> moves2) {
		if(moves1 != null && moves2 != null && !moves1.isEmpty()
				&& !moves2.isEmpty()) {
			try {
				return moves1.equals(Move.getInvertedMoves(moves2));
			} catch (UnknownException e) {
				return false;
			}
		}
		else {
			return false;
		}
	}
	
	public List<Move> withoutRepetitions(List<Move> groups)
				throws UnknownException {
		List<Move> output = new ArrayList<Move>();

		int size = groups.size();
		for(int i = 0; i < size; i++) {
			Move actual = groups.get(i);
			Move next = actual;

			int sameMoves = 0;
			int j = 0;
			while(actual.equals(next) && j + i < size) {
				if(sameMoves == 4) {
					sameMoves = 0;
				}

				sameMoves++;
				j++;

				if(j + i < size) {
					next = groups.get(j + i);
				}
			}

			switch(sameMoves) {
			case 1:
				output.add(actual);
				break;
			case 2:
				output.add(actual);
				output.add(actual);
				break;
			case 3:
				output.add(Move.getInvertedMove(actual));
				break;
			default:
				break;
			}

			i += j - 1;
		}

		return output;
	}

	public List<Move> getMoves() {
		return moves;
	}

	public void setMoves(List<Move> moves) {
		this.moves = moves;
	}
}
