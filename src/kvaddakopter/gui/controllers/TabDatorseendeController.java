package kvaddakopter.gui.controllers;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.image_processing.data_types.FormTemplate;
import kvaddakopter.image_processing.utils.HSVSliders;
import kvaddakopter.image_processing.utils.TemplateMatchSliders;
import kvaddakopter.interfaces.MainBusIPInterface;

public class TabDatorseendeController extends BaseController implements Initializable{

	
	private StackPane root;
	private ImageView view;
	public MainBusIPInterface mainbus;

	/*Background colors*/
	final Background GrayBackground  = new Background(new BackgroundFill(Color.GRAY,new CornerRadii(4.5),new Insets(1.0)));
	final Background LightGrayBackground  = new Background(new BackgroundFill(Color.LIGHTGOLDENRODYELLOW,new CornerRadii(4.5),new Insets(1.0)));
	final Background GreenBackground  = new Background(new BackgroundFill(Color.LIGHTGREEN,new CornerRadii(4.5),new Insets(1.0)));

	/*Image pos*/
	final static int IMAGE_POS_X = -0;
	final static int IMAGE_POS_Y = 0;
	
	/*Buttons Common */
	final static int MODES_POS_X = 200;
	final static int BUTTON_X_START = 600;
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


	/* Options Buttons */
	final static int OPT_BUTTON_X_START = -150;
	final static int OPT_BUTTON_Y_START = -250;
	final static int OPT_BUTTON_Y_SPACE = 40;
	final static int OPT_BUTTON_WIDTH = 200;
	
	
	/* Template lists */
	final ComboBox availableColorTemplates = new ComboBox();
	final ComboBox availableFormTemplates = new ComboBox();
	final static int TEMPLATE_LISTS_X_START = -150;
	final static int TEMPLATE_LISTS_Y_START = 150;
	final static int TEMPLATE_LISTS_Y_SPACE = 40;
	final static int TEMPLATE_LISTS_WIDTH = 200;

	@FXML
	private AnchorPane ipRoot;

	private void initializeModeButtons(VBox vbox ){

		mModeButtonMap = new HashMap<>(); 
		mModeButtonMap.put(MainBusIPInterface.MODE_BACKGROUND_SUBTRACION, "Background Subtraction");
		mModeButtonMap.put(MainBusIPInterface.MODE_BLUR_DETECTION, "Blur Detection");
		mModeButtonMap.put(MainBusIPInterface.MODE_COLOR_CALIBRATION, "Color Calibration");
		mModeButtonMap.put(MainBusIPInterface.MODE_COLOR_DETECTION, "Color Detection");
	//	mModeButtonMap.put(MainBusIPInterface.MODE_TEMPLATE_CALIBRATION, "Template Calibration");
		mModeButtonMap.put(MainBusIPInterface.MODE_TEMPLATE_MATCHING, "Template Matching");
		mModeButtonMap.put(MainBusIPInterface.MODE_TRACKING, "Tracking");

		mModeButtonGroup = new ToggleGroup();

		/*Add listener */
		mModeButtonGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> arg0,
					Toggle toggle, Toggle newToggle) {
				//Get list of modes
				int[] activeModes = mainbus.getIPActiveModes();
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
					mainbus.activateIPMode(mode);
				}else{
					toggledButton.setBackground(DeselectedColor);
					mainbus.deactivateIPMode(mode);
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
		//mImageButtonMap.put(MainBusIPInterface.IMAGE_TEMPLATE_MATCHING, "Template Calibrate Image");
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
				int currentImageMode = mainbus.getIPImageMode();
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
					mainbus.setIPImageMode(mode);
				}else{
					mainbus.setIPImageMode(-1);
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

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		System.out.println("IP DONE");
	}

	
	public void loadIPGUI(AnchorPane root2) {
		root = new StackPane(); // XXX: Possibly we create this multiple times.
		mainbus = this.getParent().getMainBus();
		VBox vbox = new VBox();
		//vbox.backgroundProperty().set(LightGrayBackground);
		vbox.setTranslateX(MODES_POS_X);
		root.getChildren().add(vbox);
		initializeModeButtons(vbox);
		initializeImageButtons(vbox);

		Button startIPBtn = new Button();
		startIPBtn.setText("Start Image Processing Unit");
		startIPBtn.setTranslateX(OPT_BUTTON_X_START);
		startIPBtn.setTranslateY(OPT_BUTTON_Y_START);
		startIPBtn.setMinWidth(OPT_BUTTON_WIDTH);
		root.getChildren().add(startIPBtn);
		startIPBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				//mainbus.setColorCalibrationMode(true);
				mainbus.setIsIPRunning(true);
				synchronized(mainbus){
					mainbus.notifyAll();
				}
				System.out.println("Image Processing started from GUI");
			}
		});
		
		Button stopIPBtn = new Button();
		stopIPBtn.setText("Stop Image Processing Unit");
		stopIPBtn.setTranslateX(OPT_BUTTON_X_START + OPT_BUTTON_WIDTH + TEMPLATE_LISTS_Y_SPACE);
		stopIPBtn.setTranslateY(OPT_BUTTON_Y_START);
		stopIPBtn.setMinWidth(OPT_BUTTON_WIDTH);
		root.getChildren().add(stopIPBtn);
		stopIPBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				mainbus.setIsIPRunning(false);
			}
		});


		Button cCalibBtn = new Button();
		cCalibBtn.setText("Color Template Settings");
		cCalibBtn.setTranslateX(OPT_BUTTON_X_START);
		cCalibBtn.setTranslateY(OPT_BUTTON_Y_START+OPT_BUTTON_Y_SPACE);
		cCalibBtn.setMinWidth(OPT_BUTTON_WIDTH);
		root.getChildren().add(cCalibBtn);
		//Initiating sliderGUI
		final HSVSliders hsvSliders = new HSVSliders(this);

		cCalibBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				hsvSliders.setHSVChannels(mainbus, null);
			}
		});

		Button imgCalibrateTemplateButton = new Button();
		imgCalibrateTemplateButton.setText("Template matching Settings");
		final TemplateMatchSliders templateSliders = new TemplateMatchSliders(this);
		imgCalibrateTemplateButton.setTranslateX(OPT_BUTTON_X_START);
		imgCalibrateTemplateButton.setTranslateY(OPT_BUTTON_Y_START+OPT_BUTTON_Y_SPACE*2);
		imgCalibrateTemplateButton.setMinWidth(OPT_BUTTON_WIDTH);
		root.getChildren().add(imgCalibrateTemplateButton);
		imgCalibrateTemplateButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				templateSliders.setTemplateGeomtry(mainbus, null);
			}
		});
		

		// Color and form template lists
		root.getChildren().add(availableColorTemplates);
		root.getChildren().add(availableFormTemplates);
		availableColorTemplates.setTranslateX(TEMPLATE_LISTS_X_START);
		availableColorTemplates.setTranslateY(TEMPLATE_LISTS_Y_START);
		availableColorTemplates.setMinWidth(TEMPLATE_LISTS_WIDTH);
		availableColorTemplates.setVisibleRowCount(10);
		availableFormTemplates.setTranslateX(TEMPLATE_LISTS_X_START);
		availableFormTemplates.setTranslateY(TEMPLATE_LISTS_Y_START + TEMPLATE_LISTS_Y_SPACE);
		availableFormTemplates.setMinWidth(TEMPLATE_LISTS_WIDTH);
		

		Button toggleColorTemplateActiveButton = new Button();
		toggleColorTemplateActiveButton.setText("Toggle active");
		toggleColorTemplateActiveButton.setTranslateX(TEMPLATE_LISTS_X_START + TEMPLATE_LISTS_WIDTH + TEMPLATE_LISTS_Y_SPACE);
		toggleColorTemplateActiveButton.setTranslateY(TEMPLATE_LISTS_Y_START);
		toggleColorTemplateActiveButton.setMinWidth(TEMPLATE_LISTS_WIDTH);
		root.getChildren().add(toggleColorTemplateActiveButton);
		toggleColorTemplateActiveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				ColorTemplate currTemplate = (ColorTemplate)availableColorTemplates.getValue();
				currTemplate.toggleActive();
//				updateColorTemplates();
				availableColorTemplates.setValue(currTemplate);
			}
		});

		Button toggleFormTemplateActiveButton = new Button();
		toggleFormTemplateActiveButton.setText("Toggle active");
		toggleFormTemplateActiveButton.setTranslateX(TEMPLATE_LISTS_X_START + TEMPLATE_LISTS_WIDTH + TEMPLATE_LISTS_Y_SPACE);
		toggleFormTemplateActiveButton.setTranslateY(TEMPLATE_LISTS_Y_START + TEMPLATE_LISTS_Y_SPACE);
		toggleFormTemplateActiveButton.setMinWidth(TEMPLATE_LISTS_WIDTH);
		root.getChildren().add(toggleFormTemplateActiveButton);
		toggleFormTemplateActiveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				FormTemplate currTemplate = (FormTemplate)availableFormTemplates.getValue();
				if(currTemplate != null){
					currTemplate.toggleActive();
					updateFormTemplates();
					availableFormTemplates.setValue(currTemplate);
				}
				updateFormTemplates();
				
			}
		});
		
		updateColorTemplates();
		updateFormTemplates();
		
		
		view = new ImageView();
		view.setTranslateX(IMAGE_POS_X);
		view.setTranslateX(IMAGE_POS_Y);
		root.getChildren().add(view);
		
		
		AnchorPane.setTopAnchor(root, 100.0);
		AnchorPane.setBottomAnchor(root, 100.0);
		AnchorPane.setLeftAnchor(root, 100.0);
		AnchorPane.setRightAnchor(root, 100.0);
		root2.getChildren().add(root);


	}
	
	public void updateColorTemplates(){
		availableColorTemplates.getItems().clear(); // TODO: Remove and update text instead
		ArrayList<ColorTemplate> templates = mainbus.getIPColorTemplates();
		for(ColorTemplate template : templates){
			if(!availableColorTemplates.getItems().contains(template)){
				availableColorTemplates.getItems().add(template);
			}
		}
	}

	public void updateFormTemplates(){
		availableFormTemplates.getItems().clear(); // TODO: Remove and update text instead
		ArrayList<FormTemplate> templates = mainbus.getIPFormTemplates();
		for(FormTemplate template : templates){
			if(!availableFormTemplates.getItems().contains(template)){
				availableFormTemplates.getItems().add(template);
			}
		}
	}	
	
	public void updateImage() {
		if(mainbus == null) return;
		Image image = mainbus.getIPImageToShow();
		if(image != null){
			view.setImage(image);
			System.out.println("Image redraw");
		}

		
	}

}
