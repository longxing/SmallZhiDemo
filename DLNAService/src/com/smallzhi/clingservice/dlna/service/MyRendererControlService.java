package com.smallzhi.clingservice.dlna.service;

import java.lang.reflect.Method;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.support.renderingcontrol.AbstractAudioRenderingControl;
import org.fourthline.cling.support.renderingcontrol.RenderingControlException;

import android.content.Context;
import android.media.AudioManager;

import com.iii360.sup.common.utl.LogManager;
import com.smallzhi.clingservice.util.Constants;
import com.smallzhi.clingservice.util.VolumeCotroller;
import com.smallzhi.clingservice.media.PlayServiceGetter;

public class MyRendererControlService extends AbstractAudioRenderingControl {
    private PlayServiceGetter mGetter;
    private AudioManager mAudioManager;
    private Context context;

    private static final String TAG = "SmallZhiDLNA ControlService";
    
    public MyRendererControlService(PlayServiceGetter getter, Context context) {
        // TODO Auto-generated constructor stub
        mGetter = getter;
        this.context= context;
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    @UpnpAction(out = @UpnpOutputArgument(name = "CurrentMute", stateVariable = "Mute"))
    public boolean getMute(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0, @UpnpInputArgument(name = "Channel") String arg1)
            throws RenderingControlException {
        // TODO Auto-generated method stub
        LogManager.d(TAG,"getMute :" + arg1);
        
        boolean isMute = false;
        try {
            Method method = AudioManager.class.getMethod("isStreamMute", int.class);
            isMute = ((Boolean) method.invoke(mAudioManager, Constants.STREAM_TYPE)).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isMute;
    }

    @Override
    @UpnpAction(out = @UpnpOutputArgument(name = "CurrentVolume", stateVariable = "Volume"))
    public UnsignedIntegerTwoBytes getVolume(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0,
            @UpnpInputArgument(name = "Channel") String arg1) throws RenderingControlException {
        // TODO Auto-generated method stub

    	LogManager.d(TAG,"getVolume :" + arg1);
        int volume = mAudioManager.getStreamVolume(Constants.STREAM_TYPE)*100/VolumeCotroller.getMaxVolume(context);
        
        LogManager.d(TAG,"getVolume/volume :" + volume);
        return new UnsignedIntegerTwoBytes(volume);
    }

    @Override
    @UpnpAction
    public void setMute(@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0, @UpnpInputArgument(name = "Channel") String arg1,
            @UpnpInputArgument(name = "DesiredMute", stateVariable = "Mute") boolean desiredMute) throws RenderingControlException {
        // TODO Auto-generated method stub
    	LogManager.d(TAG,"setMute :" + arg1 + "||arg2=" + desiredMute);
        mAudioManager.setStreamMute(Constants.STREAM_TYPE, desiredMute);
    }

    @Override
    public void setVolume(UnsignedIntegerFourBytes instanceId, String channelName, UnsignedIntegerTwoBytes desiredVolume)
            throws RenderingControlException {
        if (mGetter.getPlayService() != null) {
            //kugou dlan v=0-100
        	
        	LogManager.d(TAG,"setVolume : desiredVolume = " + desiredVolume.getValue().intValue());
        	LogManager.d(TAG,"setVolume : MaxVolume = " + VolumeCotroller.getMaxVolume(context));
        	
            int v2  = Math.abs((desiredVolume.getValue().intValue()*VolumeCotroller.getMaxVolume(context))/100);
            mGetter.getPlayService().ISetVolume(v2);
            
            int volume = mAudioManager.getStreamVolume(Constants.STREAM_TYPE);
            LogManager.d(TAG,"setVolume volume now is =" + volume);
        }

    }

	@Override
	public UnsignedIntegerFourBytes[] getCurrentInstanceIds() {
		// TODO Auto-generated method stub
		LogManager.d(TAG,"getCurrentInstanceIds");
        return null;
	}

	@Override
	protected Channel[] getCurrentChannels() {
		// TODO Auto-generated method stub
		LogManager.d(TAG,"getCurrentChannels");
        return null;
	}

}
