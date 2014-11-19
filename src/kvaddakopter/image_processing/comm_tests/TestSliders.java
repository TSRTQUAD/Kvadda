package kvaddakopter.image_processing.comm_tests;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.utils.HSVSliders;
import kvaddakopter.interfaces.MainBusIPInterface;

import org.opencv.core.Core;

public class TestSliders extends Application implements Runnable{
	static MainBusIPInterface mMainbus;
	
	public TestSliders(){
		super();
	}
	
	public TestSliders(IPMockMainBus mainbus){
		mMainbus = mainbus;
	}
	
	@Override
    public void start(final Stage primaryStage) {
		//M�ste laddas i b�rjan av programmet... F�rslagsvis h�r.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
				
        Button btn = new Button();
        btn.setText("Open a New Window");
        
        final HSVSliders hsvSliders = new HSVSliders();

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	//mMainbus.setColorCalibrationMode(true);
            	ColorTemplate cTemplate = new ColorTemplate();
            	mMainbus.addIPColorTemplate(cTemplate);
            	mMainbus.setIPCalibTemplate(mMainbus.getIPColorTemplates().get(0));
            	
                hsvSliders.setHSVChannels(mMainbus.getIPCalibTemplate(),primaryStage);
            }
        });
         
        StackPane root = new StackPane();
        root.getChildren().add(btn);
         
        Scene scene = new Scene(root, 300, 250);
         
        primaryStage.setTitle("java-buddy.blogspot.com");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

	@Override
	public void run() {
		launch();
	}

}
