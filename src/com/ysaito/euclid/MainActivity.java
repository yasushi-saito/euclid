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
        CanvasView canvasView = (CanvasView)findViewById(R.id.canvasview);

        initializeModeButton(R.id.button_move, CanvasController.MOVE);
        initializeModeButton(R.id.button_line, CanvasController.DRAW_LINE);
        initializeModeButton(R.id.button_circle, CanvasController.DRAW_CIRCLE);

        mController = new CanvasController(mModel, canvasView);
        canvasView.initialize(mController, mModel);
    	Button button = (Button)findViewById(R.id.button_reset);
    	button.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
    			mController.onReset();
    		}
    	});
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
