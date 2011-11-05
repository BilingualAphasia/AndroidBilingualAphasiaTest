package ca.ilanguage.oprime.ui;


import java.io.BufferedInputStream;
import java.io.FileInputStream;



import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import ca.ilanguage.oprime.R;

public class TranslucentUserDrawActivity extends Activity {
    
    private Paint       mPaint;
    private MaskFilter  mEmboss;
    private MaskFilter  mBlur;
    
    public void colorChanged(int color) {
        mPaint.setColor(color);
    }

    public class MyView extends View {
        
        private static final float MINP = 0.25f;
        private static final float MAXP = 0.75f;
        
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        
        public MyView(Context c) {
            super(c);
            
            mBitmap = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
        }
        
        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawColor(0xFFAAAAAA);
            
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            
            canvas.drawPath(mPath, mPaint);
        }
        
        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;
        
        private void touch_start(float x, float y) {
            //mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }
        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            //mPath.reset();
        }
        
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }
    
    private static final int COLOR_MENU_ID = Menu.FIRST;
    private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
    private static final int BLUR_MENU_ID = Menu.FIRST + 2;
    private static final int ERASE_MENU_ID = Menu.FIRST + 3;
    private static final int SRCATOP_MENU_ID = Menu.FIRST + 4;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        
        menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c');
        menu.add(0, EMBOSS_MENU_ID, 0, "Emboss").setShortcut('4', 's');
        menu.add(0, BLUR_MENU_ID, 0, "Blur").setShortcut('5', 'z');
        menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z');
        menu.add(0, SRCATOP_MENU_ID, 0, "SrcATop").setShortcut('5', 'z');

        /****   Is this the mechanism to extend with filter effects?
        Intent intent = new Intent(null, getIntent().getData());
        intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
        menu.addIntentOptions(
                              Menu.ALTERNATIVE, 0,
                              new ComponentName(this, NotesList.class),
                              null, intent, 0, null);
        *****/
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }
 /**   
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xFF);

        switch (item.getItemId()) {
            case COLOR_MENU_ID:
                new ColorPickerDialog(this, this, mPaint.getColor()).show();
                return true;
            case EMBOSS_MENU_ID:
                if (mPaint.getMaskFilter() != mEmboss) {
                    mPaint.setMaskFilter(mEmboss);
                } else {
                    mPaint.setMaskFilter(null);
                }
                return true;
            case BLUR_MENU_ID:
                if (mPaint.getMaskFilter() != mBlur) {
                    mPaint.setMaskFilter(mBlur);
                } else {
                    mPaint.setMaskFilter(null);
                }
                return true;
            case ERASE_MENU_ID:
                mPaint.setXfermode(new PorterDuffXfermode(
                                                        PorterDuff.Mode.CLEAR));
                return true;
            case SRCATOP_MENU_ID:
                mPaint.setXfermode(new PorterDuffXfermode(
                                                    PorterDuff.Mode.SRC_ATOP));
                mPaint.setAlpha(0x80);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/
	
	@Override
	public void setContentView(View view) {
        if (false) { // set to true to test Picture
            ViewGroup vg = new PictureLayout(this);
            vg.addView(view);
            view = vg;
        }
        
        super.setContentView(view);
    }
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new MyView(this));

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);
        
        mEmboss = new EmbossMaskFilter(new float[] { 1, 1, 1 },
                                       0.4f, 6, 3.5f);

        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
    }
	
//	protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
        //setContentView(R.layout.translucent_background);
        
//        ImageView mImage = (ImageView) findViewById(R.id.mainimage);
//        String mImageFile ="/sdcard/OPrime/BilingualAphasiaTest/images/BAT-E1.png";    
//
//	    FileInputStream in;
//        BufferedInputStream buf;
//        try {
//       	    in = new FileInputStream(mImageFile);
//            buf = new BufferedInputStream(in);
//            Bitmap bMap = BitmapFactory.decodeStream(buf);
//            mImage.setImageBitmap(bMap);
//            if (in != null) {
//         	in.close();
//            }
//            if (buf != null) {
//         	buf.close();
//            }
//        } catch (Exception e) {
//            Log.e("Error reading image file", e.toString());
//        }
//        
//        Button button = (Button) findViewById(R.id.button_click_me);
//        button.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View view) {
//				Toast.makeText(TranslucentUserDrawActivity.this, "Button Clicked",Toast.LENGTH_SHORT).show();
//        }});
	//}
//
//        // Create our Preview view and set it as the content of our
//        // Activity
//        mGLSurfaceView = new GLSurfaceView(this);
//        // We want an 8888 pixel format because that's required for
//        // a translucent window.
//        // And we want a depth buffer.
//        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
//        // Tell the cube renderer that we want to render a translucent version
//        // of the cube:
//        //mGLSurfaceView.setRenderer(new CubeRenderer(true));
//        //for a reactive application
//        //GLSurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);
//        
//        // Use a surface format with an Alpha channel:
//        mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
//        setContentView(mGLSurfaceView);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mGLSurfaceView.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mGLSurfaceView.onPause();
//    }
//
//    private GLSurfaceView mGLSurfaceView;
	public class PictureLayout extends ViewGroup {
	    private final Picture mPicture = new Picture();

	    public PictureLayout(Context context) {
	        super(context);
	    }

	    public PictureLayout(Context context, AttributeSet attrs) {
	        super(context, attrs);
	    }    

	    @Override
	    public void addView(View child) {
	        if (getChildCount() > 1) {
	            throw new IllegalStateException("PictureLayout can host only one direct child");
	        }

	        super.addView(child);
	    }

	    @Override
	    public void addView(View child, int index) {
	        if (getChildCount() > 1) {
	            throw new IllegalStateException("PictureLayout can host only one direct child");
	        }

	        super.addView(child, index);
	    }

	    @Override
	    public void addView(View child, LayoutParams params) {
	        if (getChildCount() > 1) {
	            throw new IllegalStateException("PictureLayout can host only one direct child");
	        }

	        super.addView(child, params);
	    }

	    @Override
	    public void addView(View child, int index, LayoutParams params) {
	        if (getChildCount() > 1) {
	            throw new IllegalStateException("PictureLayout can host only one direct child");
	        }

	        super.addView(child, index, params);
	    }

	    @Override
	    protected LayoutParams generateDefaultLayoutParams() {
	        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    }

	    @Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	        final int count = getChildCount();

	        int maxHeight = 0;
	        int maxWidth = 0;

	        for (int i = 0; i < count; i++) {
	            final View child = getChildAt(i);
	            if (child.getVisibility() != GONE) {
	                measureChild(child, widthMeasureSpec, heightMeasureSpec);
	            }
	        }

	        maxWidth += getPaddingLeft() + getPaddingRight();
	        maxHeight += getPaddingTop() + getPaddingBottom();

	        Drawable drawable = getBackground();
	        if (drawable != null) {
	            maxHeight = Math.max(maxHeight, drawable.getMinimumHeight());
	            maxWidth = Math.max(maxWidth, drawable.getMinimumWidth());
	        }

	        setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec),
	                resolveSize(maxHeight, heightMeasureSpec));
	    }
	    
	    private void drawPict(Canvas canvas, int x, int y, int w, int h,
	                          float sx, float sy) {
	        canvas.save();
	        canvas.translate(x, y);
	        canvas.clipRect(0, 0, w, h);
	        canvas.scale(0.5f, 0.5f);
	        canvas.scale(sx, sy, w, h);
	        
	        String mImageFile ="/sdcard/OPrime/BilingualAphasiaTest/images/BAT-E1.png";    
	        FileInputStream in;
	        BufferedInputStream buf;
	        try {
	       	    in = new FileInputStream(mImageFile);
	            buf = new BufferedInputStream(in);
	            Bitmap bMap = BitmapFactory.decodeStream(buf);
	            canvas.setBitmap(bMap);
	            if (in != null) {
	         	in.close();
	            }
	            if (buf != null) {
	         	buf.close();
	            }
	        } catch (Exception e) {
	            Log.e("Error reading image file", e.toString());
	            Toast.makeText(TranslucentUserDrawActivity.this, "Error reading image file "+mImageFile, Toast.LENGTH_SHORT).show();
	        }
	        

	        canvas.drawPicture(mPicture);
	        canvas.restore();
	    }

	    @Override
	    protected void dispatchDraw(Canvas canvas) {
	        super.dispatchDraw(mPicture.beginRecording(getWidth(), getHeight()));
	        mPicture.endRecording();
	        
	        int x = getWidth()/2;
	        int y = getHeight()/2;
	        
	        if (false) {
	            canvas.drawPicture(mPicture);
	        } else {
	            drawPict(canvas, 0, 0, x, y,  1,  1);
	            drawPict(canvas, x, 0, x, y, -1,  1);
	            drawPict(canvas, 0, y, x, y,  1, -1);
	            drawPict(canvas, x, y, x, y, -1, -1);
	        }
	    }

	    @Override
	    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
	        location[0] = getLeft();
	        location[1] = getTop();
	        dirty.set(0, 0, getWidth(), getHeight());
	        return getParent();
	    }

	    @Override
	    protected void onLayout(boolean changed, int l, int t, int r, int b) {
	        final int count = super.getChildCount();

	        for (int i = 0; i < count; i++) {
	            final View child = getChildAt(i);
	            if (child.getVisibility() != GONE) {
	                final int childLeft = getPaddingLeft();
	                final int childTop = getPaddingTop();
	                child.layout(childLeft, childTop,
	                        childLeft + child.getMeasuredWidth(),
	                        childTop + child.getMeasuredHeight());

	            }
	        }
	    }
	}
/*
	public class ColorPickerDialog extends Dialog {

	    public interface OnColorChangedListener {
	        void colorChanged(int color);
	    }

	    private OnColorChangedListener mListener;
	    private int mInitialColor;

	    private static class ColorPickerView extends View {
	        private Paint mPaint;
	        private Paint mCenterPaint;
	        private final int[] mColors;
	        private OnColorChangedListener mListener;
	        
	        ColorPickerView(Context c, OnColorChangedListener l, int color) {
	            super(c);
	            mListener = l;
	            mColors = new int[] {
	                0xFFFF0000, 0xFFFF00FF, 0xFF0000FF, 0xFF00FFFF, 0xFF00FF00,
	                0xFFFFFF00, 0xFFFF0000
	            };
	            Shader s = new SweepGradient(0, 0, mColors, null);
	            
	            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	            mPaint.setShader(s);
	            mPaint.setStyle(Paint.Style.STROKE);
	            mPaint.setStrokeWidth(32);
	            
	            mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	            mCenterPaint.setColor(color);
	            mCenterPaint.setStrokeWidth(5);
	        }
	        
	        private boolean mTrackingCenter;
	        private boolean mHighlightCenter;

	        @Override 
	        protected void onDraw(Canvas canvas) {
	            float r = CENTER_X - mPaint.getStrokeWidth()*0.5f;
	            
	            canvas.translate(CENTER_X, CENTER_X);
	            
	            canvas.drawOval(new RectF(-r, -r, r, r), mPaint);            
	            canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint);
	            
	            if (mTrackingCenter) {
	                int c = mCenterPaint.getColor();
	                mCenterPaint.setStyle(Paint.Style.STROKE);
	                
	                if (mHighlightCenter) {
	                    mCenterPaint.setAlpha(0xFF);
	                } else {
	                    mCenterPaint.setAlpha(0x80);
	                }
	                canvas.drawCircle(0, 0,
	                                  CENTER_RADIUS + mCenterPaint.getStrokeWidth(),
	                                  mCenterPaint);
	                
	                mCenterPaint.setStyle(Paint.Style.FILL);
	                mCenterPaint.setColor(c);
	            }
	        }
	        
	        @Override
	        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	            setMeasuredDimension(CENTER_X*2, CENTER_Y*2);
	        }
	        
	        private static final int CENTER_X = 100;
	        private static final int CENTER_Y = 100;
	        private static final int CENTER_RADIUS = 32;

	        private int floatToByte(float x) {
	            int n = java.lang.Math.round(x);
	            return n;
	        }
	        private int pinToByte(int n) {
	            if (n < 0) {
	                n = 0;
	            } else if (n > 255) {
	                n = 255;
	            }
	            return n;
	        }
	        
	        private int ave(int s, int d, float p) {
	            return s + java.lang.Math.round(p * (d - s));
	        }
	        
	        private int interpColor(int colors[], float unit) {
	            if (unit <= 0) {
	                return colors[0];
	            }
	            if (unit >= 1) {
	                return colors[colors.length - 1];
	            }
	            
	            float p = unit * (colors.length - 1);
	            int i = (int)p;
	            p -= i;

	            // now p is just the fractional part [0...1) and i is the index
	            int c0 = colors[i];
	            int c1 = colors[i+1];
	            int a = ave(Color.alpha(c0), Color.alpha(c1), p);
	            int r = ave(Color.red(c0), Color.red(c1), p);
	            int g = ave(Color.green(c0), Color.green(c1), p);
	            int b = ave(Color.blue(c0), Color.blue(c1), p);
	            
	            return Color.argb(a, r, g, b);
	        }
	        
	        private int rotateColor(int color, float rad) {
	            float deg = rad * 180 / 3.1415927f;
	            int r = Color.red(color);
	            int g = Color.green(color);
	            int b = Color.blue(color);
	            
	            ColorMatrix cm = new ColorMatrix();
	            ColorMatrix tmp = new ColorMatrix();

	            cm.setRGB2YUV();
	            tmp.setRotate(0, deg);
	            cm.postConcat(tmp);
	            tmp.setYUV2RGB();
	            cm.postConcat(tmp);
	            
	            final float[] a = cm.getArray();

	            int ir = floatToByte(a[0] * r +  a[1] * g +  a[2] * b);
	            int ig = floatToByte(a[5] * r +  a[6] * g +  a[7] * b);
	            int ib = floatToByte(a[10] * r + a[11] * g + a[12] * b);
	            
	            return Color.argb(Color.alpha(color), pinToByte(ir),
	                              pinToByte(ig), pinToByte(ib));
	        }
	        
	        private static final float PI = 3.1415926f;

	        @Override
	        public boolean onTouchEvent(MotionEvent event) {
	            float x = event.getX() - CENTER_X;
	            float y = event.getY() - CENTER_Y;
	            boolean inCenter = java.lang.Math.sqrt(x*x + y*y) <= CENTER_RADIUS;
	            
	            switch (event.getAction()) {
	                case MotionEvent.ACTION_DOWN:
	                    mTrackingCenter = inCenter;
	                    if (inCenter) {
	                        mHighlightCenter = true;
	                        invalidate();
	                        break;
	                    }
	                case MotionEvent.ACTION_MOVE:
	                    if (mTrackingCenter) {
	                        if (mHighlightCenter != inCenter) {
	                            mHighlightCenter = inCenter;
	                            invalidate();
	                        }
	                    } else {
	                        float angle = (float)java.lang.Math.atan2(y, x);
	                        // need to turn angle [-PI ... PI] into unit [0....1]
	                        float unit = angle/(2*PI);
	                        if (unit < 0) {
	                            unit += 1;
	                        }
	                        mCenterPaint.setColor(interpColor(mColors, unit));
	                        invalidate();
	                    }
	                    break;
	                case MotionEvent.ACTION_UP:
	                    if (mTrackingCenter) {
	                        if (inCenter) {
	                            mListener.colorChanged(mCenterPaint.getColor());
	                        }
	                        mTrackingCenter = false;    // so we draw w/o halo
	                        invalidate();
	                    }
	                    break;
	            }
	            return true;
	        }
	    }

	    public ColorPickerDialog(Context context,
	                             OnColorChangedListener listener,
	                             int initialColor) {
	        super(context);
	        
	        mListener = listener;
	        mInitialColor = initialColor;
	    }

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        OnColorChangedListener l = new OnColorChangedListener() {
	            public void colorChanged(int color) {
	                mListener.colorChanged(color);
	                dismiss();
	            }
	        };

	        setContentView(new ColorPickerView(getContext(), l, mInitialColor));
	        setTitle("Pick a Color");
	    }
	}
*/
}
