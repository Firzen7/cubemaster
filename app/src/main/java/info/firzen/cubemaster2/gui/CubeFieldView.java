package info.firzen.cubemaster2.gui;

import info.firzen.cubemaster2.R;
import info.firzen.cubemaster2.backend.cube.interfaces.IRgb;
import info.firzen.cubemaster2.cube.CubeColor;
import info.firzen.cubemaster2.other.Action;
import info.firzen.cubemaster2.other.DataHolder;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CubeFieldView extends View {
	
	private int xCoord;
	private int yCoord;
	private CubeColor startColor = null;
	private CubeColor colorCorrection = null;
	private Paint paint = new Paint();
	private RectF r = new RectF();
	
	private boolean circleShape = true;
	private boolean clickable = false;
	private Action onTouch;
	
	private Point center = new Point();
	private int circleSize = -1;
	private boolean drawCross = false;
	
	public CubeFieldView(Context context, AttributeSet attrs) {
		super(context, attrs);
		circleShape = false;
	}
	
	public CubeFieldView(Context context) {
		super(context);
		this.onTouch = null;
		paint.setStrokeWidth(3);
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setColor(Color.rgb(0, 0, 0));
		paint.setAntiAlias(true);
		initActions();
	}
	
	public CubeFieldView(Context context, Action onTouch) {
		super(context);
		this.onTouch = onTouch;
		paint.setStrokeWidth(3);
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setColor(Color.rgb(0, 0, 0));
		paint.setAntiAlias(true);
		initActions();
	}

	public CubeFieldView(Context context, Action onTouch,
			boolean clickable, boolean circleShape) {
		super(context);
		this.onTouch = onTouch;
		this.clickable = clickable;
		this.circleShape = circleShape;
		paint.setStrokeWidth(3);
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setColor(Color.rgb(0, 0, 0));
		paint.setAntiAlias(true);
		initActions();
	}
	
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if(circleShape) {
			if(drawCross) {
				int diffW = (int) (this.getWidth() * 0.3);
				int diffH = (int) (this.getHeight() * 0.3);

				paint.setColor(Color.rgb(255, 0, 0));
				canvas.drawLine(diffW, diffH, this.getWidth() - diffW,
						this.getHeight() - diffH, paint);
				canvas.drawLine(this.getWidth() - diffW, diffH, diffW,
						this.getHeight() - diffH, paint);
			}
			else {
				if(circleSize == -1) {
					int smaller = (int) ((double) Math.min(canvas.getWidth(),
							canvas.getHeight()) / 2.3);
					canvas.drawCircle(center.x, center.y, smaller, paint);
				}
				else {
					canvas.drawCircle(center.x, center.y, circleSize, paint);
				}
			}
		}
		else {
			int diffW = (int) (this.getWidth() * 0.08);
			int diffH = (int) (this.getHeight() * 0.08);
			
			r.set(diffW, diffH, this.getWidth() - diffW,
					this.getHeight() - diffH);
			canvas.drawRoundRect(r, 7f, 7f, paint);
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);			

		center.set(width / 2, height / 2);
	}
	
	private void initActions() {
		if(clickable) {
			OnClickListener listener = new OnClickListener() {
				public void onClick(View v) {
					correctColor();
				}
			};
			
			setOnClickListener(listener);
		}
	}
	
	public void setColor(CubeColor color)
	{
		startColor = color;
		
		if(CubeColor.UNKNOWN.equals(color)) {
			drawCross = true;
		}
		
		IRgb rgb = color.getColor();
		paint.setColor(Color.rgb(rgb.getRed(), rgb.getGreen(),
				rgb.getBlue()));
		this.invalidate();
	}

	public int getxCoord() {
		return xCoord;
	}

	public void setxCoord(int xCoord) {
		this.xCoord = xCoord;
	}

	public int getyCoord() {
		return yCoord;
	}

	public void setyCoord(int yCoord) {
		this.yCoord = yCoord;
	}
	
	private void correctColor() {
		final CharSequence[] items = {"Červená", "Oranžová", "Žlutá", "Zelená",
				"Modrá", "Bílá"};

		AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
		builder.setTitle(R.string.choose_load_type);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				switch(item) {
					case 0: setColor(CubeColor.RED);
							setColorCorrection(CubeColor.RED);
							runOnTouchAction();
							break;
					case 1: setColor(CubeColor.ORANGE);
							setColorCorrection(CubeColor.ORANGE);
							runOnTouchAction();
							break;
					case 2: setColor(CubeColor.YELLOW);
							setColorCorrection(CubeColor.YELLOW);
							runOnTouchAction();
							break;
					case 3: setColor(CubeColor.GREEN);
							setColorCorrection(CubeColor.GREEN);
							runOnTouchAction();
							break;
					case 4: setColor(CubeColor.BLUE);
							setColorCorrection(CubeColor.BLUE);
							runOnTouchAction();
							break;
					case 5: setColor(CubeColor.WHITE);
							setColorCorrection(CubeColor.WHITE);
							runOnTouchAction();
							break;
					default:
						break;
				}
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	private void runOnTouchAction() {
		DataHolder.getInstance().setPickerColor(getColorCorrection());
		if(onTouch != null) {
			onTouch.run();
		}
	}

	public CubeColor getColorCorrection() {
		return colorCorrection;
	}

	public void setColorCorrection(CubeColor colorCorrection) {
		this.colorCorrection = colorCorrection;
	}
	
	public boolean isCorrected() {
		return colorCorrection != null;
	}

	public CubeColor getStartColor() {
		return startColor;
	}

	public void setStartColor(CubeColor startColor) {
		this.startColor = startColor;
	}

	public int getCircleSize() {
		return circleSize;
	}

	public void setCircleSize(int circleSize) {
		this.circleSize = circleSize;
	}
	
	public void setCircleShape(boolean state) {
		circleShape = state;
		invalidate();
	}
}
