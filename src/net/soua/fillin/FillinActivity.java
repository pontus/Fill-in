package net.soua.fillin;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.graphics.Bitmap;
import android.graphics.drawable.*;
import android.graphics.Canvas;

public class FillinActivity extends Activity {
	/** Called when the activity is first created. */
	private Canvas c;
	private Handler mHandler = null;
	protected Activity t = this;

	static int requestCode = 0;
	FillableImageView i = null;

	int colors[] = { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW };
	int activeColor = 0;

	int imageHeight = 0;
	int imageWidth = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		mHandler = new Handler();
		mHandler.postDelayed(new NotificationCloser(), 10);

		Display display = getWindowManager().getDefaultDisplay();
		int height = display.getHeight();
		int width = display.getWidth();

		imageHeight = (int) (height * 0.8);
		imageWidth = (int) (width * 0.8);
		i = new FillableImageView(getApplicationContext());

		// i.setAdjustViewBounds(true); // set the ImageView bounds to match the
		// Drawable's dimensions

		LayoutParams params = new LayoutParams(imageWidth, imageHeight);

		i.setLayoutParams(params);

		LinearLayout imageLayout = (LinearLayout) findViewById(R.id.linearLayout1);

		imageLayout.setLayoutParams(new LinearLayout.LayoutParams(imageWidth,
				imageHeight));

		imageLayout.removeAllViews();
		imageLayout.addView(i);

		LinearLayout.LayoutParams bParams = new LinearLayout.LayoutParams(
				imageWidth / 4, imageHeight / 8);

		LinearLayout colorLayout = (LinearLayout) findViewById(R.id.colorLayout);
		colorLayout.setLayoutParams(new LinearLayout.LayoutParams(width,
				imageHeight / 8));

		Button b;
		LongClickListener lcListener = new LongClickListener();

		for (int i = 0; i < 4; i++) {
			int id = colorButtonId(i);

			Log.e("FD", "Fixing button for color " + i);
			b = (Button) findViewById(id);
			b.setLayoutParams(bParams);
			// b.setPadding(10, 10, 10, 10);
			b.setOnLongClickListener(lcListener);

			Log.e("FD", "Höjd: " + b.getHeight() + " " + imageHeight / 8);
			//b.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
		}

		fixColors();

		b = (Button) findViewById(R.id.functionButton);
		b.setLayoutParams(bParams);
		b.setOnLongClickListener(lcListener);
		b.setTextSize(10);

		Intent i = new Intent("net.soua.fillin.images");
		i.putExtra("action", "getrandomimage");

		startActivityForResult(i, requestCode);
	}

	protected void fixColors() {
		Button b;

		for (int i = 0; i < 4; i++) {
			int id = colorButtonId(i);
			b = (Button) findViewById(id);
			b.setBackgroundColor(colors[i]);

			if (i == activeColor) {
				b.setText(" \u03C0");

			} else
				b.setText("");
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("RES", "Resultat");
		if (requestCode == this.requestCode && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();

			Bitmap b = (Bitmap) extras.getParcelable("image");
			i.setImage(b);
			Log.e("FS", "Got image!");
		}
	}

	public void onResume() {

		super.onResume();
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	private boolean shown = false;

	private class NotificationCloser implements Runnable {

		public void run() {

			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
				// On newer devices, continually hide the status bar - causes
				// notification pane to go away.

				View v = findViewById(R.id.linearLayout1);
				v.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);

				Method expandStatusBar = null;
				Object statusBarManager = t.getSystemService("statusbar");
				Method[] methods = statusBarManager.getClass()
						.getDeclaredMethods();
				for (Method method : methods) {

					if (method.getName().compareTo("collapse") == 0)
						try {
							method.invoke(statusBarManager);
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
			} else {
				// requestWindowFeature(Window.FEATURE_NO_TITLE);
				getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

				getWindow()
						.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
				// Log.e("fdfds","fdsfd");
			}

			mHandler.postDelayed(this, 20); // Would be nice to have a handler
											// for this

		}
	}

	private class LongClickListener implements OnLongClickListener {
		public boolean onLongClick(View v) {
			int clickId = v.getId();

			if (clickId == R.id.functionButton) {

				Log.e("FUNCT", "Funciton button pressed.");

				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				Fragment prev = getFragmentManager()
						.findFragmentByTag("dialog");

				DialogFragment fillinActDialogFragment = FillinActionDialog
						.newInstance(1);
				fillinActDialogFragment.show(ft, "dialog");

				return true;
			}
			for (int i = 0; i < 4; i++) {
				if (clickId == colorButtonId(i)) {

					Log.e("L", "Long click color: " + v.getId() + " " + i);
					return true;

				}
			}

			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub

		if (event.getAction() != MotionEvent.ACTION_DOWN) {
			return false;
		}

		if (i != null) {

			double whereX = event.getX() / imageWidth;
			double whereY = event.getY() / imageWidth;

			new AsyncTask<Integer, Void, Void>() {
				protected Void doInBackground(Integer... f) {
					double whereX = f[0] / imageWidth;
					double whereY = f[1] / imageHeight;

					i.Fill(whereX, whereY, f[2]);
					return null;
				}

				protected void onPostExecute(Void f) {
					i.postInvalidate();
				}
			}.execute(new Integer((int) event.getX()),
					new Integer((int) event.getY()), new Integer(
							colors[activeColor]));
			// i.Fill(whereX, whereY, colors[activeColor]);
		}

		return true;

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// We eat this key

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

	public void onTemplateClick(View v) {
		startActivity(new Intent(FillinActivity.this,
				TemplateChooserActivity.class));
	}

	protected int colorButtonId(int button) {
		switch (button) {
		case 0:
			return R.id.colorButton1;
		case 1:
			return R.id.colorButton2;
		case 2:
			return R.id.colorButton3;
		case 3:
			return R.id.colorButton4;
		default:
			return -1;
		}
	}

	public void onColorButtonClick(View v) {
		int id = v.getId();

		for (int i = 0; i < 4; i++) {

			if (colorButtonId(i) == id) {
				activeColor = i;
			}
		}

		Log.e("COL", "Active : " + activeColor);
	}
}
