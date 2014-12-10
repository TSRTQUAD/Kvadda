package kvaddakopter.image_processing.data_types;

import java.util.ArrayList;

/**
 * The Identifier class stores identification information about a target with information from
 * the different detection methods.
 *
 */
public class Identifier {
	// From ColorDetection
	float[] meanHSVValues; // {mean hue, mean saturation, mean value}
	float meanHSVValuesCertainty; // More updates add to certainty

	// From TemplateMatching
	int templateID;
	float percentageMatches;  // Number of matches for templateID divided by total possible mathes for that template.

	
	/**
	 * Constructor which takes mean HSV values as identifier.
	 * @param targetHSVChannels
	 */
	public Identifier(ArrayList<Long> targetHSVChannels){
		meanHSVValues = new float[3];
		meanHSVValuesCertainty = 0.7f;
		templateID = -1;
		percentageMatches = 0;
		
		if(targetHSVChannels == null) return;
		for(int i = 0; i < 3; i++){
			meanHSVValues[i] = (float)targetHSVChannels.get(i);
		}
	}
	
	/**
	 * Constructor which takes template matching information and 
	 * @param newID
	 * @param newPercentageMatches
	 */
	public Identifier(int newID, float newPercentageMatches){
		meanHSVValues = new float[3];
		meanHSVValuesCertainty = 0.7f;
		templateID =  newID;
		percentageMatches = newPercentageMatches;
	}
	
	/**
	 * Compares two identifiers and returns the estimated probability that the identifiers are from the same target.
	 * @param first
	 * @param second
	 * @return
	 */
	public static float compare(Identifier first, Identifier second){
		
		float certaintyFromColorDetection = 0;
		float colorDetectionPower = 1;
		// Multiplies the normalized difference in each channel with the two certainties 
		if(first.meanHSVValuesCertainty > 0 && second.meanHSVValuesCertainty > 0){
			// TODO: Add check if array is initialized
			certaintyFromColorDetection += first.meanHSVValuesCertainty * second.meanHSVValuesCertainty * 
					Math.pow((1 - Math.abs(first.meanHSVValues[0] - second.meanHSVValues[0]) / 255), 4) *  
					Math.pow((1 - Math.abs(first.meanHSVValues[1] - second.meanHSVValues[1]) / 255), 4) *
					Math.pow((1 - Math.abs(first.meanHSVValues[2] - second.meanHSVValues[2]) / 255), 4);
		}
		

		float certaintyFromTemplates = 0;
		float templateMatchingPower = 0;
		if(first.templateID != -1 || second.templateID != -1){
			if(first.templateID == -1 || second.templateID == -1){
				templateMatchingPower = 1;
			} else {
				templateMatchingPower = first.percentageMatches * second.percentageMatches;
				certaintyFromTemplates = (1 - Math.abs(first.percentageMatches - second.percentageMatches));
			}
		}
		
		
		// For each template the number of normalized matches in first and second is
		// added and multiplied by 1 - the difference between number of matches. 
		// Let a be the number of matches for template i divided by maximum possible 
		// matches in that template for identifier 'first'. Let b be the corresponding 
		// for the 'second' identifier. Then each certainty can be multiplied by:   
		// (1 - abs(a - b))		
		// Old version used with addition: ((a + b) / 2) * (1 - abs(a - b)) / numberOfTemplates
		/* XXX: Possible update with detections in multiple templates. 
		 * int numberOfTemplates = Template.getTemplates().size();
		float certaintyFromTemplates = 0;
		float templateMatchingPower = 0;
		for(int i = 0; i < numberOfTemplates; i++){
			float maxNumMatches = Template.getTemplates().get(i).getNumDescriptors();
			float a = first.templateMatches[i] / maxNumMatches;
			float b = second.templateMatches[i] / maxNumMatches;
			certaintyFromTemplates *= (1 - Math.abs(a - b));
			
			templateMatchingPower += a * b;
		}*/
		
		
		// Returns the normalized certainity
		return (colorDetectionPower * certaintyFromColorDetection + templateMatchingPower * certaintyFromTemplates) / 
				(colorDetectionPower + templateMatchingPower);
	}
	
	/**
	 * Adapts an identifier towards a new identifier.
	 * @param newMeasured The identifier to adapt to.
	 */
	public void update(Identifier newMeasured){
		float timeConstant = 10;
		
		// Update colorDetection values
		for(int i = 0; i < 3; i++){
			// Updates mean HSV value by adapting to newMewasured mean HSV values with time constant timeConstant
			meanHSVValues[i] += (newMeasured.meanHSVValues[i] - meanHSVValues[i]) / timeConstant;
			
			// Updates certainty much when there is a low difference between measured and old values
			// If diff/255 < 0.5 (large difference) then certainty is lowered
			float diff = Math.abs(meanHSVValues[i] - newMeasured.meanHSVValues[i]) / 255;
			meanHSVValuesCertainty += (0.5 - diff) / (5 * timeConstant);
			meanHSVValuesCertainty = Math.min(1, Math.max(0, meanHSVValuesCertainty)); // Constrain certainty between 0 and 1
		}

		// Updates number of matches by adapting to newMeasured number of matches with time constant timeConstant
		if(newMeasured.templateID == templateID){
			percentageMatches += (newMeasured.percentageMatches - percentageMatches) / timeConstant;
		}
	}
	
	/**
	 * Sets certainty that the mean HSV values are the correct ones.
	 * @param val certainty
	 * @return
	 */
	public Identifier setmeanHSVValuesCertainty(float val){
		meanHSVValuesCertainty = val;
		return this;
	}
}
