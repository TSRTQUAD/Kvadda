package Mainbus;

/**
 * Mainbus class
 * This is the main thread of the project
 * 
 * How to make new process
 * 1) Implement classes as runnables, see MyRunnable for setup
 * 2) Create thread with your runnable
 * 3) start thread
 * 
 * Ex)
 * MyRunnable myRunnable = new MyRunnable(other variables,mainbus);
 *       
 * Thread t = new Thread(myRunnable);
 * t.start();   
 */
public class Mainbus{
	
	private int var;
	
	public static void main(String[] args) {
		Mainbus mainbus = new Mainbus();
		
        MyRunnable myRunnable = new MyRunnable(1,mainbus);
        MyRunnable myRunnable2 = new MyRunnable(2,mainbus);
        
        Thread t = new Thread(myRunnable);
        t.start();
        
        Thread t2 = new Thread(myRunnable2);
        t2.start();
        
        while(true){
        	//Thread.sleep(1);
        	synchronized(mainbus){
            	System.out.println(mainbus.getVar());
        	}
        }
	}
	
	public synchronized void setVar(int i){
		var = i;
	}
	
	public synchronized int getVar(){
		return var;
	}
}
