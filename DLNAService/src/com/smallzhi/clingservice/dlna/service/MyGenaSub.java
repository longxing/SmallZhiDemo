package com.smallzhi.clingservice.dlna.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.fourthline.cling.model.meta.LocalService;	
import org.fourthline.cling.model.gena.CancelReason;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import org.fourthline.cling.model.meta.LocalDevice;

public class MyGenaSub {

//	LocalGENASubscription(LocalService service, Integer requestedDurationSeconds, List<URL> callbackURLs)
	private final 	int				CLOSEED	= 0;
	private final 	int				RUNNING	= 1;
	
	private LocalGENASubscription	localGENASubscription	= null;
	private LocalDevice				localDevice				= null;
	private int						state					= CLOSEED;

	
public MyGenaSub(LocalDevice localDevice) throws Exception {
	super();
	localDevice = localDevice;
	if(null == localDevice){
		return;
	}
//	LocalService[] servive = localDevice.getServices();
	LocalService localService = localDevice.getServices()[0];
	

	List<URL> urls = new ArrayList() {{
	      add(getLocalBaseURL());
	}};
	  

	localGENASubscription = new LocalGENASubscription(localService,1800,urls){

	@Override
	public void ended(CancelReason arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void established() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eventReceived() {
		// TODO Auto-generated method stub
		
	}
};
	
	
 
}


private URL getLocalBaseURL() {
	// TODO Auto-generated method stub
    try {
        return new URL("http://127.0.0.1:" + "1800" + "/");
    } catch (MalformedURLException e) {
    throw new RuntimeException(e);
    }
}


public void run(){
	
	
}
	
public int getGenaState(){
	
	return 0;
}
}
