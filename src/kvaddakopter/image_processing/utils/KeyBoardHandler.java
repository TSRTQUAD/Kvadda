package kvaddakopter.image_processing.utils;

import java.util.Scanner;

import kvaddakopter.image_processing.programs.ProgramClass;

public class KeyBoardHandler extends Thread{

	ProgramClass mKeyboardListener = null;
	boolean mExternalStop = false;

	public void setListner(ProgramClass listener){
		mKeyboardListener = listener;
	}

	public void run(){
		Scanner scan= new Scanner(System.in);
		String text; 
//		int num= scan.nextInt();
		while(!mExternalStop){
			text = scan.nextLine();
			mKeyboardListener.onKeyBoardInput(text);
		}
	}

	public void safeStop(){
		mExternalStop = true;
	}
}
