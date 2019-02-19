package info.firzen.cubemaster2.backend.other;

import info.firzen.cubemaster2.backend.cube.Field;
import info.firzen.cubemaster2.backend.cube.enums.Sticker;
import info.firzen.cubemaster2.backend.cube.enums.FieldType;
import info.firzen.cubemaster2.backend.exceptions.CreateException;
import info.firzen.cubemaster2.backend.exceptions.FieldException;
import info.firzen.cubemaster2.backend.exceptions.FilterCharsError;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Useful {
	private static int limitsCount(int x, int y, int z, int size) {
		size--;
		int count = 0;
		
		if(x == 0 || x == size) {
			count++;
		}
		if(y == 0 || y == size) {
			count++;
		}
		if(z == 0 || z == size) {
			count++;
		}
		
		return count;
	}
	
	public static boolean isCorner(int x, int y, int z, int size) {
		return limitsCount(x, y, z, size) == 3;
	}
	
	public static boolean isEdge(int x, int y, int z, int size) {
		return limitsCount(x, y, z, size) == 2;
	}
	
	public static boolean isFlat(int x, int y, int z, int size) {
		return limitsCount(x, y, z, size) == 1;
	}
	
	public static Sticker getColorOfCubeByIndex(int index) {
		switch(index) {
			case 0: return Sticker.SIX;
			case 1: return Sticker.THREE;
			case 2: return Sticker.TWO;
			case 3: return Sticker.FOUR;
			case 4: return Sticker.FIVE;
			case 5: return Sticker.ONE;
			default: return Sticker.UNKNOWN;
		}
	}
	
	public static Field getCornerFromCoords(int x, int y, int z, int size)
			throws FieldException, CreateException {
		size--;
		Field corner = new Field(FieldType.CORNER, x, y, z);
		
		if(x == 0 && y == 0 && z == 0) {
			corner.setSticker(5, getColorOfCubeByIndex(5));
			corner.setSticker(2, getColorOfCubeByIndex(2));
			corner.setSticker(3, getColorOfCubeByIndex(3));
		}
		else if(x == size && y == 0 && z == 0) {
			corner.setSticker(5, getColorOfCubeByIndex(5));
			corner.setSticker(2, getColorOfCubeByIndex(2));
			corner.setSticker(1, getColorOfCubeByIndex(1));
		}
		else if(x == 0 && y == size && z == 0) {
			corner.setSticker(5, getColorOfCubeByIndex(5));
			corner.setSticker(3, getColorOfCubeByIndex(3));
			corner.setSticker(0, getColorOfCubeByIndex(0));
		}
		else if(x == size && y == size && z == 0) {
			corner.setSticker(5, getColorOfCubeByIndex(5));
			corner.setSticker(0, getColorOfCubeByIndex(0));
			corner.setSticker(1, getColorOfCubeByIndex(1));
		}
		else if(x == 0 && y == 0 && z == size) {
			corner.setSticker(3, getColorOfCubeByIndex(3));
			corner.setSticker(2, getColorOfCubeByIndex(2));
			corner.setSticker(4, getColorOfCubeByIndex(4));
		}
		else if(x == size && y == 0 && z == size) {
			corner.setSticker(2, getColorOfCubeByIndex(2));
			corner.setSticker(4, getColorOfCubeByIndex(4));
			corner.setSticker(1, getColorOfCubeByIndex(1));
		}
		else if(x == 0 && y == size && z == size) {
			corner.setSticker(4, getColorOfCubeByIndex(4));
			corner.setSticker(3, getColorOfCubeByIndex(3));
			corner.setSticker(0, getColorOfCubeByIndex(0));
		}
		else if(x == size && y == size && z == size) {
			corner.setSticker(0, getColorOfCubeByIndex(0));
			corner.setSticker(4, getColorOfCubeByIndex(4));
			corner.setSticker(1, getColorOfCubeByIndex(1));
		}
		else {
			throw new CreateException("These coords are not corner!");
		}
		
		return corner;
	}
	
	public static Field getEdgeFromCoords(int x, int y, int z, int size)
			throws FieldException, CreateException {
		size--;
		Field edge = new Field(FieldType.EDGE, x, y, z);
		
		if(x > 0 && x < size) {
			if(y == 0) {
				if(z == 0) {
					edge.setSticker(5, getColorOfCubeByIndex(5));
					edge.setSticker(2, getColorOfCubeByIndex(2));
				}
				else if(z == size) {
					edge.setSticker(4, getColorOfCubeByIndex(4));
					edge.setSticker(2, getColorOfCubeByIndex(2));
				}
				else {
					throw new CreateException("These coords are not edge!");
				}
			} else if(y == size) {
				if(z == 0) {
					edge.setSticker(5, getColorOfCubeByIndex(5));
					edge.setSticker(0, getColorOfCubeByIndex(0));
				}
				else if(z == size) {
					edge.setSticker(4, getColorOfCubeByIndex(4));
					edge.setSticker(0, getColorOfCubeByIndex(0));
				}
				else {
					throw new CreateException("These coords are not edge!");
				}
			}
			else {
				throw new CreateException("These coords are not edge!");
			}
		}
		else if(y > 0 && y < size) {
			if(x == 0) {
				if(z == 0) {
					edge.setSticker(5, getColorOfCubeByIndex(5));
					edge.setSticker(3, getColorOfCubeByIndex(3));
				}
				else if(z == size) {
					edge.setSticker(3, getColorOfCubeByIndex(3));
					edge.setSticker(4, getColorOfCubeByIndex(4));
				}
				else {
					throw new CreateException("These coords are not edge!");
				}
			} else if(x == size) {
				if(z == 0) {
					edge.setSticker(5, getColorOfCubeByIndex(5));
					edge.setSticker(1, getColorOfCubeByIndex(1));
				}
				else if(z == size) {
					edge.setSticker(4, getColorOfCubeByIndex(4));
					edge.setSticker(1, getColorOfCubeByIndex(1));
				}
				else {
					throw new CreateException("These coords are not edge!");
				}
			}
			else {
				throw new CreateException("These coords are not edge!");
			}
		}
		else if(z > 0 && z < size) {
			if(y == 0) {
				if(x == 0) {
					edge.setSticker(3, getColorOfCubeByIndex(3));
					edge.setSticker(2, getColorOfCubeByIndex(2));
				}
				else if(x == size) {
					edge.setSticker(1, getColorOfCubeByIndex(1));
					edge.setSticker(2, getColorOfCubeByIndex(2));
				}
				else {
					throw new CreateException("These coords are not edge!");
				}
			} else if(y == size) {
				if(x == 0) {
					edge.setSticker(3, getColorOfCubeByIndex(3));
					edge.setSticker(0, getColorOfCubeByIndex(0));
				}
				else if(x == size) {
					edge.setSticker(1, getColorOfCubeByIndex(1));
					edge.setSticker(0, getColorOfCubeByIndex(0));
				}
				else {
					throw new CreateException("These coords are not edge!");
				}
			}
			else {
				throw new CreateException("These coords are not edge!");
			}
		}
		else {
			throw new CreateException("These coords are not edge!");
		}
		
		return edge;
	}
	
	public static Field getFlatFromCoords(int x, int y, int z, int size)
			throws CreateException, FieldException {
		size--;
		Field flat = new Field(FieldType.FLAT, x, y, z);
		
		if(x > 0 && x < size && y > 0 && y < size) {
			if(z == 0) {
				flat.setSticker(5, getColorOfCubeByIndex(5));
			}
			else if(z == size) {
				flat.setSticker(4, getColorOfCubeByIndex(4));
			}
			else {
				throw new CreateException("These coords are not edge!");
			}
		}
		else if(z > 0 && z < size && x > 0 && x < size) {
			if(y == size) {
				flat.setSticker(0, getColorOfCubeByIndex(0));
			}
			else if(y == 0) {
				flat.setSticker(2, getColorOfCubeByIndex(2));
			}
			else {
				throw new CreateException("These coords are not edge!");
			}
		}
		else if(z > 0 && z < size && y > 0 && y < size) {
			if(x == 0) {
				flat.setSticker(3, getColorOfCubeByIndex(3));
			}
			else if(x == size) {
				flat.setSticker(1, getColorOfCubeByIndex(1));
			}
			else {
				throw new CreateException("These coords are not edge!");
			}
		}
		else {
			throw new CreateException("These coords are not edge!");
		}
		
		return flat;
	}
	
	/**
	 * Filtruje všechny znaky v řetězci inputString, a podle toho, zda patří
	 * mezi znaky v řetězci allowedChars znak buď na jeho pozici zachová nebo
	 * jej nahradí znekam replacingChar. Pokud replacingChar == null, pak funkce
	 * vrátí řetězec obsahující pouze povolené znaky - nezachová
	 * se tedy původní pozice prvků.
	 * 
	 * Pokud deniedChars != null, pak probíhá vše stejně jako s allowedChars,
	 * akorát povoleny jsou všechny znaky kromě těch z deniedChars.
	 * 
	 * Vždy musí být buď allowedChars == null a deniedChars != null nebo
	 * allowedChars != null a deniedChars == null, jinak dojde k chybě.
	 * 
	 * @param inputString String vstupní řetězec
	 * @param allowedChars String povolené znaky
	 * @param replacingChar Character zástupný znak pro nepovolené znaky
	 * @return String upravený řetězec
	 * @throws FilterCharsError 
	 */
	public static String filterChars(String inputString, String allowedChars,
			String deniedChars, Character replacingChar) throws FilterCharsError {
		StringBuilder output = new StringBuilder();

		if(allowedChars != null && deniedChars == null) {		
			int size = inputString.length();
			int size2 = allowedChars.length();
			for(int i = 0; i < size; i++) {
				char actual = inputString.charAt(i);
				boolean passed = false;
				for(int j = 0; j < size2; j++) {
					if(actual == allowedChars.charAt(j)) {
						passed = true;
						break;
					}
				}

				if(passed) {
					output.append(actual);
				}
				else {
					if(replacingChar != null) {
						output.append(replacingChar);
					}
				}
			}
		}
		else if(allowedChars == null && deniedChars != null) {
			int size = inputString.length();
			int size2 = deniedChars.length();
			for(int i = 0; i < size; i++) {
				char actual = inputString.charAt(i);
				boolean passed = true;
				for(int j = 0; j < size2; j++) {
					if(actual == deniedChars.charAt(j)) {
						passed = false;
						break;
					}
				}

				if(passed) {
					output.append(actual);
				}
				else {
					if(replacingChar != null) {
						output.append(replacingChar);
					}
				}
			}
		}
		else {
			throw new FilterCharsError();
		}

		return output.toString();
	}
	
	public static String readString()
	{
		String s = "";
		try {
			InputStreamReader converter = new InputStreamReader(System.in);
			BufferedReader in = new BufferedReader(converter);
			s = in.readLine();
		} catch (Exception e) {
			
		}
		return s;
	}
	
	public static void sleep(int milis) {
		try {
		    Thread.sleep(milis);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}
}
