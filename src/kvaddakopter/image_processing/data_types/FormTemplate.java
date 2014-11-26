package kvaddakopter.image_processing.data_types;

import org.opencv.core.Mat;

public class FormTemplate {
	
	ImageObject mImageObject;
	boolean isCalibrated = false;
	int mId =-1;
	double mBoxWidth = 0.5;
	double mBoxHeight = 0.5;
	double mBoxOffsetX = 0.5;
	double mBoxOffsetY = 0.5;
	String description;
	private boolean isActive;
		
	
	public void setBoxWitdh(double newWitdh){
		mBoxWidth = newWitdh;
	}
	public void setBoxHeight(double newHeight){
		mBoxHeight= newHeight;
	}
	public void setBoxOffsetX(double newOffsetX){
		mBoxOffsetX= newOffsetX;
	}
	
	public void setBoxOffsetY(double newOffsetY){
		mBoxOffsetY= newOffsetY;
	}
	
	public void setCalibrated(boolean b){
		isCalibrated = b;
	}
	
	public boolean hasBeenCalibrated(){
		return isCalibrated;
	}
	
	public double getBoxWitdh(){
		return mBoxWidth;
	}
	public double getBoxHeight(){
		return mBoxWidth;
	}
	public double getBoxOffsetX(){
		return mBoxOffsetX;
	}
	
	public double getBoxOffsetY(){
		return mBoxOffsetY;
	}
	
	public void setTemplateImage(Mat image){
		mImageObject = new ImageObject(image);
	}
	
	public ImageObject getImageObject(){
		return mImageObject;
	}
	public double[] getBoxCenter() {
		return new double[]{mBoxOffsetX,mBoxOffsetY};
	}
	
	
	public double[] getScaledBoxSize(double imageWidth, double imageHeight) {
		return new double[]{mBoxWidth*imageWidth,mBoxHeight*imageHeight};
	}
	public double[] getScaledBoxCenter(double imageWidth, double imageHeight) {
		return new double[]{mBoxOffsetX*imageWidth,mBoxOffsetY*imageHeight};
	}
	public void setId(int id) {
		mId = id;
	}
	
	public int getId() {
		return mId;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setDescription(String descr){
		description = descr;
	}

	
	/**
	 * Renderer for ComboBox
	 */
	public String toString() {
		if(isActive){
			return "[x] " + description;
		}
		return "[-] " + description;
	}
	
	/**
	 * Activates the template
	 * Set active flag to true
	 */
	public void activate(){
		isActive = true;
	}
	
	/**
	 * Deactivates the template
	 * Set the active flag to false
	 */
	public void deactivate(){
		isActive = false;
	}
	
	/**
	 * Toggle activation of the template
	 */
	public void toggleActive(){
		isActive = !isActive;
	}
	
	/**
	 * Check if template is active or not
	 * @return boolean isActive flag
	 */
	public boolean isActive(){
		return isActive;
	}
}
