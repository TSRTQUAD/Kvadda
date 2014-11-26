package kvaddakopter.interfaces;

public interface IPAndGUIInterface extends MainBusGUIInterface, MainBusIPInterface {

	public void toggleController();
	public void setShouldStart(boolean b);

}
