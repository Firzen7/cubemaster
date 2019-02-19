package info.firzen.cubemaster2.gui;

import info.firzen.cubemaster2.R;
import info.firzen.cubemaster2.cube.CubeColor;
import info.firzen.cubemaster2.other.DataHolder;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

public class ColorChooser extends LinearLayout {
	private List<CubeFieldView> fields = new ArrayList<CubeFieldView>();
	private final int selected = Color.rgb(150, 150, 150);
	private final int noColor = Color.argb(0, 0, 0, 0);
	
	public ColorChooser(Context context, AttributeSet attrs) {
		super(context, attrs);
        LayoutInflater mInflater = (LayoutInflater)context.getSystemService(
        		Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.color_chooser, this);
        
        createGui();
	}
	
	private CubeFieldView initFieldView(int id, final CubeColor color) {
		CubeFieldView v = (CubeFieldView) findViewById(id);
		v.setColor(color);

		v.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				for(CubeFieldView field : fields) {
					field.setBackgroundColor(noColor);
				}
				v.setBackgroundColor(selected);
				DataHolder.getInstance().setPickerColor(color);
			}
		});
		
		fields.add(v);
		
		return v;
	}
	
	public void refreshSelection() {
		CubeColor current = DataHolder.getInstance().getPickerColor();
		for(CubeFieldView field : fields) {
			if(current.equals(field.getStartColor())) {
				field.setBackgroundColor(selected);
			}
			else {
				field.setBackgroundColor(noColor);
			}
		}
	}
	
	private void createGui() {
		initFieldView(R.id.cubeFieldView1, CubeColor.ORANGE);
		initFieldView(R.id.cubeFieldView2, CubeColor.RED);
		initFieldView(R.id.cubeFieldView3, CubeColor.BLUE);
		initFieldView(R.id.cubeFieldView4, CubeColor.GREEN);
		initFieldView(R.id.cubeFieldView5, CubeColor.WHITE);
		initFieldView(R.id.cubeFieldView6, CubeColor.YELLOW);
		CubeFieldView f7 = initFieldView(R.id.cubeFieldView7, CubeColor.UNKNOWN);
		f7.setCircleShape(true);
		f7.setBackgroundColor(selected);
	}
}
