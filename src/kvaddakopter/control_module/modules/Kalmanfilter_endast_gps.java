package kvaddakopter.control_module.modules;
import org.ejml.simple.SimpleMatrix;


/**
 * Kalmanfilter with GPS measurement as measurement updates and velocity measurement as input signal.
 *
 *
 */


public class Kalmanfilter_endast_gps {
	protected SimpleMatrix F,Gu,Gv,Q,H,P,K,S,R,HP,error,z1,z2,x;
	protected double[] states;	
	/** Construct all matrices from sampletime, variances and initial conditions,.
	 * 
	 * @param sampletime
	 * @param lambdaxdot
	 * @param lambday
	 * @param initialx
	 * @param initialxdot
	 */
	public Kalmanfilter_endast_gps(double sampletime,double lambdaxdot,double lambday,
						double initialx, double initialxdot){		
	    // Construct all matrices
		F = new SimpleMatrix(3,3,true,		1.0,	(sampletime),	0.0,
											0.0,	0.0,			1.0,
											0.0,	0.0,			1.0); 
		
		Gu = new SimpleMatrix(3,1,true,	0.0,
										1.0,
										0.0);
		
		Gv = new SimpleMatrix(3,1,true,	Math.pow(sampletime, 4)/4,
										Math.pow(sampletime, 3)/2,
														1);
		
		Q = new SimpleMatrix(1,1,true,lambdaxdot);
		R = new SimpleMatrix(1,1,true,lambday);
		H = new SimpleMatrix(1,3,true,	1.0,
										0.0,
										0,0);
		
		P = new SimpleMatrix(3,3,true,	1,0,0,
										0,1,0,
										0,0,1);       ///// Initial states!!
		HP = new SimpleMatrix(1,3);
		K = new SimpleMatrix(3,3);
		S = new SimpleMatrix(1,1);
		error = new SimpleMatrix(1,1);
		z1 = new SimpleMatrix(1,1);
		z2 = new SimpleMatrix(1,1);
		x = new SimpleMatrix(3,1,true,initialx,initialxdot,0);
	}
	
	
	
	
	/** Update x according to new GPS measurement and returns the calculated states position and velocity. 
	 * @param observation
	 * @return
	 */
	public double[] gpsmeasurementupdate(double observation){	
	    // Transform observation to matrix
        this.z1 = new SimpleMatrix(1,1,true,observation);
		//z1.print();
		//x.print();
        // error = z - H x
        this.error = z1.minus(H.mult(x));
        //error.print();
        // HP = H P
        this.HP = H.mult(P);
        //HP.print();
        // S = H P H' + R
        this.S = HP.mult(H.transpose()).plus(R);
        //S.print();
        // K = PH'S^(-1)
        this.K = P.mult(H.transpose().mult(S.pseudoInverse()));
        //K.print();
        // x = x + Ky
        this.x = x.plus(K.mult(error));
        //x.print();
        // P = (I-kH)P = P - KHP
        this.P = P.minus(K.mult(HP));
        this.P = (P.plus(P.transpose())).scale(.5);;
        states = new double[]{x.get(0),x.get(1)};
        return states;
	    }

	
	
	
	/** Update x by time and current velocity measurements. Returns calculated states.
	 * @param observation
	 * @return
	 */
	public double[] timeupdate(double observation){
		
		// Transform observation to matrix
		this.z2 = new SimpleMatrix(1,1,true,observation);
		// x = F x + Gu u
		this.x = F.mult(x).plus(Gu.mult(z2));		
		// P = F P F' + Gv Q Gv'
		//x.print();
		this.P = F.mult(P.mult(F.transpose())).plus(Gv.mult(Q.mult(Gv.transpose())));
		this.P = (P.plus(P.transpose())).scale(.5);;
        states = new double[]{x.get(0),x.get(1)};
        return states;
	}
	
	
	
	/**
	 * Print out current matrixes.
	 */
	public void print(){
	    // Construct all matrices 	
		F.print(); 
		Gu.print();
		Gv.print();
		Q.print();
		R.print();
		H.print();
		P.print();       ///// Initial states!!
		HP.print();
		K.print();
		S.print();
		error.print();
		x.print();
	}
	
	
	// Getters
	public SimpleMatrix getF() {
		return F;
	}


	public SimpleMatrix getGu() {
		return Gu;
	}


	public SimpleMatrix getQ() {
		return Q;
	}


	public SimpleMatrix getH() {
		return H;
	}


	public SimpleMatrix getP() {
		return P;
	}


	public SimpleMatrix getK() {
		return K;
	}


	public SimpleMatrix getS() {
		return S;
	}


	public SimpleMatrix getR() {
		return R;
	}


	public SimpleMatrix getHP() {
		return HP;
	}


	public SimpleMatrix getError() {
		return error;
	}


	public SimpleMatrix getZ1() {
		return z1;
	}


	public SimpleMatrix getZ2() {
		return z2;
	}


	public SimpleMatrix getX() {
		return x;
	}

	

	
	
}
	   
	
	
	