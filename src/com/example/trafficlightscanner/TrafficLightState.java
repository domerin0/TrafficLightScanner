package com.example.trafficlightscanner;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class TrafficLightState {
	
	private static final String TAG = "TrafficLightState";
	/*This class does the following to an hsv image
	 * 1. Checks if it contains the appropriate traffic light colours
	 * 2. Checks if each coloured image contains small circles */
	public Mat hsv;
	private Mat redMat;
	private Mat greenMat;
	private Mat yellowMat;
	
public TrafficLightState(Mat hsv)
{
	this.hsv = hsv;
	this.redMat = new Mat();
	this.greenMat = new Mat();
	this.yellowMat = new Mat();
	Core.inRange(hsv, new Scalar(0,0,0), new Scalar(0,0,255), redMat);
	Core.inRange(hsv, new Scalar(0,0,0), new Scalar(0,255,0), greenMat);
	Core.inRange(hsv, new Scalar(20, 100, 100), new Scalar(30, 255, 255), yellowMat);
}
	
public List<Mat> convertToGrayScale()
{
	//converts the 3 color images to grey scale
	List<Mat> hsvChannels = new ArrayList<Mat>();

		List<Mat> tempMat = new ArrayList<Mat>();
		Core.split(redMat, tempMat);
		hsvChannels.add(tempMat.get(0));
		Core.split(greenMat, tempMat);
		hsvChannels.add(tempMat.get(0));
		Core.split(yellowMat, tempMat);
		hsvChannels.add(tempMat.get(0));


	return hsvChannels;
}

public static List<Mat> applyGaussianBlur(List<Mat> hsvChannels)
{
	for (Mat channel : hsvChannels)
	{
		Imgproc.GaussianBlur(channel, channel, new Size(15,15),50);
	}
	return hsvChannels;
}

public static boolean containsCircles(Mat greyScale)
{
	Mat circles = new Mat();
	// circles in picture will be between 7 and 20 pixels in radius
	Imgproc.HoughCircles(greyScale, circles, Imgproc.CV_HOUGH_GRADIENT, 1, 14, 200, 100, 7, 20);
	if(circles.cols() > 0)
	{
		return true;
	}
	else
	{
		return false;
	}
}
	
}
