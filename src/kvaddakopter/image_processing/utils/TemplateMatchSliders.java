package kvaddakopter.image_processing.utils;

import java.io.File;
import java.util.ArrayList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import kvaddakopter.Mainbus.Mainbus;
import kvaddakopter.image_processing.data_types.FormTemplate;
import kvaddakopter.interfaces.MainBusIPInterface;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class TemplateMatchSliders{
	static final float MIN_SIZE = 0.0f;
	static final float MAX_SIZE = 1.0f;




	double mCurrentWidth  = 0.5;
	double mCurrentHeight = 0.5;

	double mCurrentOffsetX = 0.5;
	double mCurrentOffsetY = 0.5;

	Scene secondScene;
	Stage secondStage;

	int mIdCounter = 0;
	FormTemplate activeTempate = null;
	ArrayList<FormTemplate> mTemplateList;

	public TemplateMatchSliders(){
		secondStage = new Stage();
	}
	
	void resetActiveTemplate(){
		mCurrentHeight = 0.5;
		mCurrentWidth  = 0.5;
		
		mCurrentOffsetX = 0.5;
		mCurrentOffsetY = 0.5;
		
		activeTempate = null;
	}

	public void setTemplateGeomtry( final  MainBusIPInterface mainBus, Stage primaryStage){
		
		mTemplateList = mainBus.getIPFormTemplates();
		
		Label templateMatch = new Label("Template Match Settings");
		Label templateWidth = new Label("Template Witdh");
		Label templateHeight = new Label("Template Height");
		Label offsetXLabel = new Label("Offset X");
		Label offsetYLabel = new Label("Offset Y");

		

		Slider sliderWidth= new Slider(MIN_SIZE,MAX_SIZE,0.5);
		sliderWidth.setShowTickLabels(true);
		sliderWidth.setShowTickMarks(true);
		sliderWidth.setMajorTickUnit(50);
		sliderWidth.setMinorTickCount(5);
		sliderWidth.setBlockIncrement(10);
		sliderWidth.valueProperty().addListener(
				new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> arg0,
							Number arg1, Number arg2) {
						if(activeTempate != null)
							synchronized(activeTempate){

								double newWitdh = arg1.doubleValue();
								mCurrentWidth = ((mCurrentOffsetX + newWitdh/2.0) > 1.0)?1.0 :newWitdh;  
								mCurrentWidth = ((mCurrentOffsetX - newWitdh/2.0) < 0.0)?0.0 :newWitdh;
								activeTempate.setBoxWitdh(mCurrentWidth);
							}
					}
				});

		Slider sliderHeight = new Slider(MIN_SIZE,MAX_SIZE,0.5f);
		sliderHeight .setShowTickLabels(true);
		sliderHeight .setShowTickMarks(true);
		sliderHeight .setMajorTickUnit(50);
		sliderHeight .setMinorTickCount(5);
		sliderHeight .setBlockIncrement(10);
		sliderHeight.valueProperty().addListener(
				new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> arg0,
							Number arg1, Number arg2) {
						if(activeTempate != null)
							synchronized(activeTempate){
								double newHeight= arg1.doubleValue();
								mCurrentHeight = ((mCurrentOffsetY + newHeight/2.0) > 1.0)?1.0 :newHeight;  
								mCurrentHeight = ((mCurrentOffsetY - newHeight/2.0) < 0.0)?0.0 :newHeight;
								activeTempate.setBoxHeight(mCurrentHeight);
							}
					}
				});

		Slider sliderOffsetX = new Slider(MIN_SIZE,MAX_SIZE,0.5f);
		sliderOffsetX .setShowTickLabels(true);
		sliderOffsetX .setShowTickMarks(true);
		sliderOffsetX .setMajorTickUnit(50);
		sliderOffsetX .setMinorTickCount(5);
		sliderOffsetX .setBlockIncrement(10);
		sliderOffsetX.valueProperty().addListener(
				new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> arg0,
							Number arg1, Number arg2) {
						if(activeTempate != null)
							synchronized(activeTempate){
								double newOffsetX = arg1.doubleValue();
								mCurrentOffsetX = (( newOffsetX + mCurrentWidth/2.0) > 1.0)?1.0 :newOffsetX;  
								mCurrentOffsetX = ((newOffsetX - mCurrentWidth/2.0) < 0.0)?0.0 :newOffsetX;
								activeTempate.setBoxOffsetX(mCurrentOffsetX);
							}
					}
				});

		Slider sliderOffsetY = new Slider(MIN_SIZE,MAX_SIZE,0.5f);
		sliderOffsetY .setShowTickLabels(true);
		sliderOffsetY .setShowTickMarks(true);
		sliderOffsetY .setMajorTickUnit(50);
		sliderOffsetY .setMinorTickCount(5);
		sliderOffsetY .setBlockIncrement(10);
		sliderOffsetY.valueProperty().addListener(
				new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> arg0,
							Number arg1, Number arg2) {
						if(activeTempate != null)
							synchronized(activeTempate){
								double newOffsetY = arg1.doubleValue();
								mCurrentOffsetY = ((newOffsetY + mCurrentHeight/2.0) > 1.0)?1.0 :newOffsetY;  
								mCurrentOffsetY = ((newOffsetY - mCurrentHeight/2.0) < 0.0)?0.0 :newOffsetY;
								activeTempate.setBoxOffsetY(mCurrentOffsetY);
							}
					}
				});


		StackPane secondaryLayout = new StackPane();
		secondaryLayout.setAlignment(Pos.TOP_CENTER);
		secondaryLayout.getChildren().add(templateMatch);

		//Positioning
		int textStart =10;	
		templateWidth.setTranslateY(textStart+10);
		secondaryLayout.getChildren().add(templateWidth);
		sliderWidth.setTranslateY(textStart+30);
		sliderWidth.setMaxWidth(180);
		secondaryLayout.getChildren().add(sliderWidth);

		templateHeight.setTranslateY(textStart+60);
		secondaryLayout.getChildren().add(templateHeight);
		sliderHeight.setTranslateY(textStart+90);
		sliderHeight.setMaxWidth(180);
		secondaryLayout.getChildren().add(sliderHeight);

		offsetXLabel.setTranslateY(textStart+120);
		secondaryLayout.getChildren().add(offsetXLabel);
		sliderOffsetX.setTranslateY(textStart+150);
		sliderOffsetX.setMaxWidth(180);
		secondaryLayout.getChildren().add(sliderOffsetX);

		offsetYLabel.setTranslateY(textStart+160);
		secondaryLayout.getChildren().add(offsetYLabel);
		sliderOffsetY.setTranslateY(textStart+190);
		sliderOffsetY.setMaxWidth(180);
		secondaryLayout.getChildren().add(sliderOffsetY);


		Button fileSelectButton = new Button("Select Template Image"); 
		fileSelectButton.setTranslateY(textStart+240);
		secondaryLayout.getChildren().add(fileSelectButton);
		fileSelectButton.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.getExtensionFilters().addAll(
						new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif","*.pdf")
						);
				File selectedFile = fileChooser.showOpenDialog(secondStage);

				if(selectedFile != null){	
					Mat templateImage = Highgui.imread(selectedFile.getPath());
					activeTempate = new FormTemplate();
					activeTempate.setId(mIdCounter++);
					activeTempate.setTemplateImage(templateImage);
					mainBus.setCalibFormTemplate(activeTempate);
				}
			}
		});
		Button saveTemplateButton = new Button("Save Template"); 
		saveTemplateButton.setTranslateY(textStart+270);
		secondaryLayout.getChildren().add(saveTemplateButton);
		fileSelectButton.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				if(activeTempate != null){
					mTemplateList.add(activeTempate);
					resetActiveTemplate();
				}
			}
			
		});
		
		
		secondScene = new Scene(secondaryLayout, 200, 350);

		secondStage.setTitle("Template Match Calibration");
		secondStage.setScene(secondScene);

		//Set position of second window, related to primary window.
		secondStage.setX(primaryStage.getX() - 250);
		secondStage.setY(primaryStage.getY() - 100);

		secondStage.show();
	}

}
