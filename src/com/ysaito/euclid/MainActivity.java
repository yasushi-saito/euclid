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
        
        Point.UserDefined p0 = new Point.UserDefined(100, 100);
        mModel.addUserDefinedPoint(p0);
        Point.UserDefined p1 = new Point.UserDefined(110, 110);
        mModel.addUserDefinedPoint(p1);
        mModel.addShape(new Shape.Circle(p0, p1));

        Point.UserDefined p2 = new Point.UserDefined(50, 50);
        mModel.addUserDefinedPoint(p2);
        mModel.addShape(new Shape.Line(p0, p2));
        
        Point.UserDefined p3 = new Point.UserDefined(80, 80);
        mModel.addUserDefinedPoint(p3);
        mModel.addShape(new Shape.Line(p1, p3)); 
        CanvasView canvasView = (CanvasView)findViewById(R.id.canvasview);
        
        mController = new CanvasController(mModel, canvasView);
        canvasView.initialize(mController, mModel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    CanvasController mController;
    CanvasModel mModel;
}
