package kvaddakopter.image_processing.comm_tests;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.data_types.FormTemplate;
import kvaddakopter.image_processing.utils.HSVSliders;
import kvaddakopter.image_processing.utils.TemplateMatchSliders;
import kvaddakopter.interfaces.MainBusIPInterface;

import org.opencv.core.Core;

public class IPTestGUI extends Application implements Runnable{
	static MainBusIPInterface mMainbus;


	/*Background colors*/
	final Background GrayBackground  = new Background(new BackgroundFill(Color.GRAY,new CornerRadii(4.5),new Insets(1.0)));
	final Background LightGrayBackground  = new Background(new BackgroundFill(Color.LIGHTGOLDENRODYELLOW,new CornerRadii(4.5),new Insets(1.0)));
	final Background GreenBackground  = new Background(new BackgroundFill(Color.LIGHTGREEN,new CornerRadii(4.5),new Insets(1.0)));


	/*Buttons Common */
	final static int BUTTON_X_START = 300;
	final static int BUTTON_Y_START = 20;
	final static int BUTTON_GROUP_SEPARATION = 40;
	
	final Background SelectedColor = GreenBackground;
	final Background DeselectedColor = GrayBackground;

	/* Image Toggle Buttons */ 
	ToggleGroup mImageButtonGroup;
	ToggleButton[] mImageButtons;
	HashMap<Integer,String> mImageButtonMap;
	final static int IMG_BUTTON_MIN_WIDTH   = 150;

	/* Mode Buttons */
	ToggleGroup mModeButtonGroup;
	ToggleButton[] mModeButtons;
	HashMap<Integer,String> mModeButtonMap;
	final static int MODE_BUTTON_MIN_WIDTH   = 150;


	/*Options Buttons */
	final static int OPT_BUTTON_X_START = -150;
	final static int OPT_BUTTON_Y_START = -150;
	final static int OPT_BUTTON_Y_SPACE = 40;
	public IPTestGUI(){
		super();
	}

	public IPTestGUI(IPMockMainBus mainbus){
		mMainbus = mainbus;
	}

	@Override
	public void start(final Stage primaryStage) {

		StackPane root = new StackPane();
		
		VBox vbox = new VBox();
		vbox.backgroundProperty().set(LightGrayBackground);
		root.getChildren().add(vbox);
		initializeModeButtons(vbox);
		initializeImageButtons(vbox);
	
		Button startIPBtn = new Button();
		startIPBtn.setText("Start Image Processing Unit");
		startIPBtn.setTranslateX(OPT_BUTTON_X_START);
		startIPBtn.setTranslateY(OPT_BUTTON_Y_START);
		root.getChildren().add(startIPBtn);
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


		Button cCalibBtn = new Button();
		cCalibBtn.setText("Color calibration");
		cCalibBtn.setTranslateX(OPT_BUTTON_X_START);
		cCalibBtn.setTranslateY(OPT_BUTTON_Y_START+OPT_BUTTON_Y_SPACE);
		root.getChildren().add(cCalibBtn);
		//Initiating sliderGUI
		final HSVSliders hsvSliders = new HSVSliders();

		cCalibBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				mMainbus.setIPCalibTemplate(mMainbus.getIPColorTemplates().get(mMainbus.getIPColorTemplates().size()-1));
				hsvSliders.setHSVChannels(mMainbus.getIPCalibTemplate(),primaryStage);
			}
		});

		Button imgCalibrateTemplateButton = new Button();
		imgCalibrateTemplateButton.setText("Template matching Settings");
		final TemplateMatchSliders templateSliders = new TemplateMatchSliders();
		imgCalibrateTemplateButton.setTranslateX(OPT_BUTTON_X_START);
		imgCalibrateTemplateButton.setTranslateY(OPT_BUTTON_Y_START+OPT_BUTTON_Y_SPACE*2);
		root.getChildren().add(imgCalibrateTemplateButton);
		imgCalibrateTemplateButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				templateSliders.setTemplateGeomtry(mMainbus, primaryStage);
			}
		});
	


		Scene scene = new Scene(root, 500, 500);

		primaryStage.setTitle("GUI/Imageprocessing test");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

	@Override
	public void run() {
		launch();
	}
	
	private void initializeModeButtons(VBox vbox ){

		mModeButtonMap = new HashMap<>(); 
		mModeButtonMap.put(MainBusIPInterface.MODE_BACKGROUND_SUBTRACION, "Background Subtraction");
		mModeButtonMap.put(MainBusIPInterface.MODE_BLUR_DETECTION, "Blur Detection");
		mModeButtonMap.put(MainBusIPInterface.MODE_COLOR_CALIBRATION, "Color Calibration");
		mModeButtonMap.put(MainBusIPInterface.MODE_COLOR_DETECTION, "Color Detection");
		mModeButtonMap.put(MainBusIPInterface.MODE_TEMPLATE_CALIBRATION, "Template Calibration");
		mModeButtonMap.put(MainBusIPInterface.MODE_TEMPLATE_MATCHING, "Template Matching");
		mModeButtonMap.put(MainBusIPInterface.MODE_TRACKING, "Tracking");

		mModeButtonGroup = new ToggleGroup();
	
		/*Add listener */
		mModeButtonGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> arg0,
					Toggle toggle, Toggle newToggle) {
				//Get list of modes
				int[] activeModes = mMainbus.getIPActiveModes();
				//Read toggle event
				int mode;
				ToggleButton toggledButton;
				if(newToggle != null){
					toggledButton = (ToggleButton)newToggle;
					mode = Integer.parseInt(toggledButton.getId());
				}else{
					toggledButton = (ToggleButton)toggle;
					mode = Integer.parseInt(toggledButton.getId());
				}

				//Check previous state of the mode and toggle it.
				if(activeModes[mode] == 0){
					toggledButton.setBackground(SelectedColor);
					mMainbus.activateIPMode(mode);
				}else{
					toggledButton.setBackground(DeselectedColor);
					mMainbus.deactivateIPMode(mode);
				}
			}
		});

		mModeButtons = new ToggleButton[mModeButtonMap.size()];
		Iterator<Entry<Integer, String>> it = mModeButtonMap.entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			Entry<Integer, String> pairs = (Entry<Integer, String>)it.next();
			//Create new button
			final ToggleButton newButton = new ToggleButton();
			//Set text
			newButton.setTextAlignment(TextAlignment.LEFT);
			newButton.setText(pairs.getValue());

			//Set X and Y position
			newButton.setTranslateX(BUTTON_X_START);
			newButton.setTranslateY(BUTTON_Y_START);
			newButton.setMinWidth(MODE_BUTTON_MIN_WIDTH);

			//Add button to toggle group
			newButton.setToggleGroup(mModeButtonGroup);


			//Set default gray background
			newButton.setBackground(DeselectedColor);

			final int imageCode = pairs.getKey();
			newButton.setId(String.valueOf(imageCode));
			vbox.getChildren().add(newButton);

			mModeButtons[i++] = newButton;		


		}
	}


	private void initializeImageButtons(VBox vbox){

		mImageButtonMap = new HashMap<>(); 
		mImageButtonMap.put(MainBusIPInterface.IMAGE_TEMPLATE_CALIBRATE, "Template Match Image");
		mImageButtonMap.put(MainBusIPInterface.IMAGE_TEMPLATE_MATCHING, "Template Calibrate Image");
		mImageButtonMap.put(MainBusIPInterface.IMAGE_TARGET, "Target Image");
		mImageButtonMap.put(MainBusIPInterface.IMAGE_DEFAULT, "Default Image");
		mImageButtonMap.put(MainBusIPInterface.IMAGE_CUT_OUT, "Cut out Image");
		mImageButtonMap.put(MainBusIPInterface.IMAGE_COLOR_CALIBRRATE, "Color Calibrate Image");
		mImageButtonMap.put(MainBusIPInterface.IMAGE_SURPRISE, "Suprise Image");

		mImageButtons = new ToggleButton[mImageButtonMap.size()];
		mImageButtonGroup = new ToggleGroup();
		mImageButtonGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> arg0,
					Toggle toggle, Toggle newToggle) {
				//Get active image mode
				int currentImageMode = mMainbus.getIPImageMode();
				//Read toggle event
				int mode;
				ToggleButton toggledButton;
				if(newToggle != null){
					toggledButton = (ToggleButton)newToggle;
					mode = Integer.parseInt(toggledButton.getId());
				}else{
					toggledButton = (ToggleButton)toggle;
					mode = Integer.parseInt(toggledButton.getId());
				}
				
				//Set all image buttons to gray
				for (int j = 0; j < mImageButtons.length; j++) {
					mImageButtons[j].setBackground(DeselectedColor);
				}

				//Check previous state of the mode and toggle it.
				if(currentImageMode != mode){
					toggledButton.setBackground(SelectedColor);
					mMainbus.setIPImageMode(mode);
				}else{
					mMainbus.setIPImageMode(-1);
				}
			}
		});

		Iterator<Entry<Integer, String>> it = mImageButtonMap.entrySet().iterator();
		int i = 0;
		while (it.hasNext()) {
			Entry<Integer, String> pairs = (Entry<Integer, String>)it.next();
			
			//Create new button
			final ToggleButton newButton = new ToggleButton();
			
			//Set text
			newButton.setText(pairs.getValue());
			
			//Set X and Y position
			newButton.setTranslateX(BUTTON_X_START);
			newButton.setTranslateY(BUTTON_Y_START+BUTTON_GROUP_SEPARATION);
			newButton.setMinWidth(MODE_BUTTON_MIN_WIDTH);

			//Set Defualt Background
			newButton.setBackground(DeselectedColor);

			//Add button to toggle group
			newButton.setToggleGroup(mImageButtonGroup);
			
			//Set button id to the image mode
			final int imageCode = pairs.getKey();
			newButton.setId(String.valueOf(imageCode));
			vbox.getChildren().add(newButton);
			mImageButtons[i++] = newButton;			
		
		}
	}


}


/*
 * 
 * 
 * Skip to content
 This repository
Explore
Gist
Blog
Help
zizou89skovde zizou89skovde
 
7  Unwatch 
  Star 1
 Fork 0TSRTQUAD/Kvadda
 branch: master  Kvadda / src / kvaddakopter / image_processing / comm_tests / IPTestGUI.java
Tobias Hammarling 7 hours ago Commit refresh
1 contributor
181 lines (134 sloc)  5.799 kb RawBlameHistory   
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
import kvaddakopter.image_processing.data_types.FormTemplate;
import kvaddakopter.image_processing.utils.HSVSliders;
import kvaddakopter.image_processing.utils.TemplateMatchSliders;
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
        
      
        
       
        Button imgCalibrateTemplateButton = new Button();
        imgCalibrateTemplateButton.setText("Template matching Settings");
        final TemplateMatchSliders templateSliders = new TemplateMatchSliders();
        imgCalibrateTemplateButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	templateSliders.setTemplateGeomtry(mMainbus, primaryStage);
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
        
        imgCalibrateTemplateButton.setTranslateY(120);
        root.getChildren().add(imgCalibrateTemplateButton);
        
        
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

*/


