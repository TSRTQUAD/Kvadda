package kvaddakopter.Mainbus;

import kvaddakopter.Mainbus.*;
//import Mainbus.RunnableListener;

public class MyRunnable2 implements Runnable {
	
	//private volatile Container container;
	private boolean condIsActive = false;
//	private RunnableListener mRunnableListener;
    private int mThreadId;
    private volatile Mainbus mMainbus;

    public MyRunnable2(int threadid,Mainbus mainbus) {
        mThreadId = threadid;
        mMainbus = mainbus;
    }
    /*
	public void setRunnableListener(RunnableListener listener){
		mRunnableListener = listener;		
	}*/
	    
	@Override
	public void run() {
		waitOnCondVar();
		System.out.println("Stopped waiting");
	}
	
	public void waitOnCondVar(){
		while (!mMainbus.getCondVar())
			try{
				synchronized(mMainbus){
					mMainbus.wait();
				}

			}
		catch (InterruptedException e) {}
	}

}
