package info.firzen.cubemaster2.activities;

import info.firzen.cubemaster2.R;
import info.firzen.cubemaster2.cube.CubeColor;
import info.firzen.cubemaster2.gui.ColorPickerDialog;
import info.firzen.cubemaster2.other.ErrorsCatcher;
import info.firzen.cubemaster2.other.Useful;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class ColorsActivity extends Activity {

	private static final String ORANGE = "p_orange";
	private static final String RED = "p_red";
	private static final String BLUE = "p_blue";
	private static final String GREEN = "p_green";
	private static final String WHITE = "p_white";
	private static final String YELLOW = "p_yellow";
	
	private Button save;
	private Button cancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setTheme(android.R.style.Theme_Holo);
		
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ErrorsCatcher(this));
		setContentView(R.layout.activity_colors);
		
		createGui();
		loadColors();
		addActions();
	}
	
	private void createGui() {
		save = (Button) findViewById(R.id.colors_save);
		cancel = (Button) findViewById(R.id.colors_cancel);
	}
	
	private void addActions() {
		save.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				saveColors();
				finish();
			}
		});
		cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});
		
		addColorPickerListener(R.id.colors_front);
		addColorPickerListener(R.id.colors_back);
		addColorPickerListener(R.id.colors_left);
		addColorPickerListener(R.id.colors_right);
		addColorPickerListener(R.id.colors_up);
		addColorPickerListener(R.id.colors_down);
	}
	
	private void addColorPickerListener(int layoutId) {
		final LinearLayout l = (LinearLayout) findViewById(layoutId);
		final int background = ((ColorDrawable) l.getBackground()).getColor();
		final ColorPickerDialog picker = new ColorPickerDialog(
				ColorsActivity.this, "cc", background, background);

		picker.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				l.setBackgroundColor(picker.getCurrentColor());
			}
		});
		
		l.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				picker.show();
			}
		});
	}

	private void loadColors() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		LinearLayout l = (LinearLayout) findViewById(R.id.colors_front);
		l.setBackgroundColor(prefs.getInt(ORANGE,
				CubeColor.ORANGE.getAndroidColor()));
		l = (LinearLayout) findViewById(R.id.colors_back);
		l.setBackgroundColor(prefs.getInt(RED,
				CubeColor.RED.getAndroidColor()));
		l = (LinearLayout) findViewById(R.id.colors_left);
		l.setBackgroundColor(prefs.getInt(BLUE,
				CubeColor.BLUE.getAndroidColor()));
		l = (LinearLayout) findViewById(R.id.colors_right);
		l.setBackgroundColor(prefs.getInt(GREEN,
				CubeColor.GREEN.getAndroidColor()));
		l = (LinearLayout) findViewById(R.id.colors_up);
		l.setBackgroundColor(prefs.getInt(WHITE,
				CubeColor.WHITE.getAndroidColor()));
		l = (LinearLayout) findViewById(R.id.colors_down);
		l.setBackgroundColor(prefs.getInt(YELLOW,
				CubeColor.YELLOW.getAndroidColor()));
	}
	
	private void saveColors() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = prefs.edit();

		LinearLayout l = (LinearLayout) findViewById(R.id.colors_front);
		editor.putInt(ORANGE, ((ColorDrawable) l.getBackground()).getColor());
		l = (LinearLayout) findViewById(R.id.colors_back);
		editor.putInt(RED, ((ColorDrawable) l.getBackground()).getColor());		
		l = (LinearLayout) findViewById(R.id.colors_left);
		editor.putInt(BLUE, ((ColorDrawable) l.getBackground()).getColor());		
		l = (LinearLayout) findViewById(R.id.colors_right);
		editor.putInt(GREEN, ((ColorDrawable) l.getBackground()).getColor());		
		l = (LinearLayout) findViewById(R.id.colors_up);
		editor.putInt(WHITE, ((ColorDrawable) l.getBackground()).getColor());		
		l = (LinearLayout) findViewById(R.id.colors_down);
		editor.putInt(YELLOW, ((ColorDrawable) l.getBackground()).getColor());

		editor.apply();
		
		Useful.refreshBackendStickers();
	}
}
