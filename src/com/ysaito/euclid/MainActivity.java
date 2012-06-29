package com.ysaito.euclid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mModel = new CanvasModel();
        mModel.addShape(new Shape.Circle(100, 100, 50));
        mModel.addShape(new Shape.Line(10, 10, 40, 50));
        mModel.addShape(new Shape.Line(10, 10, 20, 50));
        CanvasView canvasView = (CanvasView)findViewById(R.id.canvasview);
        canvasView.initialize(mModel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    CanvasModel mModel;
}
