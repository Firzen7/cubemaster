package info.firzen.cubemaster2.gui;

import info.firzen.cubemaster2.cube.CubeColor;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.GridLayout;

public class CubeSideView extends GridLayout {
	private boolean readyToDraw = false;
	private List<CubeFieldView> fields = new ArrayList<CubeFieldView>();
	private List<GridLayout.LayoutParams> params
				= new ArrayList<GridLayout.LayoutParams>();
	
	private int columns = 0;
	private int rows = 0;
	
	public CubeSideView(Context context, int columns, int rows) {
		super(context);
		this.columns = columns;
		this.rows = rows;
		createGui();
	}

	private void createGui() {
		for(int i = 0; i < columns; i++) {
			for(int j = 0; j < rows; j++) {
				CubeFieldView field = new CubeFieldView(getContext());
				field.setxCoord(j);
				field.setyCoord(i);
				
				GridLayout.LayoutParams p = new GridLayout.LayoutParams();
				p.columnSpec = GridLayout.spec(field.getxCoord());
				p.rowSpec = GridLayout.spec(field.getyCoord());
		
				field.setLayoutParams(p);
				
				fields.add(field);
				params.add(p);
				this.addView(field);
			}
		}
		readyToDraw = true;
	}
	
	public void setColors(List<CubeColor> colors) {
		if(colors != null) {
			int size = colors.size();
			if(size == fields.size()) {
				for(int i = 0; i < size; i++) {
					fields.get(i).setColor(colors.get(i));
					fields.get(i).setStartColor(colors.get(i));
				}
			}
		}
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		if(readyToDraw) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = MeasureSpec.getSize(heightMeasureSpec);			

			int smallerSize = Math.min(width, height);

			width = (int)((float) smallerSize / 1.25f);
			height = (int)((float) smallerSize / 1.25f);

			this.setColumnCount(columns);
			this.setRowCount(rows);

			for(GridLayout.LayoutParams p : params) {
				p.width = width / columns;
				p.height = height / rows;
			}

			int smaller = columns;
			if(rows < columns) {
				smaller = rows;
			}
			for(CubeFieldView field : fields) {
				field.setCircleSize(width / smaller
						/ smaller / 3);
				field.invalidate();
			}
		}
	}

	public List<CubeFieldView> getFields() {
		return fields;
	}
}
