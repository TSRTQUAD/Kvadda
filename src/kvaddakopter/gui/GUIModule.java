package kvaddakopter.gui;



import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import kvaddakopter.communication.ManualControl2;
import kvaddakopter.gui.controllers.MainController;
import kvaddakopter.interfaces.IPAndGUIInterface;
import kvaddakopter.interfaces.MainBusGUIInterface;
import kvaddakopter.interfaces.ManualControlInterface;

public class GUIModule extends Application implements Runnable{

	/**
	 * Path for base View FXML-file
	 */
	private String mainViewPath = "/kvaddakopter/gui/views/Main.fxml";


	/**
	 * Path for base View FXML-file
	 */
	private String mainCss = "/kvaddakopter/application.css";


	/**
	 * Application title
	 */
	private String applicationTitle = "KvaddaKopter -  Mission planner";


	private ManualControl2 manualControl;


	static IPAndGUIInterface mainBus;

	/**
	 * Runs once when the application is started.
	 * @param primaryStage Stage used for application
	 */

	public GUIModule(){
		super();
	}

	public GUIModule(IPAndGUIInterface mainBus1) {
		mainBus = mainBus1;
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(this.mainViewPath));
			Parent root = loader.load();
			MainController mainController = loader.getController();
			mainController.setMainBus(mainBus);
			Scene scene = new Scene(root);
			
			
			//Consume all key events to this GUI.
			this.manualControl  = new ManualControl2((ManualControlInterface) mainBus);
			scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
				@Override
				public void handle(KeyEvent event) {
					manualControl.handleKeyPressed(event.getCode());
					event.consume();
				}
			});

			
			
			scene.getStylesheets().add(getClass().getResource(this.mainCss).toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle(this.applicationTitle);
			primaryStage.show();


		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		launch();
	}



}
