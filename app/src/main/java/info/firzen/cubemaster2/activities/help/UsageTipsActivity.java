package info.firzen.cubemaster2.activities.help;

import info.firzen.cubemaster2.R;
import info.firzen.cubemaster2.other.ErrorsCatcher;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class UsageTipsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setTheme(android.R.style.Theme_Holo);

		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ErrorsCatcher(this));
		setContentView(R.layout.activity_usage_tips);
	}
}
