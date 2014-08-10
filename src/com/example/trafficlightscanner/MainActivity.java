package com.example.trafficlightscanner;

import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;


public class MainActivity extends Activity implements CvCameraViewListener2, OnTouchListener {

	public static final String TAG = "MainActivity";
	public boolean isTrafficLightThere = true;
    private Mat mRgba;
    private Mat	hsv;
    private TrafficLightState tfs;
	
	
    private CameraBridgeViewBase mOpenCvCameraView;

    
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this)
    {
    	public void onManagerConnected(int status)
    	{
    		switch(status)
    		{
    		case LoaderCallbackInterface.SUCCESS:
    		{
    			Log.i(TAG, "OpenCV loaded successfully");
                mOpenCvCameraView.enableView();
                mOpenCvCameraView.setOnTouchListener(MainActivity.this);
    		} break;
    		
    		default:
    		{
    			super.onManagerConnected(status);
    		}break;
    		}
    	}
    };
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.traffic_light_surfaceview);
        mOpenCvCameraView.setCvCameraViewListener(this);
        Log.i(TAG, getVideoResolution().get(0).height + "x" +getVideoResolution().get(0).width );
    }

    
    
    
    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);	
	}




	@Override
	public void onCameraViewStopped() {
		mRgba.release();		
	}




	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        hsv = convert2Hsv(mRgba);
        tfs = new TrafficLightState(hsv);
        List<Mat> blurredImages = TrafficLightState.applyGaussianBlur(tfs.convertToGrayScale());
        boolean[] lightState = new boolean[3];
        for (int i = 0; i < 3 ; i ++)
        {
        boolean state = TrafficLightState.containsCircles(blurredImages.get(i));
        lightState[i] = state;
        }
        Log.i(TAG, "Light state is :" + String.valueOf(lightState[0]) + ", " 
        + String.valueOf(lightState[1]) + ", "  + String.valueOf(lightState[2]));
        return blurredImages.get(0);

       
	}

    private Scalar convertScalarHsv2Rgba(Scalar hsvColor) {
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0, 0));
    }


    private Mat convert2Hsv(Mat rbga)
    {
    	Mat hsv = new Mat();
    	Imgproc.cvtColor(rbga, hsv, Imgproc.COLOR_RGB2HSV, 3);
    	return hsv;
    }

	public List<Camera.Size> getVideoResolution()
	{
	Camera camera = Camera.open();
	List<Camera.Size> cameraSizes = camera.getParameters().getSupportedVideoSizes();
	camera.release();
	return cameraSizes;
	}
	
}
