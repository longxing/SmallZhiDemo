package com.iii360.base.inf;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.Toast;

import com.base.platform.IPlatform;
import com.iii360.base.common.utl.BaseContext;
import com.iii360.base.common.utl.ControllerVolume;
import com.iii360.base.common.utl.LogManager;
import com.iii360.base.common.utl.MediaPlayerUtil;
import com.iii360.base.common.utl.TaskSchedu;
import com.iii360.base.inf.ITTSController.ITTSStateListener;
import com.iii360.base.inf.parse.ICommandEngine;
import com.iii360.base.inf.recognise.IRecogniseSystem;
//hefeng begin
import com.iii360.sup.common.utl.TimerTicker;

/**
 * 别new这个对象，不然会死的很惨
 * 
 * @author jushag
 * 
 */
public class BasicServiceUnion implements IRecogniseSensitive, ITTSSensitive, ICommandEngineSensitive {

	private ITTSController mTTSController;
	private IRecogniseSystem mRecSystem;
	private ICommandEngine mCommandEngine;
	private IWidgetControllor mWidgetControllor;
	private IViewContainer mViewContainer;
	private ControlInterface mControlInterface;
	private TaskSchedu mTaskSchedu;
	private BaseContext mBaseContext;
	private Handler mHandler;
	private IMainThreadUtil mainThreadUtil;
	private IMediaInterface mMediaHanlder;

	public void setMediaInterface(IMediaInterface mediaInterface) {

		mMediaHanlder = mediaInterface;
	}

	public IMediaInterface getMediaInterface() {
		return mMediaHanlder;
	}

	public IMainThreadUtil getMainThreadUtil() {
		return mainThreadUtil;
	}

	public void setMainThreadUtil(IMainThreadUtil mainThreadUtil) {
		this.mainThreadUtil = mainThreadUtil;
	}

	public Handler getHandler() {
		return mHandler;
	}

	public void setHandler(Handler mHandler) {
		this.mHandler = mHandler;
	}

	public BaseContext getBaseContext() {
		return mBaseContext;
	}

	public void setBaseContext(BaseContext baseContext) {
		this.mBaseContext = baseContext;
	}

	public TaskSchedu getTaskSchedu() {
		return mTaskSchedu;
	}

	public void setTaskSchedu(TaskSchedu mTaskSchedu) {
		this.mTaskSchedu = mTaskSchedu;
	}

	public ControlInterface getControlInterface() {
		return mControlInterface;
	}

	public void setControlInterface(ControlInterface mControlInterface) {
		this.mControlInterface = mControlInterface;
	}

	@Override
	public void setCommandEngine(ICommandEngine commandEngine) {
		// TODO Auto-generated method stub
		mCommandEngine = commandEngine;
	}

	@Override
	public void setTTSController(ITTSController ttsController) {
		// TODO Auto-generated method stub
		mTTSController = ttsController;
	}

	@Override
	public void setRecogniseSystem(IRecogniseSystem recSystem) {
		// TODO Auto-generated method stub
		mRecSystem = recSystem;
	}

	@Override
	public ICommandEngine getCommandEngine() {
		// TODO Auto-generated method stub
		return mCommandEngine;
	}

	@Override
	public ITTSController getTTSController() {
		// TODO Auto-generated method stub
		return mTTSController;
	}

	@Override
	public IRecogniseSystem getRecogniseSystem() {
		// TODO Auto-generated method stub
		return mRecSystem;
	}

	public void setWidgetController(IWidgetControllor widgetController) {
		// TODO Auto-generated method stub
		mWidgetControllor = widgetController;
	}

	public IWidgetControllor getWidgetController() {
		// TODO Auto-generated method stub
		return mWidgetControllor;
	}

	public void setViewContainer(IViewContainer viewContainer) {
		// TODO Auto-generated method stub
		mViewContainer = viewContainer;
	}

	public IViewContainer getViewContainer() {
		// TODO Auto-generated method stub
		return mViewContainer;
	}

}
