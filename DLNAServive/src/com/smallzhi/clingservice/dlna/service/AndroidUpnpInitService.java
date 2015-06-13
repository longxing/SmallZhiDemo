package com.smallzhi.clingservice.dlna.service;



import java.util.UUID;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.gena.LocalGENASubscription;
import org.fourthline.cling.model.gena.RemoteGENASubscription;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.support.avtransport.AVTransportException;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.lastchange.LastChangeAwareServiceManager;

import android.R.string;
import android.bluetooth.BluetoothClass.Device;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.iii360.sup.common.utl.LogManager;
import com.iii360.sup.common.utl.ShellUtils;
import com.smallzhi.clingservice.MyApplication;
import com.smallzhi.clingservice.util.Constants;
import com.smallzhi.clingservice.util.DlnaReceiver;
import com.smallzhi.clingservice.util.WifiStateReceiver;
import com.smallzhi.clingservice.media.IPlayService;
import com.smallzhi.clingservice.media.MyMediaPlayerService;
import com.smallzhi.clingservice.media.PlayServiceGetter;

public class AndroidUpnpInitService extends AndroidUpnpServiceImpl implements PlayServiceGetter, IService {

	private final String MODEL_NAME = "MediaRender";
	private final String MODEL_DESCRIPTION = "MediaRender for SmallZhi";
	private final String MODEL_NUMBER = "V1.0";

	private static final String TAG = "SmallZhiDLNA";
	
	private DlanUpnpBinder mDlanUpnpBinder = new DlanUpnpBinder();

	private ServiceManager<MyAVTransportService> mAVTransportManager = null;
	private ServiceManager<MyRendererControlService> mRenderingControl = null;
	private LocalDevice mLocalRendererDevice = null;
	private IPlayService mPlayService = null;
	private MyMediaPlayerService mediaPlayerService = null;
	
	WifiStateReceiver mWifiMonitor = null;
	DlnaReceiver	  mDlnaReceiver = null;
	

	//SmallZhi DLNA error code
	private int DLNA_SUCESS			=	 0;
	private int DLNA_ERROR			=	-1;
	
	private int MAX_DEVICE_TIME		=	10;
	
	
	private String mUUID;
	
	
	@Override
	protected UpnpServiceConfiguration createConfiguration() {
		// TODO Auto-generated method stub
		return new AndroidUpnpServiceConfiguration() {

			
			
			@Override
			public int getRegistryMaintenanceIntervalMillis() {
				// TODO Auto-generated method stub
				return 7000;
				
			}
		

			//If you return a non-zero value, Cling will send alive NOTIFY messages repeatedly with the given interval
//			@Override public int getAliveIntervalMillis() { 
//				return 5000; 
//		    }
	
			//If you are not writing a control point but a server application, you can return null in the 
			//getExclusiveServiceTypes() method. This will disable discovery completely, now all device and 
			//service advertisements are dropped as soon as they are received.			
			@Override
			public ServiceType[] getExclusiveServiceTypes() {
				// TODO Auto-generated method stub
				return new ServiceType[] { new UDAServiceType("AVTransport"), new UDAServiceType("ContentDirectory"), new UDAServiceType("RenderingControl") };
//				return null;
			}
		};
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// bindService();
		// createDevice();
		// registry();
		
		//creat wifi monitor for dlna session
		mWifiMonitor = new WifiStateReceiver();	
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.net.wifi.RSSI_CHANGED");
		intentFilter.addAction("android.net.wifi.STATE_CHANGE");
		intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
		registerReceiver(mWifiMonitor,intentFilter);
		
		//creat volume monitor for dlan seesion
		mDlnaReceiver = new DlnaReceiver();
		IntentFilter dlnaFiletr = new IntentFilter();
		dlnaFiletr.addAction(Constants.IKEY_VOLUME_CHANGED);
		registerReceiver(mDlnaReceiver,dlnaFiletr);
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		flags = START_STICKY;
		bindService();
		createDevice();
		mediaPlayerService.setManager(mAVTransportManager, mRenderingControl);
		registry();
		return super.onStartCommand(intent, flags, startId);
	}

	private void registry() {

		if (upnpService == null) {
			LogManager.e("mUpnpService is null");
			return;
		} else {
			MyApplication.getInstance().setUpnpService(upnpService);
		}
		upnpService.getRegistry().removeAllLocalDevices();
		
		if(null != mDlanUpnpBinder.getLocalRenderer()){
			upnpService.getRegistry().addDevice(mDlanUpnpBinder.getLocalRenderer());
		}
		else {
			LogManager.e("LocalRenderer is null");
			return;
		}
		
		upnpService.getRegistry().addListener(new RegistryListener() {
			@Override
			public void remoteDeviceUpdated(Registry arg0, RemoteDevice arg1) {
				// TODO Auto-generated method stub
				LogManager.i("remoteDeviceUpdated");
			}

			@Override
			public void remoteDeviceRemoved(Registry arg0, RemoteDevice arg1) {
				// TODO Auto-generated method stub
				LogManager.i("remoteDeviceRemoved");
				sendBroadcast(new Intent(MyAVTransportService.DLAN_MUSIC_TO_MAIN_STOP_ACTION));
			}

			@Override
			public void remoteDeviceDiscoveryStarted(Registry arg0, RemoteDevice arg1) {
				// TODO Auto-generated method stub
				LogManager.i("remoteDeviceDiscoveryStarted");
			}

			@Override
			public void remoteDeviceDiscoveryFailed(Registry arg0, RemoteDevice arg1, Exception arg2) {
				// TODO Auto-generated method stub
				LogManager.i("remoteDeviceDiscoveryFailed");
			}

			@Override
			public void remoteDeviceAdded(Registry arg0, RemoteDevice arg1) {
				// TODO Auto-generated method stub
				LogManager.i("remoteDeviceAdded");
			}

			@Override
			public void localDeviceRemoved(Registry arg0, LocalDevice arg1) {
				// TODO Auto-generated method stub
				LogManager.i("localDeviceRemoved");
			}

			@Override
			public void localDeviceAdded(Registry arg0, LocalDevice arg1) {
				// TODO Auto-generated method stub
				LogManager.i("localDeviceAdded");
			}

			@Override
			public void beforeShutdown(Registry arg0) {
				// TODO Auto-generated method stub
				LogManager.i("beforeShutdown");
			}

			@Override
			public void afterShutdown() {
				// TODO Auto-generated method stub
				LogManager.i("afterShutdown");
			}
		});

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return mDlanUpnpBinder;

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (upnpService != null) {
			upnpService.getRegistry().removeAllLocalDevices();
			upnpService.shutdown();
			return;
		}
		
		try {  
		    unregisterReceiver(mWifiMonitor); 
		    unregisterReceiver(mDlnaReceiver);
		} catch (IllegalArgumentException e) {  
		    if (e.getMessage().contains("Receiver not registered")) {  
		        // Ignore this exception. This is exactly what is desired  
		    } else {  
		        // unexpected, re-throw  
		        throw e;  
		    }  
		}

	}

	// ******根据upnp协议必须实现的四个服务*******//
	private void createDevice() {

		LogManager.d(TAG,"create Devic start");
		
		DeviceType typeRenderer = new UDADeviceType("MediaRenderer", 1);

		// create device's UDN.
		String strUUId = mediaPlayerService.GetDLNAUuid();	
		UUID uuid = UUID.fromString(strUUId);
		UDN mRendererUDN = new UDN(uuid);
		LogManager.d(TAG,"Device ID =  " + strUUId);
		
		mUUID = strUUId;
		
		String sn = ShellUtils.readSerialNumber();
		if(sn.length()<7){
			LogManager.e(TAG,"sn leng not fit need ,return");
			return;
		}
		
		String serviceName = Constants.SERVICE_NAME + sn.substring(4,sn.length()-1);
		LogManager.d(TAG,"serviceName=" + serviceName);
		DeviceDetails details = new DeviceDetails(serviceName, new ManufacturerDetails(android.os.Build.MANUFACTURER), new ModelDetails(MODEL_NAME, MODEL_DESCRIPTION, MODEL_NUMBER));
		
		// set connect type
		LocalService<MyConnectionManagerService> avConnectionService = new AnnotationLocalServiceBinder().read(MyConnectionManagerService.class);
		avConnectionService.setManager(new DefaultServiceManager<MyConnectionManagerService>(avConnectionService) {
			@Override
			protected MyConnectionManagerService createServiceInstance() throws Exception {
				return new MyConnectionManagerService();
			}
		});

		// create device's loacl service.
		LocalService<MyContentDirectoryService> contentService = new AnnotationLocalServiceBinder().read(MyContentDirectoryService.class);
		contentService.setManager(new DefaultServiceManager<MyContentDirectoryService>(contentService, MyContentDirectoryService.class) {
			@Override
			protected MyContentDirectoryService createServiceInstance() throws Exception {
				return new MyContentDirectoryService();
			}
		});

		
		LocalService<MyAVTransportService> transportService = new AnnotationLocalServiceBinder().read(MyAVTransportService.class);			
		mAVTransportManager = new DefaultServiceManager<MyAVTransportService>(transportService, MyAVTransportService.class) {
			@Override
			protected MyAVTransportService createServiceInstance() throws Exception {
				return new MyAVTransportService(AndroidUpnpInitService.this, AndroidUpnpInitService.this);
			}
		};
		transportService.setManager(mAVTransportManager);
		
		
		LocalService<MyRendererControlService> renderService = new AnnotationLocalServiceBinder().read(MyRendererControlService.class);
		mRenderingControl = new DefaultServiceManager<MyRendererControlService>(renderService, MyRendererControlService.class) {
			@Override
			protected MyRendererControlService createServiceInstance() throws Exception {
				return new MyRendererControlService(AndroidUpnpInitService.this, AndroidUpnpInitService.this);
			}
		};
		renderService.setManager(mRenderingControl);

		

		// create a loacl device for render
		try {
			mLocalRendererDevice = new LocalDevice(new DeviceIdentity(mRendererUDN, MAX_DEVICE_TIME), typeRenderer, details, new LocalService[] { renderService, avConnectionService, transportService });
		} catch (ValidationException e) {
			e.printStackTrace();
		}
		
		LogManager.d(TAG,"create Devic end");
//		runLastChangePushThread();
	}
	
	
	public int DlnaStart(String deviceId, int deviceType, int deviceCapcity){
		
		createDevice();
		registry();	
		return DLNA_SUCESS;
	}

	public void DlnaStop(){

		if (upnpService != null) {
			upnpService.getRegistry().removeAllLocalDevices();
			upnpService.shutdown();
			return;
		}
		
	}

	private void runLastChangePushThread() {
		new Thread() {
			private UnsignedIntegerFourBytes[] unsignedIntegerFourBytes = new UnsignedIntegerFourBytes[10];

			@Override
			public void run() {
				try {
					LogManager.d(TAG,"runLastChangePushThread start");
					
					while (true) {
						
						//if(true == MyAVTransportService.mState){
	//					LogManager.e(TAG,"runLastChangePushThread state " );;
							
				//    	lock();
				    	try {
				    		mAVTransportManager.getImplementation().getLastChange().fire(mAVTransportManager.getImplementation().getPropertyChangeSupport());
				    		mRenderingControl.getImplementation().getLastChange().fire(mRenderingControl.getImplementation().getPropertyChangeSupport());
				    	} finally {
			//	    		unlock();
				    	}			
						Thread.sleep(500);
					}
				} catch (Exception e) {
					LogManager.e(TAG,"runLastChangePushThread error");
					e.printStackTrace();
				}
			}
		}.start();
	}

	public class DlanUpnpBinder extends Binder {
		public UpnpService getAndroidUpnpService() {
			return upnpService != null ? upnpService : null;
		}

		public LocalDevice getLocalRenderer() {
			return mLocalRendererDevice != null ? mLocalRendererDevice : null;
		}
	}

	@Override
	public void bindService() {
		// TODO Auto-generated method stub
		LogManager.d(TAG,"bind mediaplayer.........");
		mediaPlayerService = MyMediaPlayerService.getInstance(AndroidUpnpInitService.this);
		mPlayService = mediaPlayerService;
	}
	
	
	@Override
	public void unBindService() {
		// TODO Auto-generated method stub

	}

	@Override
	public IPlayService getPlayService() {
		// TODO Auto-generated method stub
		if (mPlayService == null) {
			LogManager.e(TAG,"bind music server error");
		} else {
//			LogManager.d(TAG,"bind music server sucess");
		}
		return mPlayService;
	}

}
