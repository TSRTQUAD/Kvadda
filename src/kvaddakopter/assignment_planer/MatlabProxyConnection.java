package kvaddakopter.assignment_planer;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;
import matlabcontrol.MatlabProxyFactoryOptions.Builder;

/*
 * Sets up an Matlab Proxy, which can be called for function calls to Matlab.
 */

public class MatlabProxyConnection {
	MatlabProxy proxy;

	public void startMatlab(boolean quiet) throws MatlabConnectionException{
		//Create a proxy, which will be used to control MATLAB
		if (quiet) {
			//Set the options to quiet so Matlab can run in the background.
			Builder buildoptions = new MatlabProxyFactoryOptions.Builder();
			buildoptions.setHidden(true);
			MatlabProxyFactoryOptions options = buildoptions.build();

			MatlabProxyFactory factory = new MatlabProxyFactory(options);
			this.proxy = factory.getProxy();
		}
		else {
			MatlabProxyFactory factory = new MatlabProxyFactory();
			this.proxy = factory.getProxy();
		}
	}

	public void terminateMatlab() {
		//Terminate Matlab
		//proxy.exit();

		//Disconnect the proxy from MATLAB
		this.proxy.disconnect();
	}

	public MatlabProxy getMatlabProxy() {
		return this.proxy;
	}
}
