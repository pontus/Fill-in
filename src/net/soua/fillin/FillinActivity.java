package net.soua.fillin;

import android.graphics.Bitmap;
import android.app.Activity;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.drawable.*;
import android.graphics.Canvas;

public class FillinActivity extends Activity {
	/** Called when the activity is first created. */
	private Canvas c;

	FillableImageView i = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		
		
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			View v = findViewById(R.id.linearLayout1);
			v.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
		}


		i = new FillableImageView(getApplicationContext());

		// i.setAdjustViewBounds(true); // set the ImageView bounds to match the
		// Drawable's dimensions
		i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		LinearLayout l = (LinearLayout) findViewById(R.id.linearLayout1);

		l.removeAllViews();
		l.addView(i);

		// Drawable d = i.getDrawable();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		if (i != null)
			i.doFill((int) event.getX(), (int) event.getY());

		return true;
		// return super.onTouchEvent(event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		return true;
	}

	@Override
	public void onAttachedToWindow() {
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}

	public void onExitClick(View v) {
		finish();
	}
}