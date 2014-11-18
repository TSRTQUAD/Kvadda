package kvaddakopter.image_processing.data_types;

import org.ejml.simple.SimpleMatrix;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class MatUtils{
	static public SimpleMatrix fromMat(Mat in_mat){
		int rows = in_mat.rows();
		int cols = in_mat.cols();
		SimpleMatrix res = new SimpleMatrix(rows, cols); 
		for(int r = 0; r < rows; r++){
			for(int c = 0; c < cols; c++){
				res.set(r, c, in_mat.get(r, c)[0]);
			}
		}
		return res;
	}

	static public Mat fromSimpleMatrix(SimpleMatrix in_mat){
		int rows = in_mat.numRows();
		int cols = in_mat.numCols();
		Mat res = new Mat(rows, cols, CvType.CV_64F); 
		for(int r = 0; r < rows; r++){
			for(int c = 0; c < cols; c++){
				res.put(r, c, in_mat.get(r, c));
			}
		}
		return res;
	}
}

