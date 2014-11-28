package kvaddakopter.gui;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import kvaddakopter.gui.controllers.MainController;
import kvaddakopter.interfaces.IPAndGUIInterface;
import kvaddakopter.interfaces.MainBusGUIInterface;

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
