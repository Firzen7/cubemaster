package info.firzen.cubemaster2.activities;

import info.firzen.cubemaster2.R;
import info.firzen.cubemaster2.activities.help.HelpActivity;
import info.firzen.cubemaster2.other.Action;
import info.firzen.cubemaster2.other.Constants;
import info.firzen.cubemaster2.other.Useful;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends Activity {
	private Button solveByHandButton;
	private Button settingsButton;
	private Button helpButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		Constants.baseContext = getApplicationContext();
		Useful.refreshBackendStickers();

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		initActions();
		handleError();
	}
	
	private void handleError() {
		Bundle extras = getIntent().getExtras();
		
		if(extras != null) {
			String error = extras.getString("error");

			if(error != null && !error.isEmpty()) {
				Useful.showError(getString(R.string.error_critical_report) + "\n\n"
						+ error, MainActivity.this);
			}
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}
	
	private void initActions() {
		solveByHandButton = (Button) this.findViewById(R.id.button_solve_by_hand);
		settingsButton = (Button) this.findViewById(R.id.button_settings);
		helpButton = (Button) this.findViewById(R.id.button_help);
		
		solveByHandButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				selectCubeSize(new Action() {
					public void run() {
						showCubeActivity();
					}
				});
			}
		});

		settingsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showSettings();
			}
		});
		
		helpButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showHelp();
			}
		});
	}
	
	private void showHelp() {
		try {
			Intent intent = new Intent(MainActivity.this,
					HelpActivity.class);
			startActivity(intent);
		}
		catch (Exception e) {
			Useful.showError(getString(R.string.error_invoking_help), getApplicationContext(), e);
		}
	}
	
	private void showSettings() {
		Intent intent = new Intent(MainActivity.this,
				SettingsActivity.class);
		startActivity(intent);
	}
	
	private void showCubeActivity() {
		Intent intent = new Intent(MainActivity.this,
				CubeActivity.class);
		startActivity(intent);
	}
	
	private void selectCubeSize(final Action afterAction) {
		final CharSequence[] items = {
				getString(R.string.size_2x2x2),
				getString(R.string.size_3x3x3)};
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.select_size);
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	switch(item) {
			    	case 0: Constants.cubeSize = 2; break;
			    	case 1: Constants.cubeSize = 3; break;
			    	default: break;
		    	}
		    	afterAction.run();
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
