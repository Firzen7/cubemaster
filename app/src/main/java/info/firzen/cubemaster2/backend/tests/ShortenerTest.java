package info.firzen.cubemaster2.backend.tests;

import info.firzen.cubemaster2.backend.cube.Move;
import info.firzen.cubemaster2.backend.cube.solver.Shortener;
import info.firzen.cubemaster2.backend.exceptions.FilterCharsError;
import info.firzen.cubemaster2.backend.exceptions.ParseException;
import info.firzen.cubemaster2.backend.exceptions.UnknownException;

import java.util.List;

import org.junit.Test;
import static org.junit.Assert.fail;

public class ShortenerTest {
	@Test
	public void complexTest() throws UnknownException, ParseException,
			FilterCharsError {
		shortenComplex("U U1 U1 U1 U1 U2 U2 U2 U2", "U");
		shortenComplex("U R D Di L R R Ri Ri", "U R L");
		shortenComplex("U R D D D Di U U U1 U1 U1 U1 U2 U2 U2 U2", "U R Di Di U U");		
	}
	
	@Test
	public void testNegation() throws UnknownException, ParseException,
			FilterCharsError {
		shortenNegations("U R D Di L R R Ri Ri", "U R L");
		shortenNegations("R", "R");
		shortenNegations("", "");
		shortenNegations("L Li", "");
		shortenNegations("L Li R", "R");
		shortenNegations("Fi F R U Ui Ri Fi", "Fi");
		shortenNegations("F Fi F Fi F Fi F", "F");
	}
	
	@Test
	public void testRepetitions() throws ParseException, FilterCharsError,
			UnknownException {
		shortenRepetitions("U", "U");
		shortenRepetitions("U U1", "U U1");
//		shortenRepetitions("U U1 U U1 U U1", "Ui1 Ui");
//		shortenRepetitions("R U U1 U U1 U U1", "R Ui1 Ui");
//		shortenRepetitions("R L L L U U1 U U1 U U1", "R Li Ui1 Ui");
		shortenRepetitions("U U U", "Ui");
		shortenRepetitions("U U U U", "");
		shortenRepetitions("U U U U U", "U");
		shortenRepetitions("R R R R R R R R R R R", "Ri");
		shortenRepetitions("U", "U");
		shortenRepetitions("U U U", "Ui");
		shortenRepetitions("U U1 U1 U1 U1 U2 U2 U2 U2", "U");		
	}
	
	private void shortenComplex(String m1, String pattern)
			throws UnknownException, ParseException, FilterCharsError {
		List<Move> moves = Move.parseMoves(m1);
		List<Move> check = Move.parseMoves(pattern);
		
		Shortener s = new Shortener(moves);
		
		List<Move> output = s.getShortenedMoves();
		
		if(!output.equals(check)) {
			System.out.println(moves + " --> " + output);
			fail();
		}
	}	
	
	private void shortenNegations(String m1, String pattern)
			throws UnknownException, ParseException, FilterCharsError {
		List<Move> moves = Move.parseMoves(m1);
		List<Move> check = Move.parseMoves(pattern);
		
		Shortener s = new Shortener(moves);
		
		List<Move> output = s.withoutNegations(moves);
		
		if(!output.equals(check)) {
			System.out.println(moves + " --> " + output);
			fail();
		}
	}
	
	private void shortenRepetitions(String m1, String pattern)
			throws UnknownException, ParseException, FilterCharsError {
		List<Move> moves = Move.parseMoves(m1);
		List<Move> check = Move.parseMoves(pattern);
		
		Shortener s = new Shortener(moves);
		List<Move> output = s.withoutRepetitions(moves);
		
		if(!output.equals(check)) {
			System.out.println(moves + " --> " + output);
			fail();
		}
	}
}
