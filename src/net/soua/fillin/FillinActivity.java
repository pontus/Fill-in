package net.soua.fillin;

import android.graphics.Bitmap;
import android.app.Activity;
import android.view.*;
import android.widget.ImageView;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.drawable.*;
import android.graphics.Canvas;

public class FillinActivity extends Activity {
    /** Called when the activity is first created. */
    private Canvas c;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ImageView i = (ImageView) findViewById(R.id.imageView1);
        // Drawable d = i.getDrawable();
        
        
        
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
    
    public void onExitClick(View v)
    {
    	finish();
    }
}