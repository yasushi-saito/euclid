package com.ysaito.euclid;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mModel = new CanvasModel();
        
        Point.Explicit p0 = new Point.Explicit(100, 100);
        mModel.addUserDefinedPoint(p0);
        Point.Explicit p1 = new Point.Explicit(110, 110);
        mModel.addUserDefinedPoint(p1);
        mModel.addShape(new Shape.Circle(p0, p1));

        Point.Explicit p2 = new Point.Explicit(50, 50);
        mModel.addUserDefinedPoint(p2);
        mModel.addShape(new Shape.Line(p0, p2));
        
        Point.Explicit p3 = new Point.Explicit(80, 80);
        mModel.addUserDefinedPoint(p3);
        mModel.addShape(new Shape.Line(p1, p3)); 
        CanvasView canvasView = (CanvasView)findViewById(R.id.canvasview);
        
        initializeModeButton(R.id.button_move, CanvasController.MOVE);
        initializeModeButton(R.id.button_line, CanvasController.DRAW_LINE);        
        initializeModeButton(R.id.button_circle, CanvasController.DRAW_CIRCLE);
        
        mController = new CanvasController(mModel, canvasView);
        canvasView.initialize(mController, mModel);
    }

    static final String TAG = "EuclidMain";
    
    class ModeButtonListener implements View.OnClickListener {
    	private final int mMode;
    	public ModeButtonListener(int mode) { mMode = mode; }
    	public void onClick(View v) {
    		mController.onModeChange(mMode);
    	}
    }
    private void initializeModeButton(int id, int mode) {
    	Button button = (Button)findViewById(id);
    	button.setOnClickListener(new ModeButtonListener(mode));
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    CanvasController mController;
    CanvasModel mModel;
}
