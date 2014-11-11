package kvaddakopter.control_module.modules;
import org.ejml.simple.SimpleMatrix;



public class Kalmanfilter {

	protected SimpleMatrix F,G,Q,H,P,K,S,R,HP,error,z1,z2,x;
	
	
	// Construct all matrices from initial conditions, and variances
	public Kalmanfilter(double sampletime,double lambdax,double lambdaxdot,double lambday,
						double initialx, double initialxdot){		
	    // Construct all matrices 	
		F = new SimpleMatrix(2,2,true, ((double) 1), (((double)1) + sampletime), ((double) 0),((double) 0)); 
		G = new SimpleMatrix(2,1,true,((double)0),((double)1));
		Q = new SimpleMatrix(2,2,true,lambdax,lambdaxdot);
		R = new SimpleMatrix(1,1,true,lambday);
		H = new SimpleMatrix(1,2,true,((double)1),((double)0));
		P = new SimpleMatrix(2,2,true,1,0,0,1);       ///// Initial states!!
		HP = new SimpleMatrix(1,2);
		K = new SimpleMatrix(2,2);
		S = new SimpleMatrix(1,1);
		error = new SimpleMatrix(1,1);
		z1 = new SimpleMatrix(1,1);
		z2 = new SimpleMatrix(1,1);
		x = new SimpleMatrix(2,1,true,initialx,initialxdot);
	}
	
	
	
	
	// Update x according to new observations. 
	public void measurementupdate(double observation){	
	    // Transform observation to matrix
        this.z1 = new SimpleMatrix(1,1,true,observation);

        // error = z - H x
        this.error = z1.minus(H.mult(x));
        
        // HP = H P
        this.HP = H.mult(P);
        // S = H P H' + R
        this.S = HP.mult(H.transpose()).plus(R);

        // K = PH'S^(-1)
        this.K = P.mult(H.transpose().mult(S.invert()));

        // x = x + Ky
        this.x = x.plus(K.mult(error));

        // P = (I-kH)P = P - KHP
        P = P.minus(K.mult(HP));
	    }

	
	
	// Update x by time.	
	public void timeupdate(double observation){
		// x = F x + G u
		this.x = F.mult(x).plus(G.mult(z2));
		// P = F P F' + G Q G'
		this.P = F.mult(P.mult(F.transpose())).plus(G.mult(Q.mult(G.transpose())));
	}
	
	
	
	// Getters
	public SimpleMatrix getF() {
		return F;
	}


	public SimpleMatrix getG() {
		return G;
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
	   
	
	
	