package kvaddakopter.image_processing.data_types;

import java.util.ArrayList;

import kvaddakopter.maps.GPSCoordinate;

import org.ejml.simple.SimpleMatrix;
import org.opencv.core.Point;
import org.opencv.core.Rect;

/**
 * The TargetObject class contains information about the targets state and covariance.
 * It also contains an Identifier object. The state and covariance are represented as 
 * matrices usable in a Kalman filter. 
 * <p>
 * The filter currentyl works on 2D image positions. 
 * <p>
 * The state is defined as the matrix [x_position y_position x_velocity y_velocity] transposed.
 */
public class TargetObject {
	private SimpleMatrix x; // State [x1, x2, dot(x1), dot(x2)]'
	private SimpleMatrix P; // Covariance for the states
	private Identifier identifier; // Holds identification information about the target
	private GPSCoordinate geoPosition; // Estimated Geo position for target

	/**
	 * The constructor for TargetObject assumes zero velocity and creates the state and
	 * covariance matrices. The identifier is created with mean HSV values from the color detection.
	 * @param position 		A 2x1 matrix representing the position of the target with 
	 * x = position(0, 0) and y = position(1, 0)
	 * @param noise_level	The scale factor for the covariance matrix
	 * @param targetHSVChannels	The HSV channels to use in matching.
	 */
	public TargetObject(SimpleMatrix position_, float noise_level, ArrayList<Long> targetHSVChannels) {
		// Create the state matrix with given position measurements
		x = new SimpleMatrix(4, 1, true, position_.get(0, 0), position_.get(1,
				0), 0, 0);

		// Create the covariance matrix with noise_level on diagonal
		P = SimpleMatrix.diag(1, 1, 3, 3).scale(noise_level);
		identifier = new Identifier(targetHSVChannels);
	}
	
	/**
	 * The constructor for TargetObject assumes zero velocity and creates the state and
	 * covariance matrices. The identifier is created with mean HSV values from the color detection.
	 * @param boundingBox 		A Rect object where position is considered as center of the Rect.
	 * @param noise_level		The scale factor for the covariance matrix.
	 * @param targetHSVChannels	The HSV channels to use in matching.
	 */
	public TargetObject(Rect boundingBox, float noise_level, ArrayList<Long> targetHSVChannels) {
		// Create the state matrix with given position measurements
		x = new SimpleMatrix(4, 1, true, boundingBox.x + boundingBox.width / 2,
				boundingBox.y + boundingBox.height / 2, 0, 0);

		// Create the covariance matrix with noise_level on diagonal
		P = SimpleMatrix.diag(1, 1, 3, 3).scale(noise_level);

		identifier = new Identifier(targetHSVChannels);
	}

	/**
	 * The constructor for TargetObject assumes zero velocity and creates the state and
	 * covariance matrices. The identifier is created with number of matches in each template
	 * used in template matching.
	 * @param rectCorners		An ArrayList of Points surrounding the target. The position is
	 * estimated as the mean of the points.
	 * @param noise_level		The scale factor for the covariance matrix.
	 * @param targetNumMatches	Number of matches in each templade.
	 */
	public TargetObject(ArrayList<Point> rectCorners, float noise_level,
			ArrayList<Long> targetNumMatches) {
		// Create the state matrix with given position measurements
		float meanX = 0;
		float meanY = 0;
		for(Point point : rectCorners){
			meanX += point.x;
			meanY += point.y;
		}
		meanX /= 4;
		meanY /= 4;
		x = new SimpleMatrix(4, 1, true, meanX, meanY, 0, 0);

		// Create the covariance matrix with noise_level on diagonal
		P = SimpleMatrix.diag(1, 1, 3, 3);
		P = P.scale(noise_level);

		identifier = new Identifier(targetNumMatches);
	}

	/**
	 * Returns the state matrix.
	 * @return 4x4 state matrix.
	 */
	public SimpleMatrix getState() {
		return x;
	}

	/**
	 * Returns the covariance matrix.
	 * @return 4x4 covariance matrix.
	 */
	public SimpleMatrix getCovariance() {
		return P;
	}

	/**
	 * Sets the state.
	 * @param state_ The new state.
	 */
	public void setState(SimpleMatrix state_) {
		x = state_;
	}

	/**
	 * Sets the covariance.
	 * @param covariance_ The new covariance.
	 */
	public void setCovariance(SimpleMatrix covariance_) {
		P = covariance_;
	}
	
	/**
	 * Returns the position calculated from the state matrix.
	 * @return 2x1 matrix with position.
	 */
	public SimpleMatrix getPosition() {
		return new SimpleMatrix(2, 1, true, x.get(0, 0), x.get(1, 0));
	}

	/**
	 * Returns the velocity calculated from the state matrix.
	 * @return 2x1 matrix with velocity.
	 */
	public SimpleMatrix getVelocity() {
		return new SimpleMatrix(2, 1, true, x.get(3, 0), x.get(4, 0));
	}

	/**
	 * Returns the absolute value of the velocity vector.
	 * @return speed
	 */
	public double getSpeed() {
		return Math.sqrt(Math.pow(x.get(2, 0), 2) + Math.pow(x.get(3, 0), 2));
	}

	/**
	 * Updates the identifier to go towards the new identifier. 
	 * @param newIdentifier
	 * @see Identifier#update(Identifier)		
	 */
	public void updateIdentifier(Identifier newIdentifier) {
		identifier.update(newIdentifier);
	}

	/**
	 * Sets the identifier to new identifier.
	 * @param newIdentifier
	 */
	public void setIdentifier(Identifier newIdentifier) {
		identifier = newIdentifier;
	}

	/**
	 * Returns the identifier of the target.
	 * @return identifier
	 * @see Identifier
	 */
	public Identifier getIdentifier() {
		return identifier;
	}
	
	/**
	 * Returns estimated GPS coordinate.
	 * @return geo position
	 */
	public GPSCoordinate getGPSCoordinate(){
		return geoPosition;
	}
	
	/**
	 * Sets estimated GPS coordinate.
	 * @param newCoordinate
	 */
	public void setGPSCoordinate(GPSCoordinate newCoordinate){
		geoPosition = newCoordinate;
	}
}
