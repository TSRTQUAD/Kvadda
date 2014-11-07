package kvaddakopter.image_processing.utils;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class ImageConversion {

	/**
	 * Converts Image format from BufferedImage to Mat.
	 * 
	 * @param in - Input image of BufferedImage type
	 * @return  Output image of Mat type
	 */
	public static Mat img2Mat(BufferedImage in){
//		int type = in.getType();
		byte[] pixels = ((DataBufferByte) in.getRaster().getDataBuffer()).getData();
		Mat out = new Mat(in.getHeight(),in.getWidth(),CvType.CV_8UC3);
		out.put(0, 0, pixels);
		return out;

	}
	/**
	 * Converts Image format from Mat to BufferedImage.
	 * 
	 * @param in - Input image of Mat type
	 * @return  Output image of BufferedImage type
	 */
	public static BufferedImage mat2Img(Mat in)
	{
		BufferedImage out = null;
		
		MatOfByte mob = new MatOfByte();
		Highgui.imencode(".jpg", in, mob);
		byte[] byteBuffer = mob.toArray();
		
		 try {
		        InputStream inStream = new ByteArrayInputStream(byteBuffer);
		        out = ImageIO.read(inStream);
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		
		return out;
	} 
	/**
	 * Converts an image into gray scale.
	 * @param in Color image
	 * @return Gray scale image
	 */
	public static Mat toGrey(Mat in){
		Mat grayImg = new Mat(in.size(),in.type()); 
		Imgproc.cvtColor(in, grayImg, Imgproc.COLOR_RGB2GRAY);
		return grayImg;
	}

}
