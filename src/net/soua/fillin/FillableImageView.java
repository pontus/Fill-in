package net.soua.fillin;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.content.Context;

 class FillableImageView extends android.view.View {

	
    Bitmap b;
    
    public FillableImageView (Context c)
    {
    	super(c);
    	b =  Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
    }

	protected void onDraw(Canvas c) {
		c.drawBitmap(b,0,0,null);
	}
}
