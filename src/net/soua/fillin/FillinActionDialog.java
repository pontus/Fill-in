package net.soua.fillin;

import android.app.DialogFragment;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;




public class FillinActionDialog extends DialogFragment {

    public static FillinActionDialog newInstance(int title) {
        FillinActionDialog frag = new FillinActionDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }
    
  
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);
        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog, container, false);
  //      View tv = v.findViewById(R.id.text);
   //     ((TextView)tv).setText("Dialog #" + 10 + ": using style "
    //            );

        // Watch for button clicks.
        //ImageButton button = (ImageLiButton)v.findViewById(R.id.imageButton1);
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.share);
        
        
        layout.setOnClickListener( new OnClickListener()  {
            public void onClick(View v) {
            	
            	Log.e("fd","Share intent");
            	Intent intent=new Intent(android.content.Intent.ACTION_SEND);
            	intent.setType("image/png");
            	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

            	// Add data to the intent, the receiving app will decide what to do with it.
            	intent.putExtra(Intent.EXTRA_SUBJECT, "Some Subject Line");
            	intent.putExtra(Intent.EXTRA_TEXT, "Body of the message, woot!");
            	
            	
            	startActivity(Intent.createChooser(intent, "How do you want to share?"));
            	
                // When button is clicked, call up to owning activity.
                // (getActivity()).showDialog();
            }
            
        });
        

        return v;
    }
    
    }
    
