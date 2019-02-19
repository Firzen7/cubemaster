package info.firzen.cubemaster2.backend.cube;

import info.firzen.cubemaster2.backend.cube.enums.BasicMove;
import info.firzen.cubemaster2.backend.cube.enums.SideType;
import info.firzen.cubemaster2.backend.exceptions.FilterCharsError;
import info.firzen.cubemaster2.backend.exceptions.ParseException;
import info.firzen.cubemaster2.backend.exceptions.UnknownException;
import info.firzen.cubemaster2.backend.other.Useful;

import java.util.ArrayList;
import java.util.List;

public class Move {
	private BasicMove basicMove;
	private int level = 0;
	private boolean wholeCube = false;

	public Move(SideType sideType) throws UnknownException {
		setBasicMove(sideType);
	}

	public Move(SideType sideType, int level) throws UnknownException {
		setBasicMove(sideType);
		setLevel(level);
	}

	public Move(SideType sideType, int level, boolean wholeCube)
			throws UnknownException {
		setBasicMove(sideType);
		setLevel(level);
		setWholeCube(wholeCube);
	}
	
	public Move(BasicMove basicMove) {
		setBasicMove(basicMove);
	}
	
	public Move(BasicMove basicMove, boolean wholeCube) {
		setBasicMove(basicMove);
		setWholeCube(wholeCube);
	}
	
	public Move(BasicMove basicMove, int level) {
		setBasicMove(basicMove);
		setLevel(level);
	}
	
	public Move(BasicMove basicMove, int level, boolean wholeCube) {
		setBasicMove(basicMove);
		setLevel(level);
		setWholeCube(wholeCube);
	}
	
	public Move(String str) throws ParseException, FilterCharsError {
		Move m = parseMove(str);
		this.setBasicMove(m.getBasicMove());
		this.setLevel(m.getLevel());
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public BasicMove getBasicMove() {
		return basicMove;
	}
	
	public void setBasicMove(SideType sideType) throws UnknownException {
		this.basicMove = BasicMove.parseSideType(sideType);
	}

	public void setBasicMove(BasicMove basicMove) {
		this.basicMove = basicMove;
	}
	
	public static Move getRandomMove(int cubeSize) {
		BasicMove bmove = BasicMove.getRandomMove();
		int level = (int) (Math.random() * (double)(cubeSize - 1));
		return new Move(bmove, level);
	}
		
	public static Move parseMove(String str) throws FilterCharsError,
			ParseException {
		boolean whole = false;
		
		String onlyLetters = Useful.filterChars(str, null, "0123456789", null);
		String onlyNumbers = Useful.filterChars(str, "0123456789", null, null);
		
		if(onlyLetters.contains("W")) {
			whole = true;
		}
		
		BasicMove parsedMove = BasicMove.parseString(onlyLetters.replace("W", ""));
		Integer parsedNumber = 0;
		if(!onlyNumbers.isEmpty()) {
			parsedNumber = Integer.parseInt(onlyNumbers);
		}
		return new Move(parsedMove, parsedNumber, whole);
	}
	
	public static List<Move> parseMoves(String str) throws ParseException,
			FilterCharsError {
		if(str != null && !str.isEmpty()) {
			List<Move> output = new ArrayList<Move>();
			String[] parts = str.split(" ");
			for(String part : parts) {
				output.add(parseMove(part));
			}
			return output;
		}
		else {
			return new ArrayList<Move>();
		}
	}
	
	public static List<Move> getRandomMoves(int cubeSize, int count) {
		List<Move> output = new ArrayList<Move>();
		for(int i = 0; i < count; i++) {
			output.add(Move.getRandomMove(cubeSize));
		}
		return output;
	}
	
	public static String getMovesAsString(List<Move> moves) {
		StringBuilder buffer = new StringBuilder();
		for(Move move : moves) {
			buffer.append(move.toString());
			buffer.append(" ");
		}
		buffer.deleteCharAt(buffer.length() - 1);
		return buffer.toString();
	}
	
	public static String getRandomMovesString(int cubeSize, int count) {
		List<Move> moves = getRandomMoves(cubeSize, count);
		StringBuilder buffer = new StringBuilder();
		for(Move move : moves) {
			buffer.append(move.toString());
			buffer.append(" ");
		}
		buffer.deleteCharAt(buffer.length() - 1);
		return buffer.toString();
	}
	
	public static Move getInvertedMove(Move move) throws UnknownException {
		return new Move(BasicMove.getInvertedMove(move.getBasicMove()),
				move.getLevel(), move.isWholeCube());
	}
	
	public static List<Move> getInvertedMoves(List<Move> moves)
			throws UnknownException {
		List<Move> output = new ArrayList<Move>();
		int size = moves.size();
		for(int i = size - 1; i >= 0; i--) {
			output.add(Move.getInvertedMove(moves.get(i)));
		}
		return output;
	}
	
	public static List<Move> getInvertedMoves(String str)
			throws UnknownException, ParseException, FilterCharsError {
		List<Move> moves = Move.parseMoves(str);
		return Move.getInvertedMoves(moves);
	}
	
	public boolean isInverted() {
		return basicMove.isInverted();
	}
	
	public Move rotate(BasicMove move) throws UnknownException {
		return new Move(getBasicMove().rotate(move), getLevel());
	}
	
	public Move getCopy() {
		Move output = new Move(basicMove, level, isWholeCube());
		return output;
	}
	
	public boolean equals(Object other) {
		if(other != null && other instanceof Move) {
			Move m = (Move) other;
			return basicMove.equals(m.getBasicMove()) && getLevel() == m.getLevel()
					&& isWholeCube() == m.isWholeCube();
		}
		else {
			return false;
		}
	}
	
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(this.getBasicMove().toString());
		if(level != 0) {
			buffer.append(level);
		}
		
		if(isWholeCube()) {
			buffer.append("W");
		}
		
		return buffer.toString();
	}
	
	public String toString(int cubeSize) {
		StringBuilder buffer = new StringBuilder();
		
		if(level > (cubeSize / 2)) {
			try {
				Move m = new Move(getBasicMove().getOppositeMove(), cubeSize - level - 1);
				return m.toString();
			} catch (UnknownException e) {
				return this.toString();
			}
		}
		else {
			buffer.append(this.getBasicMove().toString());
			if(level != 0) {
				buffer.append(level);
			}

			if(isWholeCube()) {
				buffer.append("W");
			}
			
			return buffer.toString();
		}
	}

	public boolean isWholeCube() {
		return wholeCube;
	}

	public void setWholeCube(boolean wholeCube) {
		this.wholeCube = wholeCube;
	}
}
