package info.firzen.cubemaster2.activities;

import info.firzen.cubemaster2.R;
import info.firzen.cubemaster2.cube.CubeColor;
import info.firzen.cubemaster2.gui.CameraPreview;
import info.firzen.cubemaster2.gui.CubeSideView;
import info.firzen.cubemaster2.gui.Watermark;
import info.firzen.cubemaster2.other.Action;
import info.firzen.cubemaster2.other.Constants;
import info.firzen.cubemaster2.other.DataHolder;
import info.firzen.cubemaster2.other.ErrorsCatcher;
import info.firzen.cubemaster2.other.Useful;
import info.firzen.cubemaster2.recognition.Recognition;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

public class DetectActivity extends Activity {
	private Camera mCamera;
	private CameraPreview mPreview;
	private Watermark wmark;
	private FrameLayout preview;
	private Bitmap shoot = null;
	private List<CubeColor> colors;
	private static Action afterAction;
	
	private Button photoButton;
	private Button previewButton;
	
	private Toast feedback;
	private int tries = 0;

	private PictureCallback mPicture = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			mCamera.stopPreview();

			Bitmap output = BitmapFactory.decodeByteArray(data, 0, data.length);

			Matrix matrix = new Matrix();
			matrix.setRotate(90);
			// XXX
			matrix.postScale((float)0.1, (float)0.1);
			shoot = Bitmap.createBitmap(output, 0, 0,
					output.getWidth(), output.getHeight(),
					matrix, false);

			showPreview();

			mCamera.startPreview();
		}
	};
	
	// -------------------------------------------------------------------------

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ErrorsCatcher(this));
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_detect);

		createGui();
		reloadCameraSettings();
	}

	public void createGui() {
		setContentView(R.layout.activity_detect);

		photoButton = (Button) this.findViewById(R.id.side_done);
		previewButton = (Button) this.findViewById(R.id.button2);

		mCamera = getCameraInstance();
		mPreview = new CameraPreview(this, mCamera);
		preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);

		wmark = new Watermark(this);
		FrameLayout watermark = (FrameLayout) findViewById(R.id.watermark);
		watermark.addView(wmark);

		photoButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tries++;
				hideFeedback();
				photoButton.setEnabled(false);
				mCamera.takePicture(null, null, mPicture);
				photoButton.setEnabled(true);
				previewButton.setEnabled(true);
			}
		});

		previewButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				hideFeedback();
				DataHolder.getInstance().setColors(colors);
				
				if(afterAction != null) {
					afterAction.run();
				}
				
				photoButton.setEnabled(true);
				
				DetectActivity.this.finish();
			}
		});		
	}
	
	AlertDialog alert;
	private void selectWhiteBalance() {
		hideFeedback();
		
		final CharSequence[] items = {
				getString(R.string.wb_auto),
				getString(R.string.wb_tungsten),
				getString(R.string.wb_fluorescent),
				getString(R.string.wb_daylight),
				getString(R.string.wb_cloudy)};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setTitle(R.string.white_balance);
		builder.setSingleChoiceItems(items, Constants.currentWhiteBalance,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch(item) {
				case 0: Constants.whiteBalance = Camera.Parameters.WHITE_BALANCE_AUTO;
						Constants.currentWhiteBalance = 0; break;
				case 1: Constants.whiteBalance = Camera.Parameters.WHITE_BALANCE_INCANDESCENT;
						Constants.currentWhiteBalance = 1; break;
				case 2: Constants.whiteBalance = Camera.Parameters.WHITE_BALANCE_FLUORESCENT;
						Constants.currentWhiteBalance = 2; break;
				case 3: Constants.whiteBalance = Camera.Parameters.WHITE_BALANCE_DAYLIGHT;
						Constants.currentWhiteBalance = 3; break;
				case 4: Constants.whiteBalance = Camera.Parameters.WHITE_BALANCE_CLOUDY_DAYLIGHT;
						Constants.currentWhiteBalance = 4; break;
				default: break;
				}
				
				alert.dismiss();
				reloadCameraSettings();
			}
		});
		
		alert = builder.create();
		alert.show();
	}
	
	private void reloadCameraSettings() {
		if(mCamera != null) {
			Camera.Parameters p = mCamera.getParameters();
			p.setWhiteBalance(Constants.whiteBalance);
			mCamera.setParameters(p);
		}
	}
	
	public List<CubeColor> getColors() {
		return colors;
	}

	private void showPreview() {
		if(shoot != null) {
			Recognition r = new Recognition();
			
			int width = shoot.getWidth();
			int height = shoot.getHeight();
			
			int smallerSize = Math.min(width, height);
			int centerX = width / 2;
			int centerY = height / 2;

			Bitmap b = r.cutImage(shoot,
					((int)(centerX - (smallerSize / 2.5))),
					(int)(centerY - (smallerSize / 2.5)),
					(int)(2 * (smallerSize / 2.5)),
					(int)(2 * (smallerSize / 2.5)));

			colors = r.detectCubeSide(b);
			
			CubeSideView sideView = new CubeSideView(DetectActivity.this,
					Constants.cubeSize, Constants.cubeSize);
			sideView.setColors(colors);

			feedback = new Toast(getApplicationContext());
			feedback.setGravity(Gravity.CENTER, 0, 0);
			feedback.setDuration(Toast.LENGTH_LONG);
			feedback.setView(sideView);
			feedback.show();
			
			if(tries >= 5) {
				Useful.showToast(getString(R.string.suggest_white_balance), DetectActivity.this);
			}
		}
		else {
			Useful.showError(getString(R.string.error_damaged_photo), DetectActivity.this);
		}
	}
	
	private void hideFeedback() {
		if(feedback != null) {
			feedback.cancel();
		}
	}

	private Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open();
			c.setDisplayOrientation(90);
			//CameraManager man = new CameraManager();
		}
		catch (Exception e) {
			Useful.showError(getString(R.string.error_camera_init), DetectActivity.this);
		}
		return c;
	}

	protected void onPause() {
		releaseCamera();
		finish();
		super.onPause();
	}
	
	protected void onResume() {
		if(mCamera == null) {
			mCamera = getCameraInstance();
			if(mCamera != null) {
	            mCamera.setPreviewCallback(null);      
	            mPreview = new CameraPreview(this, mCamera);      
	            preview.addView(mPreview);     
	            mCamera.startPreview();
			}
			else {
				Useful.showError(getString(R.string.error_camera_init), DetectActivity.this);
			}
		}
		super.onResume();
	}

	private void releaseCamera(){
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.detect_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.set_white_balance:
			selectWhiteBalance();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static void setAfterAction(Action afterAction) {
		DetectActivity.afterAction = afterAction;
	}
}
