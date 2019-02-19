package info.firzen.cubemaster2.gui;

import info.firzen.cubemaster2.backend.cube.Cube;
import info.firzen.cubemaster2.backend.cube.Move;
import info.firzen.cubemaster2.backend.cube.enums.RotationAxis;
import info.firzen.cubemaster2.backend.cube.enums.SideType;
import info.firzen.cubemaster2.backend.cube.geom.Point3D;
import info.firzen.cubemaster2.backend.cube.interfaces.IField;
import info.firzen.cubemaster2.backend.cube.interfaces.ISticker;
import info.firzen.cubemaster2.backend.exceptions.CreateException;
import info.firzen.cubemaster2.backend.exceptions.FieldException;
import info.firzen.cubemaster2.backend.exceptions.FilterCharsError;
import info.firzen.cubemaster2.backend.exceptions.ParseException;
import info.firzen.cubemaster2.backend.exceptions.PointException;
import info.firzen.cubemaster2.backend.exceptions.UnknownException;
import info.firzen.cubemaster2.cube.CubeColor;
import info.firzen.cubemaster2.other.Action;
import info.firzen.cubemaster2.other.Constants;
import info.firzen.cubemaster2.other.DataHolder;
import info.firzen.cubemaster2.other.DrawingAction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.preference.PreferenceManager;
import android.view.MotionEvent;

import com.threed.jpct.Camera;
import com.threed.jpct.Config;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Interact2D;
import com.threed.jpct.Light;
import com.threed.jpct.Matrix;
import com.threed.jpct.RGBColor;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import com.threed.jpct.util.AAConfigChooser;
import com.threed.jpct.util.MemoryHelper;

@SuppressLint("ClickableViewAccessibility")
public class CubeView extends GLSurfaceView {
	private MyRenderer renderer = null;
	private FrameBuffer fb = null;
	private World world = null;
	private RGBColor back = new RGBColor(45, 45, 45);

	private float touchTurn = 0;
	private float touchTurnUp = 0;

	private float xpos = -1;
	private float ypos = -1;

	private Cube3D cube = null;

	private Light sun = null;
	private float cameraDistance; 
	
	private boolean movesAllowed = true;
	private MoveRecognition moveRecognition = new MoveRecognition();

	private Action touchAction;
	private Action initAction;
	private Queue<Action> drawingActionQueue = new LinkedList<Action>();
	private boolean worldReady = false;

	// when user is currently rotating the whole cube, this value is set to true
	private boolean rotating = false;
	// when user is currently performing some move, this value is set to true
	private boolean moving = false;
	// determines if initAction was launched
	private boolean wasInitLaunched;
	private boolean rejectTouchEvents = false;
	
	public CubeView(Context context, Action touchAction) throws Exception {
		super(context);
		renderer = new MyRenderer();
		setEGLContextClientVersion(1);
		setEGLConfigChooser(new AAConfigChooser(this));
		setPreserveEGLContextOnPause(true);
		setRenderer(renderer);
		this.touchAction = touchAction;
		
		Config.glDither = false;
		Config.vertexBufferSize = 340;
		Config.useVBO = false;
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		int size = prefs.getInt("p_cube_size", 10);
		cameraDistance = (float) (Constants.cubeSize * 3.5 + 10.0 - (float) size);
		
		createWorld();
	}

	static int moves = 0;
	
	public boolean onTouchEvent(MotionEvent me) {
		int action = me.getAction();
		
		if(!rejectTouchEvents && isInitialized() && (action != MotionEvent.ACTION_DOWN
				|| rotationStopped())) {
			if(action == MotionEvent.ACTION_UP) {
				if(rotating) {
					rotating = false;
				}
				if(moving) {
					moveRecognition.reset();
					moving = false;
				}

				if(!isColorEditing()) {
					touchAction.run();
				}
			}

			if((!moving && (rotating || !movesAllowed || !touchedCube(me)))) {
				rotateCube(me);
				setTouchedStickerColor(me);
				if(!rotating && action == MotionEvent.ACTION_DOWN) {
					rotating = true;
				}
			}
			else if(!rotating && movesAllowed) {
				try {
					if(!moving && action == MotionEvent.ACTION_DOWN) {
						moving = true;
					}

					moveRecognition.addSticker(getTouchedSticker(me));
					if(moveRecognition.isReady()) {
						cube.doMove(moveRecognition.getFrom(), moveRecognition.getTo());
						moveRecognition.reset();
					}

					if(action == MotionEvent.ACTION_UP) {
						moveRecognition.reset();
					}
				} catch (Exception e) {
					// XXX ignore
					e.printStackTrace();
				}
			}

			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean rotationStopped() {
		float limit = 2f;
		return (touchTurn > -limit && touchTurn < limit &&
				touchTurnUp > -limit && touchTurnUp < limit);
	}
	
	private boolean touchedCube(MotionEvent me) {
		Sticker3D sticker = getTouchedSticker(me);
		return sticker != null;
	}
	
	private boolean isColorEditing() {
		CubeColor color = DataHolder.getInstance().getPickerColor();
		return !CubeColor.UNKNOWN.equals(color);
	}
	
	private void rotateCube(MotionEvent me) {
		if(!isColorEditing()) {
			if (me.getAction() == MotionEvent.ACTION_DOWN) {
				xpos = me.getX();
				ypos = me.getY();
			}

			if (me.getAction() == MotionEvent.ACTION_MOVE) {

				float xd = me.getX() - xpos;
				float yd = me.getY() - ypos;

				xpos = me.getX();
				ypos = me.getY();

				touchTurn = xd * (480.0f / (float) fb.getWidth());
				touchTurnUp = -yd * (480.0f / (float) fb.getWidth());
			}
		}
		
		try {
			Thread.sleep(Constants.renderingCubeSleepTime);
		} catch (Exception e) {
			// ignore
		}
	}
	
	private void slowDownRotation() {
		if(touchTurn != 0) {
			touchTurn = touchTurn / 2f;
		}
		if(touchTurnUp != 0) {
			touchTurnUp = touchTurnUp / 2f;
		}
	}
	
	private int getNumberInRange(int number, int from, int to) {
		if(number < from) {
			return from;
		}
		else if(number > to) {
			return to;
		}
		else {
			return number;
		}
	}
	
	private void setTouchedStickerColor(MotionEvent me) {
		final Sticker3D sticker = getTouchedSticker(me);
		final CubeColor color = DataHolder.getInstance().getPickerColor();
		
		if(sticker != null && !CubeColor.UNKNOWN.equals(color)) {
			addDrawingAction(new Action() {
				public void run() {
					try {
						Point3D loc = null;
						for(IField f : cube.getFields()) {
							for(ISticker s : f.getStickers()) {
								if(s != null) {
									if(s.equals(sticker)) {
										loc = f.getLocation();
										break;
									}
								}
							}
						}

						cube.setStickerColor(color, sticker.getSide(), loc);
					} catch (Exception e) {
						// XXX ignore
						e.printStackTrace();
					}
				}
			});
		}
	}
	
//	private List<Sticker3D> getVisibleSideStickers()
//			throws UnknownException {
//		SideType side = getActualSide();
//		
//		int width = fb.getWidth();
//		int height = fb.getHeight();
//
//		List<Sticker3D> stickers = new ArrayList<Sticker3D>();
//		
//		for(int y = 0; y < height; y += 20) {
//			for(int x = 0; x < width; x += 20) {
//				Sticker3D sticker = getStickerAtCoords(x, y);
//				if(sticker != null && sticker.getSide() == side
//						&& !stickers.contains(sticker)) {
//					stickers.add(sticker);
//				}
//			}
//		}
//
//		return stickers;
//	}
	
	public SideType getActualSide() throws UnknownException {
		int width = fb.getWidth();
		int height = fb.getHeight();
		int startX = getNumberInRange(width / 2 - 40, 0, width);
		int startY = getNumberInRange(height / 2 - 40, 0, height);
		int endX = getNumberInRange(width / 2 + 40, 0, width);
		int endY = getNumberInRange(height / 2 + 40, 0, height);
		
		List<Sticker3D> stickers = new ArrayList<Sticker3D>();
		
		for(int y = startY; y < endY; y += 20) {
			for(int x = startX; x < endX; x += 20) {
				Sticker3D sticker = getStickerAtCoords(x, y);
				if(sticker != null) {
					stickers.add(sticker);
				}
			}
		}
		
		Map<SideType, Integer> stats = new HashMap<SideType, Integer>();
		for(Sticker3D sticker : stickers) {
			SideType side = sticker.getSide();
			if(stats.get(side) != null) {
				stats.put(side, stats.get(side) + 1);
			}
			else {
				stats.put(side, 1);
			}
		}
		
		int max = 0;
		SideType winningSide = null;
		for(Entry<SideType, Integer> entry : stats.entrySet()) {
			if(entry.getValue() > max) {
				max = entry.getValue();
				winningSide = entry.getKey();
			}
		}
		
		return winningSide;
	}
	
	private Sticker3D getStickerAtCoords(int x, int y) {
		Camera cam = world.getCamera();
		SimpleVector dir = Interact2D.reproject2D3DWS(cam, fb, x, y).normalize();
		Object[] res = world.calcMinDistanceAndObject3D(
				cam.getPosition(), dir, 10000);
                                                                                                                                                                                                                                                                                                                                                                          
		if(res[0] != null && res[1] != null) {
			Sticker3D touchedSticker = (Sticker3D) res[1];
			return touchedSticker;
		}
		else {
			return null;
		}

	}
	
	private Sticker3D getTouchedSticker(MotionEvent me) {
		return getStickerAtCoords((int) me.getX(), (int) me.getY());
	}
	
	public void doMoves(String moves) throws PointException, ParseException,
			FilterCharsError {
		cube.doMoves(moves);
	}
	
	public void doMoves(List<Move> moves) throws PointException, ParseException,
			FilterCharsError {
		cube.doMoves(moves);
	}
	
	private void createWorld() throws Exception {
		if(world == null) {
			world = new World();
			world.setAmbientLight(80, 80, 80);
		}

		if(sun == null) {
			sun = new Light(world);
			sun.setIntensity(240, 240, 240);
		}

		if(cube == null) {
			cube = new Cube3D(Constants.cubeSize, world, fb);
			cube.setBackgroundColor(back);
			createCube();
		}

		Camera cam = world.getCamera();

		cam.moveCamera(Camera.CAMERA_MOVEUP, cameraDistance);
		cam.rotateCameraX((float)Math.PI / 2f);

		rotateCamera(new SimpleVector(0, 0, 1), 150);
		rotateCamera(new SimpleVector(1, 0, 0), -30);

		MemoryHelper.compact();
		
		worldReady = true;
	}

	public int getOrientation() throws UnknownException {
		SideType side = getActualSide();

		SimpleVector upVector = simplifyVector(world.getCamera().getUpVector());

		if(upVector.equals(new SimpleVector(0, 0, -1))) {
			return 0;
		}
		else if(upVector.equals(new SimpleVector(1, 0, 0))
				&& side != SideType.BACK && side != SideType.DOWN) {
			return 90;
		}
		else if(upVector.equals(new SimpleVector(1, 0, 0))
				&& side == SideType.DOWN) {
			return 270;
		}
		else if(upVector.equals(new SimpleVector(0, 0, 1))) {
			return 180;
		}
		else if(upVector.equals(new SimpleVector(1, 0, 0)) && side == SideType.BACK) {
			return 270;
		}
		else if(upVector.equals(new SimpleVector(0, 1, 0)) && side == SideType.UP) {
			return 180;
		}
		else if(upVector.equals(new SimpleVector(0, 1, 0)) && side == SideType.LEFT) {
			return 90;
		}
		else if(upVector.equals(new SimpleVector(0, 1, 0)) && side == SideType.RIGHT) {
			return 270;
		}
		else if(upVector.equals(new SimpleVector(-1, 0, 0)) && side != SideType.BACK
				&& side != SideType.DOWN) {
			return 270;
		}
		else if(upVector.equals(new SimpleVector(-1, 0, 0)) && side == SideType.BACK) {
			return 90;
		}
		else if(upVector.equals(new SimpleVector(-1, 0, 0)) && side == SideType.DOWN) {
			return 90;
		}
		else if(upVector.equals(new SimpleVector(0, -1, 0)) && side == SideType.RIGHT) {
			return 90;
		}
		else if(upVector.equals(new SimpleVector(0, -1, 0)) && side == SideType.DOWN) {
			return 180;
		}
		else if(upVector.equals(new SimpleVector(0, -1, 0)) && side == SideType.LEFT) {
			return 270;
		}
		else {
			return 0;
		}
	}
	
	private SimpleVector simplifyVector(SimpleVector vector) throws UnknownException {
		RotationAxis axis = RotationAxis.X;
		float max = vector.x;
		if(Math.abs(vector.y) > Math.abs(max)) {
			axis = RotationAxis.Y;
			max = vector.y;
		}
		if(Math.abs(vector.z) > Math.abs(max)) {
			axis = RotationAxis.Z;
			max = vector.z;
		}
		
		if(max > 0) {
			max = 1;
		}
		else {
			max = -1;
		}
		
		switch(axis) {
			case X: return new SimpleVector(max, 0, 0);
			case Y: return new SimpleVector(0, max, 0);
			case Z: return new SimpleVector(0, 0, max);
			default:
				throw new UnknownException();
		}
	}
	
	public void highlightSticker(ISticker sticker, boolean state) {
		try {
			final Sticker3D s = (Sticker3D) cube.getSticker(sticker.getSide(),
					sticker.getParent().getLocation());

			if(state) {
				s.setAdditionalColor(255, 255, 255);
			}
			else {
				s.clearAdditionalColor();
			}
		}
		catch(Exception e) {
			// XXX ingore
		}
	}
	
	private ISticker[] getStickersArray(List<ISticker> stickers) {
		int size = stickers.size();
		ISticker[] output = new ISticker[size];

		for(int i = 0; i < size; i++) {
			output[i] = stickers.get(i);
		}
		
		return output;
	}

	public void highlightFields(List<IField> fields) throws UnknownException {
		sun.setIntensity(100, 100, 100);

		List<ISticker> stickersList = new ArrayList<ISticker>();
		
		for(IField field : fields) {
			stickersList.addAll(Arrays.asList(field.getStickers()));
		}
		
		final ISticker[] stickers = getStickersArray(stickersList);
		
		final int count = 30;
		for(int i = 0; i < count; i++) {
			final int j = i;
			
			addDrawingAction(new Action() {
				public void run() {
					if(j % 3 == 0) {
						for(ISticker sticker : stickers) {
							if(sticker != null) {
								highlightSticker(sticker, true);
							}
						}
					}
				}
			});
			addDrawingAction(new Action() {
				public void run() {
					if(j % 6 == 0 || j + 1 == count) {
						for(ISticker sticker : stickers) {
							if(sticker != null) {
								highlightSticker(sticker, false);
							}
						}
					}
					
					if(j + 1 == count) {
						sun.setIntensity(240, 240, 240);
					}
				}
			});
		}
	}
	
	private void createCube() {
		List<IField> fields = cube.getFields();

		for(IField f : fields) {
			ISticker[] stickers = f.getStickers();
			for(ISticker s : stickers) {
				if(s != null) {
					world.addObject((Sticker3D) s);
				}
			}
		}
	}
	
	public void setCube(final Cube c) throws FieldException, CreateException,
			ParseException, UnknownException {

		addDrawingAction(new Action() {
			public void run() {
				try {
					List<IField> fields = c.getFields();

					for(IField f : fields) {
						SideType[] sides = SideType.values();
						for(SideType side : sides) {
							ISticker sticker = f.getSticker(side);
							if(sticker != null) {
								cube.setStickerColor(CubeColor.parseRgb(sticker.getColor()),
										side, f.getLocation());
							}
						}
					}
				}
				catch(Exception e) {
					// TODO
					e.printStackTrace();
				}
				finally {
					setRejectTouchEvents(false);
				}
			}
		});
	}
	
	public Cube3D getCube() {
		return cube;
	}
	
	private void rotateCamera(SimpleVector line, int angle) {
		Camera cam = world.getCamera();

		Matrix m = line.normalize().getRotationMatrix();

		m.rotateAxis(m.getXAxis(), (float) -Math.PI / 2f);
		cam.moveCamera(Camera.CAMERA_MOVEIN, cameraDistance);
		cam.rotateAxis(m.invert3x3().getXAxis(), (float) Math.toRadians(angle));
		cam.moveCamera(Camera.CAMERA_MOVEOUT, cameraDistance);
	}

	public void clearUndo() {
		cube.clearUndo();
	}

	public void clearRedo() {
		cube.clearRedo();
	}
	
	public void undo() throws PointException, UnknownException {
		cube.undo();
	}
	
	public void redo() throws PointException, UnknownException {
		cube.redo();
	}
	
	public Action getDrawingAction() {
		return drawingActionQueue.poll();
	}

	public void addDrawingAction(Action drawingAction) {
		drawingActionQueue.add(drawingAction);
	}

	public Action getInitAction() {
		return initAction;
	}

	public void setInitAction(Action initAction) {
		this.initAction = initAction;
	}

	public boolean isMovesAllowed() {
		return movesAllowed;
	}

	public void setMovesAllowed(boolean movesAllowed) {
		this.movesAllowed = movesAllowed;
	}
	
	public boolean isInitialized() {
		if(fb == null) {
			return false;
		}
		else {
			return fb.isInitialized() && worldReady;
		}
	}

	public boolean isRejectTouchEvents() {
		return rejectTouchEvents;
	}

	public void setRejectTouchEvents(boolean rejectTouchEvents) {
		this.rejectTouchEvents = rejectTouchEvents;
	}

	class MyRenderer implements GLSurfaceView.Renderer {
		private GL10 lastGl = null;
		
		public MyRenderer() {
		}

		public void onSurfaceChanged(GL10 gl, int w, int h) {
			if(!gl.equals(lastGl)) {
				if (fb != null) {
					fb.dispose();
				}
				fb = new FrameBuffer(gl, w, h);
				lastGl = gl;
			}
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		}

		public void onDrawFrame(GL10 gl) {
			Action action = null;
			if(!drawingActionQueue.isEmpty()) {
				action = getDrawingAction();
				action.run();
			}
			
			Camera cam = world.getCamera();
			SimpleVector line = new SimpleVector(touchTurn, 0, touchTurnUp);
			Matrix m = line.normalize().getRotationMatrix();

			m.rotateAxis(m.getXAxis(), (float) -Math.PI / 2f);
			cam.moveCamera(Camera.CAMERA_MOVEIN, cameraDistance);
			cam.rotateAxis(m.invert3x3().getXAxis(), line.length() / 200f);
			cam.moveCamera(Camera.CAMERA_MOVEOUT, cameraDistance);

			sun.setPosition(cam.getPosition());
			
			slowDownRotation();

			fb.clear(back);
			world.renderScene(fb);
			world.draw(fb);
			fb.display();
			
			if(!wasInitLaunched && isInitialized()) {
				if(initAction != null) {
					initAction.run();
					wasInitLaunched = true;
				}
			}
			
			if(action != null && action instanceof DrawingAction) {
				((DrawingAction) action).finish();
			}
		}
	}
}
