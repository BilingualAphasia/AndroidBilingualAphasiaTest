package ca.ilanguage.oprime.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class TransparentPanel extends LinearLayout {
	private Paint	innerPaint, borderPaint ;
	private float scale = 1.0f;
	private Paint touchPaints[] = new Paint[MAX_TOUCHPOINTS];
	private static final int MAX_TOUCHPOINTS = 10;
	
	public TransparentPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public TransparentPanel(Context context) {
		super(context);
		init();
	}

	private void init() {
		innerPaint = new Paint();
		innerPaint.setARGB(225, 75, 75, 75); //gray
		innerPaint.setAntiAlias(true);

		borderPaint = new Paint();
		borderPaint.setARGB(255, 255, 255, 255);
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(2);
	}
	
	public void setInnerPaint(Paint innerPaint) {
		this.innerPaint = innerPaint;
	}

	public void setBorderPaint(Paint borderPaint) {
		this.borderPaint = borderPaint;
	}
	private void drawCircle(float x, float y, Paint paint, Canvas c) {
		c.drawCircle(x, y, 40 * scale, paint);
	}
    @Override
    protected void dispatchDraw(Canvas canvas) {
    	
    	
    	
    	RectF drawRect = new RectF();
    	drawRect.set(0,0, getMeasuredWidth(), getMeasuredHeight());
    	
    	canvas.drawRoundRect(drawRect, 5, 5, innerPaint);
		canvas.drawRoundRect(drawRect, 5, 5, borderPaint);
		
		touchPaints[0] = new Paint();
		touchPaints[0].setColor(Color.BLUE);
		float x =(float) 624.38;
		float y = (float) 210.63;
		drawCircle(x, y, touchPaints[0], canvas);
		
		super.dispatchDraw(canvas);
    }
}