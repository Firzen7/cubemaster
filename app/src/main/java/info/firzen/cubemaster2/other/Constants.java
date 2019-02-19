package info.firzen.cubemaster2.other;

import android.content.Context;
import android.hardware.Camera;



public class Constants {
	public static final String FIELD_COLOR = "field_color";

	public static int scramblingMoves = 20;
	public static int cubeSize = 3;
	public static int rotateStepCount = 7;
	public static int renderingSleepTime = 25;
	public static int renderingCubeSleepTime = 75;
	
	public static Context baseContext;
	
	public static String whiteBalance = Camera.Parameters.WHITE_BALANCE_AUTO;
	public static int currentWhiteBalance = 0;
}
