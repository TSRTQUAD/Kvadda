package kvaddakopter.image_processing.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import kvaddakopter.Mainbus.Mainbus;
import kvaddakopter.gui.controllers.TabDatorseendeController;
import kvaddakopter.image_processing.algorithms.TemplateMatch;
import kvaddakopter.image_processing.data_types.FormTemplate;
import kvaddakopter.interfaces.MainBusIPInterface;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class TemplateMatchSliders{
	static int FirstItemOffset = 20;

	static final float MIN_SIZE = 0.0f;
	static final float MAX_SIZE = 1.0f;

	static final int WINDOW_SIZE_X = 300;
	static final int WINDOW_SIZE_Y = 650;

	static final double IMAGE_SIZE_X = WINDOW_SIZE_X*0.8; 

	static final double IMAGE_OFFSET_Y = FirstItemOffset+310; 

	double mCurrentWidth  = 0.5;
	double mCurrentHeight = 0.5;

	double mCurrentOffsetX = 0.5;
	double mCurrentOffsetY = 0.5;

	Scene secondScene;
	Stage secondStage;

	int mIdCounter = 0;
	FormTemplate activeTempate = null;
	ArrayList<FormTemplate> mTemplateList;

	ImageView mImageView  = null;

	/*Used for downclocking the calibration */  
	long mPreviousTime = 0;
	final static long CALIBRATION_INTERVALL = 10;


	TabDatorseendeController mParent;
	Button mSaveTemplateButton;

	public TemplateMatchSliders(TabDatorseendeController parent){
		secondStage = new Stage();
		mParent = parent;
	}

	void resetActiveTemplate(){
		mCurrentHeight = 0.5;
		mCurrentWidth  = 0.5;

		mCurrentOffsetX = 0.5;
		mCurrentOffsetY = 0.5;

		activeTempate = null;
		flushImage();

	}
	/**
	 * 
	 * @param mainBus
	 * @param primaryStage
	 */
	public void setTemplateGeomtry( final  MainBusIPInterface mainBus, Stage primaryStage){

		StackPane secondaryLayout = new StackPane();
		mTemplateList = mainBus.getIPFormTemplates();
		addSliders(secondaryLayout);

		/* Creating save buttons */
		mSaveTemplateButton = new Button("Save Template");

		/* Set offset in y-direction */
		mSaveTemplateButton.setTranslateY(FirstItemOffset+270);

		/*Attach it to the window */
		secondaryLayout.getChildren().add(mSaveTemplateButton);

		/*Save button is disabled by default */
		mSaveTemplateButton.setDisable(true);

		/* Define action when pressed */
		mSaveTemplateButton.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				if(activeTempate != null){
					/* Store a suitable label this template: eg. "Tempalte0" */
					activeTempate.setDescription("Template" + String.valueOf((mTemplateList.size())));

					/* Store template in template list which is also referring to the template list on the MainBus */
					mTemplateList.add(activeTempate);

					/*Notify parent windows that the list has changed , so the Combo box can be refreshed ..... */
					mParent.updateFormTemplates();

					/* Reset calibration data and remove calibration template */
					resetActiveTemplate();

					/* Disable save button */
					mSaveTemplateButton.setDisable(true);

					/* TO BE REMOVED */
					mainBus.setCalibFormTemplate(null);


				}
			}

		});

		/* Create file select button */
		final Button fileSelectButton = new Button("Select Template Image");

		/* Set offset in y-direction */
		fileSelectButton.setTranslateY(FirstItemOffset+240);

		/* Attach select file button to the window */
		secondaryLayout.getChildren().add(fileSelectButton);

		/* Define action when button is pressed */
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
					/* Create an calibration template */
					activeTempate = new FormTemplate();

					/* Attach a suitable id. eg. 1337 */
					activeTempate.setId(mIdCounter++);

					/*Attach the selected image to the calibration template */
					activeTempate.setTemplateImage(templateImage);

					/* Enable save button */
					mSaveTemplateButton.setDisable(false);

					/*Do an initial calibration with defualt ROI and present result*/
					calibrateAndReloadImage(activeTempate);
				}
			}
		});

		// Add a preview of the calibrated image 
		mImageView = new ImageView();
		secondaryLayout.getChildren().add(mImageView);



		//Set Lay out stuff
		secondScene = new Scene(secondaryLayout, WINDOW_SIZE_X, WINDOW_SIZE_Y);
		secondStage.setTitle("Template Match Calibration");
		secondStage.setScene(secondScene);

		//Set position of second window, related to primary window.
		if(primaryStage!=null){
			secondStage.setX(primaryStage.getX() - 250);
			secondStage.setY(primaryStage.getY() - 100);	
		}

		secondStage.show();
	}



	private void addSliders(StackPane secondaryLayout){


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

								/* Update the ROI */
								double newWitdh = arg1.doubleValue();
								mCurrentWidth = ((mCurrentOffsetX + newWitdh/2.0) > 1.0)?mCurrentWidth :newWitdh;  
								mCurrentWidth = ((mCurrentOffsetX - newWitdh/2.0) < 0.0)?mCurrentWidth :newWitdh;
								activeTempate.setBoxWitdh(mCurrentWidth);

								/* Recalibrate and update GUI calibration image */
								calibrateAndReloadImage(activeTempate);
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
								/* Update the ROI */
								double newHeight= arg1.doubleValue();
								mCurrentHeight = ((mCurrentOffsetY + newHeight/2.0) > 1.0)?mCurrentHeight :newHeight;  
								mCurrentHeight = ((mCurrentOffsetY - newHeight/2.0) < 0.0)?mCurrentHeight :newHeight;
								activeTempate.setBoxHeight(mCurrentHeight);
								/* Recalibrate and update GUI calibration image */
								calibrateAndReloadImage(activeTempate);
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
								/* Update the ROI */
								double newOffsetX = arg1.doubleValue();
								mCurrentOffsetX = (( newOffsetX + mCurrentWidth/2.0) > 1.0)?mCurrentOffsetX :newOffsetX;  
								mCurrentOffsetX = ((newOffsetX - mCurrentWidth/2.0) < 0.0)?mCurrentOffsetX :newOffsetX;
								activeTempate.setBoxOffsetX(mCurrentOffsetX);
								/* Recalibrate and update GUI calibration image */
								calibrateAndReloadImage(activeTempate);
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
								/* Update the ROI */
								double newOffsetY = arg1.doubleValue();
								mCurrentOffsetY = ((newOffsetY + mCurrentHeight/2.0) > 1.0)?mCurrentOffsetY :newOffsetY;  
								mCurrentOffsetY = ((newOffsetY - mCurrentHeight/2.0) < 0.0)?mCurrentOffsetY :newOffsetY;
								activeTempate.setBoxOffsetY(mCurrentOffsetY);
								/* Recalibrate and update GUI calibration image */
								calibrateAndReloadImage(activeTempate);
							}
					}
				});



		secondaryLayout.setAlignment(Pos.TOP_CENTER);
		secondaryLayout.getChildren().add(templateMatch);

		//Positioning
		int textStart =10;	
		templateWidth.setTranslateY(FirstItemOffset+10);
		secondaryLayout.getChildren().add(templateWidth);
		sliderWidth.setTranslateY(FirstItemOffset+30);
		sliderWidth.setMaxWidth(180);
		secondaryLayout.getChildren().add(sliderWidth);

		templateHeight.setTranslateY(FirstItemOffset+60);
		secondaryLayout.getChildren().add(templateHeight);
		sliderHeight.setTranslateY(FirstItemOffset+90);
		sliderHeight.setMaxWidth(180);
		secondaryLayout.getChildren().add(sliderHeight);

		offsetXLabel.setTranslateY(FirstItemOffset+120);
		secondaryLayout.getChildren().add(offsetXLabel);
		sliderOffsetX.setTranslateY(FirstItemOffset+150);
		sliderOffsetX.setMaxWidth(180);
		secondaryLayout.getChildren().add(sliderOffsetX);

		offsetYLabel.setTranslateY(FirstItemOffset+160);
		secondaryLayout.getChildren().add(offsetYLabel);
		sliderOffsetY.setTranslateY(FirstItemOffset+190);
		sliderOffsetY.setMaxWidth(180);
		secondaryLayout.getChildren().add(sliderOffsetY);

	}

	private void setImg(String path){
		BufferedImage bf = null;
		try {
			bf = ImageIO.read(new File(path));
		} catch (IOException ex) {
			System.out.println("Image failed to load.");
		}
		setNewImage(bf);
	}

	private void flushImage(){
		mImageView.setImage(null);

	}

	private void setNewImage(BufferedImage bf){

		Image image = BufImg2FxImg(bf);
		mImageView.setImage(image);
		mImageView.setTranslateY(IMAGE_OFFSET_Y);
		mImageView.setFitWidth(IMAGE_SIZE_X);

		double imageRatio  = image.getHeight()/image.getWidth();
		double adjustedHeight = IMAGE_SIZE_X*imageRatio;
		mImageView.setFitHeight(adjustedHeight);


		if(adjustedHeight + IMAGE_OFFSET_Y + 20 > WINDOW_SIZE_Y){
			double windowHeight = adjustedHeight + IMAGE_OFFSET_Y + 50;
			secondScene.getWindow().setHeight(windowHeight);
		}

	}

	private void calibrateAndReloadImage(FormTemplate template){
		long currentTime = System.currentTimeMillis();
		if(currentTime - mPreviousTime > CALIBRATION_INTERVALL){
			if(template != null){
				Mat res = TemplateMatch.calibrateTemplate(template);
				BufferedImage bf = ImageConversion.mat2Img(res);
				setNewImage(bf);
			}
			mPreviousTime = currentTime;
		}

	}

	private Image BufImg2FxImg(BufferedImage bf){


		WritableImage wr = null;
		if (bf != null) {
			wr = new WritableImage(bf.getWidth(), bf.getHeight());
			PixelWriter pw = wr.getPixelWriter();
			for (int x = 0; x < bf.getWidth(); x++) {
				for (int y = 0; y < bf.getHeight(); y++) {
					pw.setArgb(x, y, bf.getRGB(x, y));
				}
			}
		}
		return wr;
	}


}
