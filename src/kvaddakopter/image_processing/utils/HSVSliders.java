package kvaddakopter.image_processing.utils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import kvaddakopter.image_processing.data_types.ColorTemplate;
import kvaddakopter.interfaces.MainBusIPInterface;

/**
 * HSVSliders class
 * Implements functionality to modify and add custom made color templates
 */
public class HSVSliders{
		static final int MIN_HUE = 0;
		static final int MAX_HUE = 179;
		
		static final int MIN_SAT = 0;
		static final int MAX_SAT = 255;
		
		static final int MIN_VAL = 0;
		static final int MAX_VAL = 400;
		
		Scene secondScene;
		Stage secondStage;
	/**
	 * Constructs the stage
	 */
	public HSVSliders(){
		secondStage = new Stage();
	}
		
	/**
	 * Adds buttons and sliders to the stage associated with this object
	 * 6 sliders: huewLow, hueHigh, satLow, satHigh, valLow, valHigh
	 * Sets calib template in mainbus and modifies this
	 * 
	 * To show the result the Image processing has to threshold image according to the calib template and return the
	 * thresholded image
	 * 
	 * @param mainbus Holds the calib template and template list to add new templates to
	 * @param primaryStage The stage which the HSVSliders popup from
	 */
	public void setHSVChannels( final MainBusIPInterface mainbus, Stage primaryStage){
		final ColorTemplate template = new ColorTemplate();
		mainbus.setIPCalibTemplate(template);
		
		Label secondLabel = new Label("HSV Channels");
		Label hueLowLabel = new Label("HUE LOW");
		Label hueHighLabel = new Label("HUE HIGH");
		Label satLowLabel = new Label("SAT LOW");
		Label satHighLabel = new Label("SAT HIGH");
		Label valLowLabel = new Label("VAL LOW");
		Label valHighLabel = new Label("VAL HIGH");
		
		Slider sliderHueLow = new Slider(MIN_HUE,MAX_HUE,30);
		sliderHueLow .setShowTickLabels(true);
		sliderHueLow .setShowTickMarks(true);
		sliderHueLow .setMajorTickUnit(50);
		sliderHueLow .setMinorTickCount(5);
		sliderHueLow .setBlockIncrement(10);
		sliderHueLow.valueProperty().addListener(
				new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> arg0,
							Number arg1, Number arg2) {
						synchronized(template){
							template.setHueLow(arg1.intValue());
						}
					}
					});
		
		Slider sliderHueHigh = new Slider(MIN_HUE,MAX_HUE,70);
		sliderHueHigh .setShowTickLabels(true);
		sliderHueHigh .setShowTickMarks(true);
		sliderHueHigh .setMajorTickUnit(50);
		sliderHueHigh .setMinorTickCount(5);
		sliderHueHigh .setBlockIncrement(10);
		sliderHueHigh.valueProperty().addListener(
				new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> arg0,
							Number arg1, Number arg2) {
						synchronized(template){
							template.setHueHigh(arg1.intValue());
						}
					}
					});
		
		Slider sliderSatLow = new Slider(MIN_SAT,MAX_SAT,30);
		sliderSatLow .setShowTickLabels(true);
		sliderSatLow .setShowTickMarks(true);
		sliderSatLow .setMajorTickUnit(50);
		sliderSatLow .setMinorTickCount(5);
		sliderSatLow .setBlockIncrement(10);
		sliderSatLow.valueProperty().addListener(
				new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> arg0,
							Number arg1, Number arg2) {
						synchronized(template){
							template.setSatLow(arg1.intValue());
						}
					}
					});
		
		Slider sliderSatHigh = new Slider(MIN_SAT,MAX_SAT,70);
		sliderSatHigh .setShowTickLabels(true);
		sliderSatHigh .setShowTickMarks(true);
		sliderSatHigh .setMajorTickUnit(50);
		sliderSatHigh .setMinorTickCount(5);
		sliderSatHigh .setBlockIncrement(10);
		sliderSatHigh.valueProperty().addListener(
				new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> arg0,
							Number arg1, Number arg2) {
						synchronized(template){
							template.setSatHigh(arg1.intValue());
						}
					}
					});
		
		Slider sliderValLow = new Slider(MIN_VAL,MAX_VAL,30);
		sliderValLow .setShowTickLabels(true);
		sliderValLow .setShowTickMarks(true);
		sliderValLow .setMajorTickUnit(50);
		sliderValLow .setMinorTickCount(5);
		sliderValLow .setBlockIncrement(10);
		sliderValLow.valueProperty().addListener(
				new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> arg0,
							Number arg1, Number arg2) {
						synchronized(template){
							template.setValLow(arg1.intValue());
						}
					}
					});
		
		Slider sliderValHigh = new Slider(MIN_VAL,MAX_VAL,70);
		sliderValHigh .setShowTickLabels(true);
		sliderValHigh .setShowTickMarks(true);
		sliderValHigh .setMajorTickUnit(50);
		sliderValHigh .setMinorTickCount(5);
		sliderValHigh .setBlockIncrement(10);
		sliderValHigh.valueProperty().addListener(
				new ChangeListener<Number>() {
					@Override
					public void changed(ObservableValue<? extends Number> arg0,
							Number arg1, Number arg2) {
						synchronized(template){
							template.setValHigh(arg1.intValue());
						}
					}
					});
		
		Button addTemplateBtn = new Button();
		addTemplateBtn.setText("Add template");
		addTemplateBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				mainbus.addIPColorTemplate(new ColorTemplate(template));
			}
		});
		
		Button closeBtn = new Button();
		closeBtn.setText("Finnished");
		closeBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				secondStage.close();
			}
		});
        
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.setAlignment(Pos.TOP_CENTER);
        secondaryLayout.getChildren().add(secondLabel);
        
        //Positioning
        hueLowLabel.setTranslateY(20);
        secondaryLayout.getChildren().add(hueLowLabel);
        sliderHueLow.setTranslateY(35);
        sliderHueLow.setMaxWidth(180);
        secondaryLayout.getChildren().add(sliderHueLow);
        
        hueHighLabel.setTranslateY(65);
        secondaryLayout.getChildren().add(hueHighLabel);
        sliderHueHigh.setTranslateY(80);
        sliderHueHigh.setMaxWidth(180);
        secondaryLayout.getChildren().add(sliderHueHigh);
        
        satLowLabel.setTranslateY(110);
        secondaryLayout.getChildren().add(satLowLabel);
        sliderSatLow.setTranslateY(125);
        sliderSatLow.setMaxWidth(180);
        secondaryLayout.getChildren().add(sliderSatLow);
        
        satHighLabel.setTranslateY(155);
        secondaryLayout.getChildren().add(satHighLabel);
        sliderSatHigh.setTranslateY(170);
        sliderSatHigh.setMaxWidth(180);
        secondaryLayout.getChildren().add(sliderSatHigh);
        
        valLowLabel.setTranslateY(200);
        secondaryLayout.getChildren().add(valLowLabel);
        sliderValLow.setTranslateY(215);
        sliderValLow.setMaxWidth(180);
        secondaryLayout.getChildren().add(sliderValLow);
        
        valHighLabel.setTranslateY(245);
        secondaryLayout.getChildren().add(valHighLabel);
        sliderValHigh.setTranslateY(265);
        sliderValHigh.setMaxWidth(180);
        secondaryLayout.getChildren().add(sliderValHigh);
        
        addTemplateBtn.setTranslateY(300);
		secondaryLayout.getChildren().add(addTemplateBtn);
        
        closeBtn.setTranslateY(330);
		secondaryLayout.getChildren().add(closeBtn);
        
         
        secondScene = new Scene(secondaryLayout, 200, 370);

        secondStage.setTitle("HSVCalibration");
        secondStage.setScene(secondScene);
         
        //Set position of second window, related to primary window.
        secondStage.setX(primaryStage.getX() + 550);
        secondStage.setY(primaryStage.getY());

        secondStage.show();
	}

}
