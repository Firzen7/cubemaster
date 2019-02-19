package info.firzen.cubemaster2.activities;

import info.firzen.cubemaster2.R;
import info.firzen.cubemaster2.activities.help.HelpActivity;
import info.firzen.cubemaster2.backend.cube.Cube;
import info.firzen.cubemaster2.backend.cube.Move;
import info.firzen.cubemaster2.backend.cube.enums.BasicMove;
import info.firzen.cubemaster2.backend.cube.enums.SideType;
import info.firzen.cubemaster2.backend.cube.interfaces.IField;
import info.firzen.cubemaster2.backend.cube.interfaces.ISticker;
import info.firzen.cubemaster2.backend.cube.solver.Solver2;
import info.firzen.cubemaster2.backend.cube.solver.Solver3;
import info.firzen.cubemaster2.backend.exceptions.FilterCharsError;
import info.firzen.cubemaster2.backend.exceptions.ParseException;
import info.firzen.cubemaster2.backend.exceptions.PointException;
import info.firzen.cubemaster2.backend.exceptions.SolveException;
import info.firzen.cubemaster2.backend.exceptions.UnknownException;
import info.firzen.cubemaster2.backend.exceptions.UnsupportedCubeSize;
import info.firzen.cubemaster2.cube.CubeColor;
import info.firzen.cubemaster2.gui.ColorChooser;
import info.firzen.cubemaster2.gui.Cube3D;
import info.firzen.cubemaster2.gui.CubeView;
import info.firzen.cubemaster2.gui.LayerSolution;
import info.firzen.cubemaster2.gui.LayerSolutions;
import info.firzen.cubemaster2.gui.Sticker3D;
import info.firzen.cubemaster2.other.Action;
import info.firzen.cubemaster2.other.Constants;
import info.firzen.cubemaster2.other.DataHolder;
import info.firzen.cubemaster2.other.DrawingAction;
import info.firzen.cubemaster2.other.ErrorsCatcher;
import info.firzen.cubemaster2.other.RepeatListener;
import info.firzen.cubemaster2.other.Useful;
import info.firzen.cubemaster2.recognition.Rgb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class CubeActivity extends Activity {
	private CubeView cubeView;
	
	private Button solve;
	private Button load;
	private Button edit;
	
	private Button undo;
	private Button redo;
	
	private FrameLayout progress;
	private TextView currentSide;
	private TextView lastMove;
	
	private ColorChooser colorChooser;
	
	private Cube backup;
	
	// fields for cube solution
	private LayerSolutions layerSolutions = new LayerSolutions();
	private LayerSolution currentLayerSolution;
	
	private boolean editMode = false;
	private boolean solvingMode = false;
	private boolean showHints = true;
	
	// here is stored progress dialog for solving or testing cube
	private ProgressDialog pd;

	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ErrorsCatcher(this));
		setContentView(R.layout.activity_cube);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		Constants.renderingCubeSleepTime = 150 / (prefs.getInt("p_rotation_speed", 1) + 1);
		Constants.rotateStepCount = 1 + (10 - prefs.getInt("p_moves_speed", 4));
		Constants.scramblingMoves = prefs.getInt("p_scramble", 20);
		showHints = prefs.getBoolean("p_show_layers_help", true);

		createGui();
		initActions();
	}
	
	// *************************************************************************
	// Initializations
	// *************************************************************************
	
	private void createGui() {
		DataHolder.getInstance().setPickerColor(CubeColor.UNKNOWN);
		
		edit = (Button) findViewById(R.id.button_edit);
		load = (Button) findViewById(R.id.button_load);
		solve = (Button) findViewById(R.id.button_play);
		undo = (Button) findViewById(R.id.button_back);
		redo = (Button) findViewById(R.id.button_forward);
		
		lastMove = (TextView) findViewById(R.id.lastMove);
		currentSide = (TextView) findViewById(R.id.actualSide);
		
		colorChooser = (ColorChooser) findViewById(R.id.colorChooser);
		
		progress = (FrameLayout) findViewById(R.id.progress);
		
		try {
			cubeView = new CubeView(getApplicationContext(), new Action() {
				public void run() {
					refreshOnTouch();
				}
			});
		} catch (Exception e) {
			Useful.showError(getString(R.string.error_init_cube),
					CubeActivity.this, e);
		}
		
		cubeView.setInitAction(new Action() {
			public void run() {
				hideProgressBar();
			}
		});
		
		FrameLayout cube = (FrameLayout) findViewById(R.id.cubeView);
		cube.addView(cubeView);
	}
	
	private void initActions() {		
		solve.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(!editMode) {
					toggleSolvingMode();
				}
				else {
					rotateActualSide();
				}
				refreshUndoRedo();
			}
		});
		
		solve.setOnLongClickListener(new OnLongClickListener() { 
	        public boolean onLongClick(View v) {
	            if(editMode) {
	            	selectFlipType();
	            }
	            return editMode;
	        }
	    });
		
		load.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(Useful.isThereCamera(CubeActivity.this)) {
					setColoringMode(false);
					loadSideByCamera();
				}
				else {
					Useful.showInfo(getString(R.string.error_no_camera),
							CubeActivity.this);
				}
			}
		});
		
		edit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(!solvingMode) {
					blockGui();
					setColoringMode(false);
					toggleEditMode();
				}
				else {
					showActualLayerHelp();
				}
			}
		});
		
		undo.setOnTouchListener(new RepeatListener(600, 200,
				new View.OnClickListener() {
			public void onClick(View v) {
				try {
					refreshUndoRedo();
					cubeView.undo();
					refreshLastMove();
				} catch (Exception e) {
					Useful.showError(getString(R.string.error_cant_move),
							CubeActivity.this, e);
				}
			}
		}));
		
		redo.setOnTouchListener(new RepeatListener(600, 200,
				new View.OnClickListener() {
			public void onClick(View v) {
				try {
					if(!DataHolder.getInstance().isHelpShown()) {
						refreshUndoRedo();

						if(solvingMode) {
							if(!cubeView.getCube().redoPossible()) {
								currentLayerSolution = layerSolutions.getNextSolution();
								if(currentLayerSolution != null) {
									List<Move> moves = currentLayerSolution.getLayerMoves();
									Collections.reverse(moves);

									cubeView.getCube().setRedoMoves(moves);
									if(showHints) {
										DataHolder.getInstance().setHelpShown(true);
										blockGui();
										currentLayerSolution.startActionBefore();
									}
									else {
										cubeView.redo();
									}
								}
							}
							else {
								cubeView.redo();
							}
						}
						else {
							cubeView.redo();
						}
						
						refreshLastMove();
					}
				} catch (Exception e) {
					Useful.showError(getString(R.string.error_cant_move),
							CubeActivity.this, e);
				}
			}
		}));

		colorChooser.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				refreshColoringMode();
			}
		});
	}
	
	// *************************************************************************
	// Refreshing
	// *************************************************************************
	
	/**
	 * Refreshes information about currently visible side and last move.
	 */
	private void refreshOnTouch() {
		runOnUiThread(new Runnable() {
			public void run() {
				String actual = getString(R.string.error);
				try {
					actual = cubeView.getActualSide().toString();
				}
				catch (Exception e) {
					// ok
				}

				currentSide.setText(actual);

				refreshLastMove();
			}
		});
	}
	
	private void hideProgressBar() {
		Handler refresh = new Handler(Looper.getMainLooper());
		refresh.post(new Runnable() {
		    public void run() {
		    	progress.setVisibility(View.INVISIBLE);
		    	currentSide.setVisibility(View.VISIBLE);
		    	
		    	undo.setVisibility(View.VISIBLE);
		    	redo.setVisibility(View.VISIBLE);
		    	solve.setVisibility(View.VISIBLE);
		    	edit.setVisibility(View.VISIBLE);
		 
		    	undo.setEnabled(true);
		    	redo.setEnabled(true);
		    	solve.setEnabled(true);
		    	edit.setEnabled(true);
		    }
		});
	}
	
	private void refreshLastMove() {
		List<Move> moves = cubeView.getCube().getUndoMoves();
		
		if(!moves.isEmpty()) {
			lastMove.setText(moves.size() + " / "
					+ moves.get(moves.size() - 1).toString(Constants.cubeSize));
		}
		else {
			lastMove.setText("");
		}
	}

	private void refreshUndoRedo() {
		if(editMode) {
			undo.setVisibility(View.INVISIBLE);
			redo.setVisibility(View.INVISIBLE);
		}
		else {
			undo.setVisibility(View.VISIBLE);
			redo.setVisibility(View.VISIBLE);
		}
	}
	
	// *************************************************************************
	// Cube editing
	// *************************************************************************
	
	private void selectFlipType() {
		final CharSequence[] items = {
				getString(R.string.flip_horizontal),
				getString(R.string.flip_vertical)};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.choose_flip_type);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch(item) {
				case 0: flipActualSideHorizontal(); break;
				case 1: flipActualSideVertical(); break;
				default: break;
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void flipActualSideHorizontal() {
		try {
			blockGui();
			setActualSideColors(flipSideHorizontal(getActualSideColors()));
		} catch (Exception e) {
			Useful.showError(getString(R.string.error_flip),
					CubeActivity.this, e);
			unblockGui();
		}
	}
	
	private void flipActualSideVertical() {
		try {
			blockGui();
			setActualSideColors(flipSideVertical(getActualSideColors()));
		} catch (Exception e) {
			Useful.showError(getString(R.string.error_flip),
					CubeActivity.this, e);
			unblockGui();
		}
	}
	
	private void rotateActualSide() {
		try {
			blockGui();
			setActualSideColors(rotateSide(getActualSideColors()));
		} catch (Exception e) {
			unblockGui();
		}
	}
	
	private List<CubeColor> flipSideVertical(List<CubeColor> colors) {
		int size = Constants.cubeSize;
		CubeColor[][] side = new CubeColor[size][size];

		int i = 0;
		for(int y = 0; y < size; y++) {
			for(int x = 0; x < size; x++) {
				side[x][y] = colors.get(i);
				i++;
			}
		}

		List<CubeColor> rotated = new ArrayList<CubeColor>(); 

		for(int y = size - 1; y >= 0; y--) {
			for(int x = 0; x < size; x++) {
				rotated.add(side[x][y]);
			}
		}

		return rotated;
	}
	
	private List<CubeColor> flipSideHorizontal(List<CubeColor> colors) {
		int size = Constants.cubeSize;
		CubeColor[][] side = new CubeColor[size][size];

		int i = 0;
		for(int y = 0; y < size; y++) {
			for(int x = 0; x < size; x++) {
				side[x][y] = colors.get(i);
				i++;
			}
		}

		List<CubeColor> rotated = new ArrayList<CubeColor>(); 

		for(int y = 0; y < size; y++) {
			for(int x = size - 1; x >= 0; x--) {
				rotated.add(side[x][y]);
			}
		}

		return rotated;
	}
	
	private List<CubeColor> rotateSide(List<CubeColor> colors) {
		int size = Constants.cubeSize;
		CubeColor[][] side = new CubeColor[size][size];

		int i = 0;
		for(int y = 0; y < size; y++) {
			for(int x = 0; x < size; x++) {
				side[x][y] = colors.get(i);
				i++;
			}
		}

		List<CubeColor> rotated = new ArrayList<CubeColor>(); 

		for(int y = 0; y < size; y++) {
			for(int x = 0; x < size; x++) {
				rotated.add(side[y][size - x - 1]);
			}
		}

		return rotated;
	}
	
	private void loadSideByCamera() {
		try {
			List<CubeColor> colors = getActualSideColors();
			DataHolder.getInstance().setColors(colors);

			DetectActivity.setAfterAction(new Action() {
				public void run() {
					try {
						setActualSideColors(orientColorsByCamera(
								DataHolder.getInstance().getColors()));
					} catch (UnknownException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			
			Intent intent = new Intent(CubeActivity.this,
					DetectActivity.class);
			startActivity(intent);
		} catch (Exception e) {
			Useful.showError(getString(R.string.error_no_camera),
					CubeActivity.this, e);
		}
	}
	
	private List<CubeColor> orientColorsByCamera(List<CubeColor> colors)
			throws UnknownException {
		if(colors != null) {
			SideType side = cubeView.getActualSide();
			float angle = cubeView.getOrientation();

			if(angle == 0) {
				if(side != SideType.DOWN) {
					return colors;
				}
				else {
					return flipSideHorizontal(colors);
				}
			}
			else if(angle == 90) {
				if(side != SideType.DOWN) {
					return rotateSide(colors);
				}
				else {
					return flipSideVertical(rotateSide(colors));
				}
			}
			else if(angle == 180) {
				if(side != SideType.DOWN) {
					return rotateSide(rotateSide(colors));
				}
				else {
					return flipSideHorizontal(rotateSide(rotateSide(colors)));
				}
			}
			else if(angle == 270) {
				if(side != SideType.DOWN) {
					return rotateSide(rotateSide(rotateSide(colors)));
				}
				else {
					return flipSideVertical(rotateSide(rotateSide(rotateSide(colors))));
				}
			}
			else {
				throw new UnknownException(getString(R.string.error_cam_orientation));
			}
		}
		else {
			throw new UnknownException(getString(R.string.error_null_colors)
					+ " " + getString(R.string.error_hardware_failure));
		}
	}
	
	private void setActualSideColors(final List<CubeColor> colors) {
		cubeView.addDrawingAction(new DrawingAction() {
			public void run() {
				try {
					SideType side = cubeView.getActualSide();
					
					int index = 0;
					for(int y = 0; y < Constants.cubeSize; y++) {
						for(int x = 0; x < Constants.cubeSize; x++) {
							CubeColor color = colors.get(index);
							cubeView.getCube().setStickerColor(color, side, x, y);
							index++;
						}
					}
				} catch (Exception e) {
					unblockGui();
				}
			}
			
			public void finish() {
				unblockGui();
			}
		});
	}
	
	private List<CubeColor> getActualSideColors() throws UnknownException,
			ParseException {
		List<CubeColor> colors = new ArrayList<CubeColor>();
		SideType side = cubeView.getActualSide();
		
		List<Sticker3D> stickers = cubeView.getCube().getStickersFromSide(side);
		
		for(Sticker3D sticker : stickers) {
			colors.add(CubeColor.parseRgb((Rgb) sticker.getColor()));
		}
		return colors;
	}
	
	private void resetCube() {
		if(cubeView.isInitialized()) {
			if(!solvingMode) {
				blockGui();
				try {
					Cube cube = new Cube(Constants.cubeSize);
					cubeView.setCube(cube);
					cubeView.clearRedo();
					cubeView.clearUndo();
					refreshUndoRedo();
					refreshLastMove();
				}
				catch (Exception e) {
					Useful.showError(getString(R.string.error_reset_cube), CubeActivity.this, e);
				}
				finally {
					unblockGui(false);
				}
			}
			else {
				Useful.showInfo(getString(R.string.cant_reset_cube_while_solving),
						CubeActivity.this);
			}
		}
	}
	
	private void scrambleCube() {
		if(cubeView.isInitialized()) {
			if(!solvingMode) {
				int backup = Constants.rotateStepCount;
				Constants.rotateStepCount = 1;

				try {
					blockGui();
					cubeView.getCube().doMoves(Move.getRandomMoves(Constants.cubeSize,
							Constants.scramblingMoves));
					cubeView.getCube().clearUndo();
					cubeView.getCube().clearRedo();
					unblockGui();
				}
				catch (Exception e) {
					Useful.showError(getString(R.string.error_scrambling),
							CubeActivity.this);
				}
				finally {
					Constants.rotateStepCount = backup;
				}
			}
			else {
				Useful.showInfo(getString(R.string.cant_scramble_while_solving),
						CubeActivity.this);
			}
		}
	}
	
	// *************************************************************************
	// Mode changing
	// *************************************************************************

	private void setColoringMode(boolean enabled) {
		if(!enabled) {
			DataHolder.getInstance().setPickerColor(CubeColor.UNKNOWN);
		}
	}
	
	private void refreshColoringMode() {
		if(CubeColor.UNKNOWN.equals(DataHolder.getInstance().getPickerColor())) {
			cubeView.setMovesAllowed(true);
		}
		else {
			cubeView.setMovesAllowed(false);
		}
	}
	
	private void backupCube() {
		try {
			backup = cubeView.getCube().getCube();
		} catch (Exception e) {
			Useful.showError(getString(R.string.error_saving_cube_state),
					CubeActivity.this, e);
		}
	}
	
	private void toggleEditMode() {
		showProgressDialog(getString(R.string.checking_cube),
				getString(R.string.checking_cube_validity));
		
		final Action switchAction = new Action() {
			public void run() {
				if(!editMode || (editMode && isCubeValid())) {
					editMode = !editMode;

					runOnUiThread(new Runnable() {
						public void run() {
							if(editMode) {
								backupCube();
								solve.setText(R.string.rotate_side);
								load.setVisibility(View.VISIBLE);
								edit.setText(R.string.game_mode);
								lastMove.setVisibility(View.INVISIBLE);
								colorChooser.setVisibility(View.VISIBLE);
								colorChooser.refreshSelection();
							}
							else {
								colorChooser.setVisibility(View.INVISIBLE);
								solve.setText(R.string.show_solution_now);
								load.setVisibility(View.INVISIBLE);
								edit.setText(R.string.edit_cube);
								lastMove.setVisibility(View.VISIBLE);
							}

							setMovingMode(!editMode);
							refreshUndoRedo();
							refreshLastMove();
						}
					});
				}

				unblockGui();
				hideProgressDialog();
			}
		};

		if(editMode) {
			new Thread() {
				public void run() {
					switchAction.run();
				}
			}.start();
		}
		else {
			switchAction.run();
		}
	}

	private boolean solveCube() {
		try {
			final Cube c = cubeView.getCube().getCube();
			
			if(!c.isSolved()) {
				showProgressDialog(getString(R.string.solving_cube),
						getString(R.string.computation_in_progress));
				
				new Thread() {
					public void run() {
						try {
							if(Constants.cubeSize == 2) {
								prepareCube_2_Solution(c);
								
							}
							else if(Constants.cubeSize == 3) {
								prepareCube_3_Solution(c);
							}
							
							runOnUiThread(new Runnable() {
								public void run() {
									Useful.showToast(getString(R.string.show_moves_to_solution),
											CubeActivity.this);
								}
							});
						}
						catch (final Exception e) {
							runOnUiThread(new Runnable() {
								public void run() {
									hideProgressDialog();
									Useful.showError(getString(R.string.error_solving_cube),
											CubeActivity.this, e);
								}
							});
						}
					}
				}.start();
				
				return true;
			}
			else {
				Useful.showInfo(getString(R.string.cube_is_solved), CubeActivity.this);
				return false;
			}
		} catch (Exception e) {
			hideProgressDialog();
			Useful.showError(getString(R.string.error_solving_cube),
					CubeActivity.this, e);
			return false;
		}
	}
	
	private void toggleSolvingMode() {
		if(solvingMode || Constants.cubeSize == 2 || Constants.cubeSize == 3) {
			solvingMode = !solvingMode;

			layerSolutions.reset();
			currentLayerSolution = null;

			if(solvingMode) {
				if(solveCube()) {
					cubeView.getCube().clearUndo();
					cubeView.getCube().clearRedo();

					solve.setText(R.string.play_cube);
					edit.setText(R.string.layer_help);
					setMovingMode(false);
				}
				else {
					solvingMode = !solvingMode;
				}
			}
			else {
				cubeView.getCube().clearUndo();
				cubeView.getCube().clearRedo();

				solve.setText(R.string.show_solution_now);
				edit.setText(R.string.edit_cube);
				setMovingMode(true);
			}
			
			refreshLastMove();
		}
		else {
			Useful.showError(getString(R.string.error_unsupported_cube_size), CubeActivity.this);
		}
	}
	
	// *************************************************************************
	// Cube solution
	// *************************************************************************

	private void showActualLayerHelp() {
		if(currentLayerSolution != null) {
			currentLayerSolution.startActionBefore();
		}
		else {
			Useful.showInfo(getString(R.string.no_instructions_so_far),	CubeActivity.this);
		}
	}
	
	@SuppressLint("InflateParams")
	private void prepareCube_3_Solution(final Cube c) throws UnknownException,
			PointException, UnsupportedCubeSize, ParseException,
			SolveException, FilterCharsError {
		Solver3 s3 = new Solver3(c);
		c.clearUndo();

		Action a = new Action() {
			public void run() {
				Useful.showCustomDialog(R.layout.bottom_cross_3x3x3,
						getString(R.string.step) + " 1/8",
						CubeActivity.this);
				unblockGui();
			}
		};

		s3.solveDownCross();
		layerSolutions.addSolution(new LayerSolution(c.getUndoMoves(), a));
		c.clearUndo();

		a = new Action() {
			public void run() {
				Useful.showCustomDialog(R.layout.bottom_cross_3x3x3_2,
						getString(R.string.step) + " 2/8",
						CubeActivity.this);
				unblockGui();
			}
		};

		s3.solveUpCross();
		layerSolutions.addSolution(new LayerSolution(c.getUndoMoves(), a));
		c.clearUndo();

		a = new Action() {
			public void run() {
				Useful.showCustomDialog(R.layout.corners_3x3x3,
						getString(R.string.step) + " 3/8",
						CubeActivity.this);
				unblockGui();
			}
		};

		s3.solveUpCorners();
		layerSolutions.addSolution(new LayerSolution(c.getUndoMoves(), a));
		c.clearUndo();

		a = new Action() {
			public void run() {
				Useful.showCustomDialog(R.layout.second_layer_3x3x3,
						getString(R.string.step) + " 4/8",
						CubeActivity.this);
				unblockGui();
			}
		};

		c.rotateCube(BasicMove.RIGHT);
		c.rotateCube(BasicMove.RIGHT);

		s3.solveSecondLayer();
		layerSolutions.addSolution(new LayerSolution(c.getUndoMoves(), a));
		c.clearUndo();

		a = new Action() {
			public void run() {
				Useful.showCustomDialog(R.layout.final_cross_3x3x3,
						getString(R.string.step) + " 5/8",
						CubeActivity.this);
				unblockGui();
			}
		};

		s3.makeUpCross();
		layerSolutions.addSolution(new LayerSolution(c.getUndoMoves(), a));
		c.clearUndo();

		a = new Action() {
			public void run() {
				Useful.showCustomDialog(R.layout.final_cross_3x3x3_2,
						getString(R.string.step) + " 6/8",
						CubeActivity.this);
				unblockGui();
			}
		};

		s3.orientEdges();
		s3.solveEdges();
		layerSolutions.addSolution(new LayerSolution(c.getUndoMoves(), a));
		c.clearUndo();

		a = new Action() {
			public void run() {
				Useful.showCustomDialog(R.layout.final_corners_3x3x3,
						getString(R.string.step) + " 7/8",
						CubeActivity.this);
				unblockGui();
			}
		};

		s3.solveCorners();
		layerSolutions.addSolution(new LayerSolution(c.getUndoMoves(), a));
		c.clearUndo();

		a = new Action() {
			public void run() {
				Useful.showCustomDialog(R.layout.final_corners_3x3x3_2,
						getString(R.string.step) + " 8/8",
						CubeActivity.this);
				unblockGui();
			}
		};

		s3.finalizeCorners();
		s3.finalizeLayers();
		layerSolutions.addSolution(new LayerSolution(c.getUndoMoves(), a));
		c.clearUndo();

		if(!c.isSolved()) {
			throw new SolveException(getString(R.string.error_solving_fatal));
		}
		
		hideProgressDialog();
	}
	
	@SuppressLint("InflateParams")
	private void prepareCube_2_Solution(Cube c) throws Exception {
		Solver2 s2 = new Solver2(c);
		c.clearUndo();

		if(!s2.isFirstLayerComplete()) {
			Action a = new Action() {
				public void run() {
					Useful.showCustomDialog(R.layout.first_layer_2x2x2,
							getString(R.string.step) + " 1/3", CubeActivity.this);
					unblockGui();
				}
			};
			
			s2.solveUpCorners();
			layerSolutions.addSolution(new LayerSolution(c.getUndoMoves(), a));
			c.clearUndo();		
		}
		
		if(!s2.isSecondLayerOriented()) {
			Action a = new Action() {
				public void run() {
					Useful.showCustomDialog(R.layout.second_layer_2x2x2,
							getString(R.string.step) + " 2/3", CubeActivity.this);
					unblockGui();
				}
			};

			s2.rearrangeDownCorners();
			layerSolutions.addSolution(new LayerSolution(c.getUndoMoves(), a));
			c.clearUndo();
		}

		Action a = new Action() {
			public void run() {
				Useful.showCustomDialog(R.layout.second_layer_2x2x2_2,
						getString(R.string.step) + " 3/3",CubeActivity.this);
				unblockGui();
			}
		};
		
		s2.finnishSolving();
		layerSolutions.addSolution(new LayerSolution(c.getUndoMoves(), a));
		c.clearUndo();
		
		if(!c.isSolved()) {
			throw new SolveException(getString(R.string.error_solving_fatal));
		}
		
		hideProgressDialog();
	}
	
	// *************************************************************************
	// Others
	// *************************************************************************
	
	private void showProgressDialog(String title, String message) {
		pd = Useful.showProgressDialog(title, message, CubeActivity.this);
	}
	
	private void hideProgressDialog() {
		if(pd != null) {
			pd.dismiss();
		}
	}
	
	private void blockGui() {
		Handler refresh = new Handler(Looper.getMainLooper());
		refresh.post(new Runnable() {
			public void run() {
				cubeView.setRejectTouchEvents(true);
				edit.setEnabled(false);
				solve.setEnabled(false);
				load.setEnabled(false);
				undo.setEnabled(false);
				redo.setEnabled(false);
			}
		});
	}
	
	private void unblockGui(final boolean unblockCube) {
		Handler refresh = new Handler(Looper.getMainLooper());
		refresh.post(new Runnable() {
			public void run() {
				cubeView.setRejectTouchEvents(!unblockCube);
				edit.setEnabled(true);
				solve.setEnabled(true);
				load.setEnabled(true);
				undo.setEnabled(true);
				redo.setEnabled(true);
			}
		});		
	}
	
	private void unblockGui() {
		unblockGui(true);
	}
	
	private boolean isCubeValid() {
		Cube c = null;
		Cube3D c3d = cubeView.getCube();
		
		try {
			List<IField> fields = c3d.getFields();
			final List<IField> invalidFields = new ArrayList<IField>();
			
			for(IField field : fields) {
				if(!field.isValid()) {
					invalidFields.add(field);
				}

				for(ISticker sticker : field.getStickers()) {
					if(sticker != null && CubeColor.UNKNOWN.getColor()
							.equals(sticker.getColor())) {
						invalidFields.add(field);
						break;
					}
				}
			}
			
			if(!invalidFields.isEmpty()) {
				checkInvalidFields(getString(R.string.check_highlighted_fields),
						invalidFields);
				return false;
			}
			
			List<IField> f1 = c3d.getFieldsByColor(CubeColor.BLUE.getColor());
			List<IField> f2 = c3d.getFieldsByColor(CubeColor.RED.getColor());
			List<IField> f3 = c3d.getFieldsByColor(CubeColor.GREEN.getColor());
			List<IField> f4 = c3d.getFieldsByColor(CubeColor.WHITE.getColor());
			List<IField> f5 = c3d.getFieldsByColor(CubeColor.ORANGE.getColor());
			List<IField> f6 = c3d.getFieldsByColor(CubeColor.YELLOW.getColor());
			
			final int colorsCount = (int) Math.pow(Constants.cubeSize, 2);
			
			if(f1.size() != colorsCount) {
				invalidFields.addAll(f1);
			}
			if(f2.size() != colorsCount) {
				invalidFields.addAll(f2);
			}
			if(f3.size() != colorsCount) {
				invalidFields.addAll(f3);
			}
			if(f4.size() != colorsCount) {
				invalidFields.addAll(f4);
			}
			if(f5.size() != colorsCount) {
				invalidFields.addAll(f5);
			}
			if(f6.size() != colorsCount) {
				invalidFields.addAll(f6);
			}
			
			if(!invalidFields.isEmpty()) {
				checkInvalidFields(getString(R.string.invalid_counts_of_colours), null);
				return false;
			}
			
			try {
				c = cubeView.getCube().getCube();
			}
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}

			if(Constants.cubeSize == 2) {
				Solver2 s2 = new Solver2(c);
				s2.getSolution();
			}
			else if(Constants.cubeSize == 3) {
				Solver3 s3 = new Solver3(c);
				s3.getSolution();
			}
			
			if(!c.isSolved()) {
				checkInvalidFields(null, null);
				return false;
			}
		}
		catch (Exception e) {
			checkInvalidFields(null, null);
			return false;
		}
		
		return true;
	}
	
	private void checkInvalidFields(final String repairHint,
			final List<IField> invalidFields) {
		final Action repair = new Action() {
			public void run() {
				if(repairHint != null) {
					Useful.showToast(repairHint, CubeActivity.this);
				}
				if(invalidFields != null && !invalidFields.isEmpty()) {
					try {
						cubeView.highlightFields(invalidFields);
					} catch (UnknownException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		
		final Action revert = new Action() {
			public void run() {
				try {
					blockGui();
					cubeView.setCube(backup);
					unblockGui();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		runOnUiThread(new Runnable() {
			public void run() {
				Useful.loadPreviousCube(getString(R.string.invalid_cube), CubeActivity.this, revert,
						repair);
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		cubeView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		cubeView.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.cube_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
        case R.id.scramble:
            scrambleCube();
            return true;
        case R.id.reset:
            resetCube();
            return true;
        case R.id.help:
    		Intent intent = new Intent(CubeActivity.this,
    				HelpActivity.class);
    		startActivity(intent);
            return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override  
	public void onBackPressed() {
		Useful.showQuestion(getString(R.string.return_to_main_menu), CubeActivity.this,
				new Action() {
					public void run() {
						finish();
					}
				});
	}

	protected boolean isFullscreenOpaque() {
		return true;
	}
	
	private void setMovingMode(boolean enabled) {
		cubeView.setMovesAllowed(enabled);
	}

}
