package net.soua.fillin;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.graphics.Color;




class FillableImageView extends android.view.View {

	Bitmap b;

	public FillableImageView(Context c) {
		super(c);
		Bitmap sourceImage = null; //getImage();

		if (sourceImage == null)
		{
			
			sourceImage = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
			sourceImage.eraseColor(Color.MAGENTA);
		}
		b = Bitmap.createBitmap(sourceImage.getWidth(),
				sourceImage.getHeight(), Bitmap.Config.ARGB_8888);
		Paint p = new Paint();

		Canvas canv = new Canvas(b);
		Rect destRect = new Rect(0, 0, canv.getWidth(), canv.getHeight());
		canv.drawBitmap(sourceImage, null, destRect, null);

		fill(b, Color.BLUE, new Point(30, 30));
	}

	protected Bitmap getImage() {

		try {
			String url = "http://www.pppst.com/clipart.GIF";
			// String url =
			// "http://www.uu.se/digitalAssets/22/22975_UU_logga_transp.png";

			HttpClient c = new DefaultHttpClient();
			HttpResponse r = c.execute(new HttpGet(url));

			Log.e("got it", "Did query");
			InputStream i = r.getEntity().getContent();

			Log.e("got it", "Got stream");
			Bitmap b = new BitmapFactory().decodeStream(i);

			return b;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void doFill(int x, int y) {
		Point p = new Point(x, y);
		fill(b, Color.RED, p);
	}

	protected void onDraw(Canvas c) {

		Rect sourceRect = new Rect(0, b.getHeight(), 0, b.getWidth());
		Rect destRect = new Rect(0, 0, c.getWidth(), c.getHeight());

		Log.e("onDraw", "Bitmap size: " + b.getWidth() + "x" + b.getHeight());

		Log.e("onDraw", "Canvas size: " + c.getWidth() + "x" + c.getHeight());

		c.drawBitmap(b, null, destRect, null);
	}

	public static void fill(Bitmap b, int c, Point coord) {
		ArrayList<Long> p;

		int w = b.getWidth();
		int h = b.getHeight();

		p = new ArrayList<Long>();
		p.add((long) coord.x + w * coord.y);

		int oldColor = b.getPixel(coord.x, coord.y);

		if (c == oldColor) // Filling with the same color?
			return;

		int runs = 0;

		while (!p.isEmpty()) {

			runs++;

			Long where = p.get(0);

			p.remove(where);

			int x = (int) ((long) where) % w;
			int y = (int) ((long) where) / w;
			int startx = x;

			b.setPixel(x,y,c);
			
			for (int direction = -1; direction < 2; direction = direction + 2) {
				x = startx+direction;
				//Log.e("rita", "direction " + direction);

				while (x >= 0 && x < w && b.getPixel(x, y) == oldColor) {
					// Log.e("p",
					// "Color at " + x + "," + y + " before set: "
					// + b.getPixel(x, y));

					b.setPixel(x, y, c);

					// Log.e("p",
					// "Color at " + x + "," + y + " after set: "
					// + b.getPixel(x, y));

					if (y > 0 && oldColor == b.getPixel(x, y - 1)) {
						long above = where - w;

						if (!p.contains(above))
							p.add(above);
					}

					if (y < (h - 1) && oldColor == b.getPixel(x, y + 1)) {
						long below = where + w;

						if (!p.contains(below))
							p.add(below);

					}

					x = x + direction;
				}
			}
		}

		Log.e("f", "fill has gone " + runs + " runs");
	}
}
