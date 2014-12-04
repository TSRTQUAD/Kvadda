package kvaddakopter.communication;

import java.awt.event.KeyEvent;

import javafx.scene.input.KeyCode;
import kvaddakopter.interfaces.ManualControlInterface;

public class ManualControl2 {
	
	
	/**
	 * Mainbus
	 */
	private ManualControlInterface mainbus;
	
	
	/**
	 * The speed increment to use.
	 */
	protected float speed = (float) 0.1;
	
	
	/**
	 * Constructor
	 * @param mainbus
	 */
	public ManualControl2(ManualControlInterface mainbus){
		this.mainbus = mainbus;
	}
	
	
	/**
	 * Handle the given keyCode to trigger correct event.
	 * @param keycode
	 */
	public void handleKeyPressed(KeyCode keycode){
		
		float[] controlSignal = mainbus.getControlSignal();
		System.out.println("KEY PRESSED TO ACTION:");
		
		switch (keycode) {
		case C:
			mainbus.setRunController(!mainbus.getRunController());;
			break;
		case ESCAPE:
			System.out.println("Get DOWN QUADDAKOPTER");
			mainbus.setEmergencyStop(true);
			break;
		case UP:
			System.out.println("Go Up (gaz+)");
			controlSignal[3] += speed;
			mainbus.setControlSignal(controlSignal);
			break;
		case DOWN:
			System.out.println("Go Down (gaz-)");
			controlSignal[3] -= speed;
			mainbus.setControlSignal(controlSignal);
			break;
		case W:
			System.out.println("Go Forward (pitch+)");
			controlSignal[2] -= speed;
			mainbus.setControlSignal(controlSignal);
			break;
		case S:
			System.out.println("Go Backward (pitch-)");
			controlSignal[2] += speed;
			mainbus.setControlSignal(controlSignal);
			break;
		case RIGHT:
			System.out.println("Rotate Right (yaw+)");
			controlSignal[4] += speed;
			mainbus.setControlSignal(controlSignal);
			break;
		case LEFT:
			System.out.println("Rotate Left (yaw-)");
			controlSignal[4] -= speed;
			mainbus.setControlSignal(controlSignal);
			break;
		case D:
			System.out.println("Go Right (roll+)");
			controlSignal[1] += speed;
			mainbus.setControlSignal(controlSignal);
			break;
		case A:
			System.out.println("Go Left (roll-)");
			controlSignal[1] -= speed;
			mainbus.setControlSignal(controlSignal);
			break;

		case SPACE:
			System.out.println("Spaced!!!");
			if (!mainbus.isStarted()) {
				mainbus.setIsStarted(true);
				mainbus.setShouldStart(false);
				System.out.println("Takeoff");
				controlSignal[0] = 1;
				mainbus.setControlSignal(controlSignal);
				synchronized(mainbus){
					mainbus.notifyAll();
				}
			} else if (controlSignal[0] == 1) {
				System.out.println("Landing");
				controlSignal[0] = 0;
				mainbus.setControlSignal(controlSignal);
			}

			break;

		case CONTROL:
			System.out.println("Hovering");
			for (int i = 1; i < 5; i = i + 1) {
				controlSignal[i] = 0;
			}
			mainbus.setControlSignal(controlSignal);
			break;

		default:
			break;
		}
		
	}
}
