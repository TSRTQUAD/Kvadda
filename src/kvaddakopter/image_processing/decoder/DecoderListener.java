package kvaddakopter.image_processing.decoder;

import java.awt.image.BufferedImage;

public interface DecoderListener {

	/**
	 * 
	 */
	
	//Events
	public boolean onFrameRecieved(BufferedImage image);
	public void onConnectionLost(boolean manualStop);
	

}
