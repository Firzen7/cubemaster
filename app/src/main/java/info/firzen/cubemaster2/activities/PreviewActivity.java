package info.firzen.cubemaster2.activities;

import info.firzen.cubemaster2.R;
import info.firzen.cubemaster2.cube.CubeColor;
import info.firzen.cubemaster2.gui.CubeFieldView;
import info.firzen.cubemaster2.gui.CubeSideView;
import info.firzen.cubemaster2.other.Action;
import info.firzen.cubemaster2.other.Constants;
import info.firzen.cubemaster2.other.DataHolder;
import info.firzen.cubemaster2.other.ErrorsCatcher;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class PreviewActivity extends Activity {
	private static Action afterAction; 
	private CubeSideView side;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ErrorsCatcher(this));
		setContentView(R.layout.activity_preview);
		createGui();
		initActions();
	}
	
	private void initActions() {
		Button ok = (Button) this.findViewById(R.id.side_done);
		
		ok.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				sideDone();
			}
		});
	}
	
	private void createGui() {
		side = new CubeSideView(this, Constants.cubeSize, Constants.cubeSize);
		side.setColors(DataHolder.getInstance().getColors());
		FrameLayout watermark = (FrameLayout) findViewById(R.id.cubeFieldView1);
		watermark.addView(side);
	}
	
	private void sideDone() {
		List<CubeColor> colors = new ArrayList<CubeColor>();
		
		List<CubeFieldView> fields = side.getFields();
		for(CubeFieldView field : fields) {
			CubeColor color = null;
			if(field.isCorrected()) {
				color = field.getColorCorrection();
			}
			else {
				color = field.getStartColor();
			}
			
			if(color == null) {
				color = CubeColor.UNKNOWN;
			}
			
			colors.add(color);
		}
		
		DataHolder.getInstance().setColors(colors);

		if(afterAction != null) {
			afterAction.run();
		}
		this.finish();
	}
	
	public void setColors(List<CubeColor> colors) {
		side.setColors(colors);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	public static void setAfterAction(Action afterAction) {
		PreviewActivity.afterAction = afterAction;
	}
}
