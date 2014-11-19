package kvaddakopter.image_processing.utils;

import org.opencv.core.Mat;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import kvaddakopter.image_processing.data_types.ColorTemplate;

public class HSVSliders{
		static final int MAX_HSV = 255;
		static final int MIN_HSV = 0;
		
		static final int MIN_SAT = 255;
		static final int MAX_SAT = 0;
		
		static final int MIN_VAL = 255;
		static final int MAX_VAL = 0;
		
	public HSVSliders(){
	}
		
	public static void setHSVChannels( final  ColorTemplate template, Stage primaryStage){
		Label secondLabel = new Label("HSV Channels");
		Label hueLowLabel = new Label("HUE LOW");
		Label hueHighLabel = new Label("HUE HIGH");
		Label satLowLabel = new Label("SAT LOW");
		Label satHighLabel = new Label("SAT HIGH");
		Label valLowLabel = new Label("VAL LOW");
		Label valHighLabel = new Label("VAL HIGH");
		
		Slider sliderHueLow = new Slider(0,255,30);
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
		Slider sliderHueHigh = new Slider(0,255,70);
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
		Slider sliderSatLow = new Slider(0,255,30);
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
		Slider sliderSatHigh = new Slider(0,255,70);
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
		Slider sliderValLow = new Slider(0,255,30);
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
		Slider sliderValHigh = new Slider(0,255,70);
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
        
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.setAlignment(Pos.TOP_CENTER);
        secondaryLayout.getChildren().add(secondLabel);
        
        hueLowLabel.setTranslateY(20);
        secondaryLayout.getChildren().add(hueLowLabel);
        sliderHueLow.setTranslateY(30);
        secondaryLayout.getChildren().add(sliderHueLow);
        
        hueHighLabel.setTranslateY(50);
        secondaryLayout.getChildren().add(hueHighLabel);
        sliderHueHigh.setTranslateY(60);
        secondaryLayout.getChildren().add(sliderHueHigh);
        
        satLowLabel.setTranslateY(80);
        secondaryLayout.getChildren().add(satLowLabel);
        sliderSatLow.setTranslateY(90);
        secondaryLayout.getChildren().add(sliderSatLow);
        
        satHighLabel.setTranslateY(110);
        secondaryLayout.getChildren().add(satHighLabel);
        sliderSatHigh.setTranslateY(120);
        secondaryLayout.getChildren().add(sliderSatHigh);
        
        valLowLabel.setTranslateY(140);
        secondaryLayout.getChildren().add(valLowLabel);
        sliderValLow.setTranslateY(150);
        secondaryLayout.getChildren().add(sliderValLow);
        
        valHighLabel.setTranslateY(170);
        secondaryLayout.getChildren().add(valHighLabel);
        sliderValHigh.setTranslateY(180);
        secondaryLayout.getChildren().add(sliderValHigh);
        
         
        Scene secondScene = new Scene(secondaryLayout, 200, 210);

        Stage secondStage = new Stage();
        secondStage.setTitle("Second Stage");
        secondStage.setScene(secondScene);
         
        //Set position of second window, related to primary window.
        secondStage.setX(primaryStage.getX() - 250);
        secondStage.setY(primaryStage.getY() - 100);

        secondStage.show();
	}

}
