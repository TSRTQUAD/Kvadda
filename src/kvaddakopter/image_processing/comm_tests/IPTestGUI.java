package kvaddakopter.image_processing.comm_tests;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.utils.HSVSliders;
import kvaddakopter.interfaces.MainBusIPInterface;

import org.opencv.core.Core;

public class IPTestGUI extends Application implements Runnable{
	static MainBusIPInterface mMainbus;
	
	public IPTestGUI(){
		super();
	}
	
	public IPTestGUI(IPMockMainBus mainbus){
		mMainbus = mainbus;
	}
	
	@Override
    public void start(final Stage primaryStage) {
		Button startIPBtn = new Button();
		startIPBtn.setText("Start Image Processing Unit");
        

		startIPBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	//mMainbus.setColorCalibrationMode(true);
            	mMainbus.setIsIPRunning(true);
            	synchronized(mMainbus){
            		mMainbus.notify();
            	}
            	System.out.println("Image Processing started from GUI");
            }
        });
		
        Button addCTemplateButton = new Button();
        addCTemplateButton.setText("Add ColorTemplate");
        

        addCTemplateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	//mMainbus.setColorCalibrationMode(true);
            	ColorTemplate cTemplate = new ColorTemplate();
            	mMainbus.addIPColorTemplate(cTemplate);
            	System.out.println("new Color Template created");
            }
        });
        
        Button cCalibBtn = new Button();
        cCalibBtn.setText("Color calibration");
        //Initiating sliderGUI
        final HSVSliders hsvSliders = new HSVSliders();

        cCalibBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	mMainbus.activateIPMode(MainBusIPInterface.COLOR_CALIBRATION_MODE);
            	mMainbus.setIPCalibTemplate(mMainbus.getIPColorTemplates().get(mMainbus.getIPColorTemplates().size()-1));
            	
                hsvSliders.setHSVChannels(mMainbus.getIPCalibTemplate(),primaryStage);
            }
        });
        
        Button imgModeBtn1 = new Button();
        imgModeBtn1.setText("Show default image");

        imgModeBtn1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	mMainbus.setIPImageMode(MainBusIPInterface.DEFAULT_IMAGE);
            }
        });
        
        Button imgModeBtn2 = new Button();
        imgModeBtn2.setText("Show cut out image");

        imgModeBtn2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	mMainbus.setIPImageMode(MainBusIPInterface.CUT_OUT_IMAGE);
            }
        });
        
        Button imgModeBtn3 = new Button();
        imgModeBtn3.setText("Surprise");

        imgModeBtn3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	mMainbus.setIPImageMode(MainBusIPInterface.SURPRISE_IMAGE);
            }
        });
        
        Button imgModeBtn4 = new Button();
        imgModeBtn4.setText("Template matching image");

        imgModeBtn4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	mMainbus.setIPImageMode(MainBusIPInterface.TEMPLATE_MATCHING_IMAGE);
            }
        });
        
        StackPane root = new StackPane();
        root.setAlignment(Pos.TOP_CENTER);
        //Place buttons
        root.getChildren().add(startIPBtn);
        addCTemplateButton.setTranslateY(60);
        root.getChildren().add(addCTemplateButton);
        cCalibBtn.setTranslateY(90);
        root.getChildren().add(cCalibBtn);
        
        //Image buttons
        imgModeBtn1.setTranslateX(150);
        root.getChildren().add(imgModeBtn1);
        
        imgModeBtn2.setTranslateX(150);
        imgModeBtn2.setTranslateY(30);
        root.getChildren().add(imgModeBtn2);
        
        imgModeBtn3.setTranslateX(150);
        imgModeBtn3.setTranslateY(60);
        root.getChildren().add(imgModeBtn3);
        
        
        imgModeBtn4.setTranslateX(150);
        imgModeBtn4.setTranslateY(90);
        root.getChildren().add(imgModeBtn4);
         
        Scene scene = new Scene(root, 500, 250);
         
        primaryStage.setTitle("GUI/Imageprocessing test");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

	@Override
	public void run() {
		launch();
	}

}
