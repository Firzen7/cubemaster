package info.firzen.cubemaster2.activities.help;

import info.firzen.cubemaster2.R;
import info.firzen.cubemaster2.other.ErrorsCatcher;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class HelpActivity extends Activity {
	private ImageView basics;
	private ImageView performance;
	private ImageView cheatsheet;
	private ImageView report;
	private ImageView about;

	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setTheme(android.R.style.Theme_Holo);

		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ErrorsCatcher(this));
		setContentView(R.layout.activity_help);
		
		createGui();
		addActions();
	}
	
	private void createGui() {
		basics = (ImageView) findViewById(R.id.basics);
		performance = (ImageView) findViewById(R.id.performance);
		cheatsheet = (ImageView) findViewById(R.id.cheatsheet);
		report = (ImageView) findViewById(R.id.report);
		about = (ImageView) findViewById(R.id.about);
	}
	
	private void addActions() {
		basics.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(HelpActivity.this,
						SolvingHelpActivity.class);
				startActivity(intent);
			}
		});
		performance.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(HelpActivity.this,
						UsageTipsActivity.class);
				startActivity(intent);
			}
		});
		cheatsheet.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(HelpActivity.this,
						CheatsheetActivity.class);
				startActivity(intent);
			}
		});
		report.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("plain/text");
				intent.putExtra(Intent.EXTRA_EMAIL,
						new String[] {getString(R.string.email)});
				intent.putExtra(Intent.EXTRA_SUBJECT, "Bug report");
				startActivity(Intent.createChooser(intent, ""));
			}
		});
		about.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(HelpActivity.this,
						AboutActivity.class);
				startActivity(intent);
			}
		});
	}
}
