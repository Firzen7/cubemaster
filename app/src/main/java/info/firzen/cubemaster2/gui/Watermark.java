package info.firzen.cubemaster2.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.View;

public class Watermark extends View {

	private RectF rectangle = new RectF();
	private Paint paint = new Paint();
	
	private Point center = new Point();
	private Point first = new Point();
	private Point second = new Point();
	private double smallerSize = 0;

	public Watermark(Context context) {
		super(context);
		paint.setAntiAlias(true);
		paint.setColor(Color.rgb(100, 255, 100));
		paint.setAlpha(160);
		paint.setStyle(Style.STROKE);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawRoundRect(rectangle, 15.0f, 15.0f, paint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		smallerSize = Math.min(this.getWidth(), this.getHeight());
		
		paint.setStrokeWidth((float) smallerSize / 50.0f);
		
		center.set(this.getWidth() / 2, this.getHeight() / 2);
		
		first.set((int)(center.x - (smallerSize / 2.5)),
				(int)(center.y - (smallerSize / 2.5)));
		
		second.set((int)(center.x + (smallerSize / 2.5)),
				(int)(center.y + (smallerSize / 2.5)));
		
		rectangle.set(first.x, first.y, second.x, second.y);
	}

	public Point getCenter() {
		return center;
	}

	public Point getFirst() {
		return first;
	}

	public Point getSecond() {
		return second;
	}
}
