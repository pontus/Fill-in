package net.soua.fillin;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.graphics.Color;

class FillableImageView extends android.view.View {

	Bitmap b = null;
	View current = this;

	public FillableImageView(Context c, Bitmap sourceImage) {
		super(c);

		setImage(sourceImage);
	}

	public FillableImageView(Context c) {
		super(c);

	}

	public void setImage(Bitmap sourceImage) {
		if (sourceImage == null) {

			sourceImage = Bitmap
					.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
			sourceImage.eraseColor(Color.MAGENTA);
		}

		b = Bitmap.createBitmap(sourceImage.getWidth(),
				sourceImage.getHeight(), Bitmap.Config.ARGB_8888);


		Canvas canv = new Canvas(b);
		Rect destRect = new Rect(0, 0, canv.getWidth(), canv.getHeight());
		canv.drawBitmap(sourceImage, null, destRect, null);
		postInvalidate();
	}

	public void fill(int x, int y) {
		Log.e("MUS", "X: " + x + " Y: " + y);
		Point p = new Point(x, y);
		fill(b, Color.RED, p);
	}

	protected void onDraw(Canvas c) {

		if (b == null)
			return;

		Rect sourceRect = new Rect(0, 0, b.getWidth(), b.getHeight());
		// Rect destRect = new Rect(0, 0, c.getWidth(), c.getHeight());
		Rect destRect = new Rect(0, 0, this.getWidth(), this.getHeight());

		Log.e("onDraw", "Bitmap size: " + b.getWidth() + "x" + b.getHeight());

		Log.e("onDraw", "Canvas size: " + c.getWidth() + "x" + c.getHeight());

		c.drawBitmap(b, sourceRect, destRect, null);
	}

	private void Fill(Bitmap bmp, Point pt, int replacementColor) {
		// Inspired by
		// http://stackoverflow.com/questions/8070401/android-flood-fill-algorithm

		int targetColor = bmp.getPixel(pt.x, pt.y);
		Queue<Point> q = new LinkedList<Point>();
		q.add(pt);
		while (q.size() > 0) {
			Point n = q.poll();
			if (bmp.getPixel(n.x, n.y) != targetColor)
				continue;

			Point w = n, e = new Point(n.x + 1, n.y);
			while ((w.x >= 0) && (bmp.getPixel(w.x, w.y) == targetColor)) {
				bmp.setPixel(w.x, w.y, replacementColor);
				if ((w.y > 0) && (bmp.getPixel(w.x, w.y - 1) == targetColor))
					q.add(new Point(w.x, w.y - 1));
				if ((w.y < bmp.getHeight() - 1)
						&& (bmp.getPixel(w.x, w.y + 1) == targetColor))
					q.add(new Point(w.x, w.y + 1));
				w.x--;
			}
			while ((e.x < bmp.getWidth())
					&& (bmp.getPixel(e.x, e.y) == targetColor)) {
				bmp.setPixel(e.x, e.y, replacementColor);

				if ((e.y > 0) && (bmp.getPixel(e.x, e.y - 1) == targetColor))
					q.add(new Point(e.x, e.y - 1));
				if ((e.y < bmp.getHeight() - 1)
						&& (bmp.getPixel(e.x, e.y + 1) == targetColor))
					q.add(new Point(e.x, e.y + 1));
				e.x++;
			}
		}
	}

	public void Fill(double xPercent, double yPercent, int c) {
		Fill((int) (b.getWidth()*xPercent), (int) (b.getHeight()*yPercent), c);
	}
	
	public void Fill(int x, int y, int c) {
		if (b != null) {
			Fill(b, new Point(x, y), c);
		}
	}

	public void fill(Bitmap b, int c, Point coord) {
		ArrayList<Long> p;

		int w = b.getWidth();
		int h = b.getHeight();

		p = new ArrayList<Long>();
		p.add((long) coord.x + w * coord.y);

		if (w <= coord.x || h <= coord.y)
			return;

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

			// if (x == 275) {
			// Log.e("Y","X: "+x+" y: +y");
			// }
			b.setPixel(x, y, c);

			for (int direction = -1; direction < 2; direction = direction + 2) {
				x = startx + direction;

				boolean needCheckAbove = true;
				boolean needCheckBelow = true;

				while (x >= 0 && x < w && b.getPixel(x, y) == oldColor) {

					// if ( x == 275)
					// {
					// Log.e("F","X: "+x+"Y: "+(y+1)+" c: "+
					// b.getPixel(x,y+1)+" oldcolor: "+oldColor);
					// }

					b.setPixel(x, y, c);

					if (needCheckAbove) {
						if (y > 0 && oldColor == b.getPixel(x, y - 1)) {
							long above = x + ((y - 1) * w);
							needCheckAbove = false;

							if (!p.contains(above))
								p.add(above);
						} else {
							needCheckAbove = true;
						}
					}

					if (needCheckBelow) {

						// if (x == 275 && y == 11) {
						// Log.e("f","HEj1!");
						// }
						if (y < (h - 1) && oldColor == b.getPixel(x, y + 1)) {
							long below = x + ((y + 1) * w);
							needCheckBelow = false;

							// if (x == 275 && y == 11) {
							// Log.e("f","HEj!");
							// }
							if (!p.contains(below)) {
								p.add(below);
								// if (x == 275)
								// Log.e("FT","HEJ3! "+below);
							}

						} else {
							needCheckBelow = true;

						}
					}

					x = x + direction;
				}
			}
		}

		Log.e("f", "fill has gone " + runs + " runs");
		this.postInvalidate();

	}

}
