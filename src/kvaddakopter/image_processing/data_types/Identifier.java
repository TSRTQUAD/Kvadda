package kvaddakopter.image_processing.data_types;

import java.util.ArrayList;

public class Identifier {
	// From ColorDetection
	float[] meanHSVValues; // {mean hue, mean saturation, mean value}
	float meanHSVValuesCertainty; // More updates add to certainty

	// From TemplateMatching
	float[] templateMatches;  // Number of matches per template where place 
							// in array corresponds to place in TemplateObjects array
	
	public Identifier(ArrayList<Long> targetHSVChannels){
		meanHSVValues = new float[3];
		meanHSVValuesCertainty = 0.5f;
		templateMatches = new float[Template.getTemplates().size()];
		
		for(int i = 0; i < 3; i++){
			meanHSVValues[i] = (float)targetHSVChannels.get(i);
		}
	}
	
	static float compare(Identifier first, Identifier second){
		
		float certaintyFromColorDetection = 0;
		float colorDetectionPower = 1;
		// Multiplies the normalized difference in each channel with the two certainties 
		if(first.meanHSVValuesCertainty > 0 && second.meanHSVValuesCertainty > 0){
			// TODO: Add check if array is initialized
			certaintyFromColorDetection += first.meanHSVValuesCertainty * second.meanHSVValuesCertainty * 
					(1 - Math.abs(first.meanHSVValues[0] - second.meanHSVValues[0]) / 255) *  
					(1 - Math.abs(first.meanHSVValues[1] - second.meanHSVValues[1]) / 255) *
					(1 - Math.abs(first.meanHSVValues[2] - second.meanHSVValues[2]) / 255);
		}
		
		
		// For each template the number of normalized matches in first and second is
		// added and multiplied by 1 - the difference between number of matches. 
		// Let a be the number of matches for template i divided by maximum possible 
		// matches in that template for identifier 'first'. Let b be the corresponding 
		// for the 'second' identifier. Then each certainty can be multiplied by:   
		// (1 - abs(a - b))		
		// Old version used with addition: ((a + b) / 2) * (1 - abs(a - b)) / numberOfTemplates
		int numberOfTemplates = Template.getTemplates().size();
		float certaintyFromTemplates = 0;
		float templateMatchingPower = 0;
		for(int i = 0; i < numberOfTemplates; i++){
			float maxNumMatches = Template.getTemplates().get(i).getNumDescriptors();
			float a = first.templateMatches[i] / maxNumMatches;
			float b = second.templateMatches[i] / maxNumMatches;
			certaintyFromTemplates += (1 - Math.abs(a - b));
			
			templateMatchingPower += a * b;
		}
		
		
		// Returns the normalized certainity
		return (colorDetectionPower * certaintyFromColorDetection + templateMatchingPower * certaintyFromTemplates) / 
				(colorDetectionPower + templateMatchingPower);
	}
	
	public void update(Identifier newMeasured){
		float timeConstant = 10;
		
		// Update colorDetection values
		for(int i = 0; i < 3; i++){
			// Updates mean HSV value by adapting to newMewasured mean HSV values with time constant timeConstant
			meanHSVValues[i] += (newMeasured.meanHSVValues[i] - meanHSVValues[i]) / timeConstant;
			
			// Updates certainty much when there is a low difference between measured and old values
			// If diff/255 < 0.5 (large difference) then certainty is lowered
			float diff = Math.abs(meanHSVValues[i] - newMeasured.meanHSVValues[i]) / 255;
			meanHSVValuesCertainty += (0.5 - diff) / timeConstant;
			meanHSVValuesCertainty = Math.min(0, Math.max(1, meanHSVValuesCertainty)); // Constrain certainty between 0 and 1
		}

		// Updates number of matches by adapting to newMeasured number of matches with time constant timeConstant
		int numberOfTemplates = Template.getTemplates().size();
		for(int i = 0; i < numberOfTemplates; i++){
			templateMatches[i] += (newMeasured.templateMatches[i] - templateMatches[i]) / timeConstant;
		}
	}
}
