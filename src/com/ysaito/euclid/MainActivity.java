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

        ExplicitPoint p0 = new ExplicitPoint(100, 100);
        mModel.addShape(p0);
        ExplicitPoint p1 = new ExplicitPoint(110, 110);
        mModel.addShape(p1);
        mModel.addShape(new Circle(p0, p1));

        ExplicitPoint p2 = new ExplicitPoint(50, 50);
        mModel.addShape(p2);
        mModel.addShape(new Line(p0, p2));

        ExplicitPoint p3 = new ExplicitPoint(80, 80);
        mModel.addShape(p3);
        mModel.addShape(new Line(p1, p3));
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
