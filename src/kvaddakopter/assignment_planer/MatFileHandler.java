package kvaddakopter.assignment_planer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.jmatio.io.MatFileReader;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLCell;
import com.jmatio.types.MLDouble;

public class MatFileHandler {
	public MatFileHandler() {
	}
	
	/**
	 * Creates a Mat-file to be loaded in Matlab by the Matlabscript. All values are obtained <br>
	 * from the input classvariable missionobject.
	 * @param missionobject
	 * @throws IOException
	 */
	public void createMatFile(String name, MissionObject missionobject) throws IOException {

		// Declare local variables
		MLCell area = new MLCell("area", new int[] {1,1});
		MLCell forbiddenarea = new MLCell("forbiddenarea", new int[] {1,1});
		ArrayList<MLArray> list = new ArrayList<MLArray>();

		//Read data and put them in ML-classes
		MLDouble mission = new MLDouble( "mission", new double[][] {{missionobject.getMissionType().getValue()}});
		MLDouble startcoordinate = new MLDouble( "startcoordinate", missionobject.getStartCoordinate() );
		MLDouble height = new MLDouble( "height", missionobject.getHeight(), 1 );
		MLDouble radius = new MLDouble( "radius", missionobject.getRadius(), 1 );

		MLDouble imageTemplate = new MLDouble("imageTemplate", new double[][] {{missionobject.getImageTemplate()}});
		MLDouble colorTemplate = new MLDouble("colorTemplate", new double[][] {{missionobject.getColorTemplate()}});
		MLDouble descriptor = new MLDouble("descriptor", new double[][] {{missionobject.getDescriptor()}});

		//Read search areas
		ArrayList<Area> searchareas = missionobject.getSearchAreaCoordinates();
		if (searchareas != null) {
			area = new MLCell("area", new int[] {1,searchareas.size()});
			for (int i = 0 ; i < searchareas.size() ; i++) {
				area.set(new MLDouble( "area" + i+1, searchareas.get(i).area ), i);
			}
		}
		else {
			area.set(new MLDouble( "area" + 1, new double[][] {{0,0}} ), 0);
		}

		//Read forbidden areas
		ArrayList<Area> forbiddenareas = missionobject.getForbiddenAreaCoordinates();
		if (forbiddenareas != null) {
			forbiddenarea = new MLCell("forbiddenarea", new int[] {1,forbiddenareas.size()});
			for (int i = 0 ; i < forbiddenareas.size() ; i++) {
				forbiddenarea.set(new MLDouble( "forbiddenarea" + i+1, forbiddenareas.get(i).area ), i);
			}
		}
		else {
			forbiddenarea.set(new MLDouble( "forbiddenarea" + 1, new double[][] {{0,0}} ), 0);
		}
		
		//Put information in array list
		list.add( mission );
		list.add( area );
		list.add( forbiddenarea );
		list.add( startcoordinate );
		list.add( height );
		list.add( radius );
		list.add( imageTemplate );
		list.add( colorTemplate );
		list.add( descriptor );

		// The Mat-file to be written is probably a mission object that are about to be saved.
		if (!name.equals("object")) {
			//Read calculated data
			MLDouble trajectory = new MLDouble("trajectory", missionobject.getTrajectory() );
			MLDouble trajectoryfullsize = new MLDouble("trajectoryfullsize", missionobject.getTrajectoryFullSize() );
			MLDouble trajectorylength = new MLDouble("trajectorylength", missionobject.getTrajectoryLength() );
			MLDouble coveragearea = new MLDouble("coveragearea", missionobject.getCoverageArea() );
			MLDouble time = new MLDouble("time", missionobject.getMissionTime() );
			MLDouble velocity = new MLDouble("velocity", missionobject.getReferenceVelocity() );
			list.add( trajectory );
			list.add( trajectoryfullsize );
			list.add( trajectorylength );
			list.add( coveragearea );
			list.add( time );
			list.add( velocity );
			
			//Write arrays to file			
			new MatFileWriter("Missions/" + name + ".mat", list );
		}
		// The Mat-file to be written only contains results obtained by the Matlab script.
		else {
			//Write arrays to file
			new MatFileWriter("src/kvaddakopter/assignment_planer/Matlab/Data/" + name + ".mat", list );
		}

	}
	
	/**
	 * Reads the Mat-file and saves the obtained results in the assigned MissionObject.
	 * @param missionobject
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public MissionObject readMatFile(String name, MissionObject missionobject) throws FileNotFoundException, IOException {

		//Save data to missionobject
		if (name.equals("results")) {
			MatFileReader MLRead = new MatFileReader("src/kvaddakopter/assignment_planer/Matlab/Data/" + name + ".mat" );
			missionobject.setTrajectory(((MLDouble) MLRead.getMLArray("trajectory")).getArray());
			missionobject.setTrajectoryFullSize(((MLDouble) MLRead.getMLArray("trajectoryfullsize")).getArray());
			missionobject.setTrajectoryLength(((MLDouble) MLRead.getMLArray("trajectorylength")).getArray());
			missionobject.setCoverageArea(((MLDouble) MLRead.getMLArray("coveragearea")).getArray());
			missionobject.setMissionTime(((MLDouble) MLRead.getMLArray("time")).getArray());
			missionobject.setReferenceVelocity(((MLDouble) MLRead.getMLArray("velocity")).getArray());
		}
		else {
			MatFileReader MLRead = new MatFileReader("Missions/" + name + ".mat" );
			
			//Set mission name
			missionobject.setMissionName(name);

			//Set mission type
			double[][] missiontype;
			missiontype = ((MLDouble) MLRead.getMLArray("mission")).getArray();
			if (missiontype[0][0] == 1) {
				missionobject.setMissionType( MissionType.AROUND_COORDINATE );
			}
			else if (missiontype[0][0] == 2) {
				missionobject.setMissionType( MissionType.ALONG_TRAJECTORY );
			}
			else if (missiontype[0][0] == 3) {
				missionobject.setMissionType( MissionType.AREA_COVERAGE );
			}
			else {
				missionobject.setMissionType( MissionType.NULL_MISSION );
			}

			//Write search areas
			MLCell tmpareas = (MLCell) MLRead.getMLArray("area");
			ArrayList<Area> searchareas = new ArrayList<Area>();
			if (tmpareas != null) {
				for (int i = 0 ; i < tmpareas.getSize() ; i++) {
					Area tmpArea = new Area();
					tmpArea.area = ((MLDouble) tmpareas.get(i)).getArray();
					searchareas.add(  tmpArea );
				}
			}
			missionobject.setSearchAreas( searchareas );

			//Write forbidden areas
			MLCell tmpforbiddenareas = (MLCell) MLRead.getMLArray("forbiddenarea");
			ArrayList<Area> forbiddenareas = new ArrayList<Area>();
			if (tmpforbiddenareas != null) {
				for (int i = 0 ; i < tmpforbiddenareas.getSize() ; i++) {
					Area tmpArea = new Area();
					tmpArea.area = ((MLDouble) tmpareas.get(i)).getArray();
					searchareas.add(  tmpArea );
				}
			}
			missionobject.setForbiddenAreas( forbiddenareas );

			//Write calculated data
			missionobject.setTrajectory(((MLDouble) MLRead.getMLArray("trajectory")).getArray());
			missionobject.setTrajectoryFullSize(((MLDouble) MLRead.getMLArray("trajectoryfullsize")).getArray());
			missionobject.setTrajectoryLength(((MLDouble) MLRead.getMLArray("trajectorylength")).getArray());
			missionobject.setCoverageArea(((MLDouble) MLRead.getMLArray("coveragearea")).getArray());
			missionobject.setMissionTime(((MLDouble) MLRead.getMLArray("time")).getArray());
			missionobject.setReferenceVelocity(((MLDouble) MLRead.getMLArray("velocity")).getArray());

			//Write all other data
			missionobject.setStartCoordinate(((MLDouble) MLRead.getMLArray("startcoordinate")).getArray());
			double[][] height = ((MLDouble) MLRead.getMLArray("height")).getArray();
			missionobject.setHeight( new double[] {height[0][0]} );
			double[][] radius = ((MLDouble) MLRead.getMLArray("radius")).getArray();
			missionobject.setHeight( new double[] {radius[0][0]} );
			double[][] imageTemplate = ((MLDouble) MLRead.getMLArray("imageTemplate")).getArray();
			missionobject.setImageTemplate( (int) imageTemplate[0][0] );
			double[][] colorTemplate = ((MLDouble) MLRead.getMLArray("colorTemplate")).getArray();
			missionobject.setColorTemplate( (int) colorTemplate[0][0] );
			double[][] descriptor = ((MLDouble) MLRead.getMLArray("descriptor")).getArray();
			missionobject.setDescriptor( (int) descriptor[0][0] );

		}
		
		return missionobject;
	}
	
	public void createMatFileFromFlightData(String name, double[][] data) throws IOException {
		MLDouble datatowrite = new MLDouble(name, data);
		ArrayList<MLArray> list = new ArrayList<MLArray>();
		list.add( datatowrite );
		new MatFileWriter(name + ".mat", list );
		
	}

}
