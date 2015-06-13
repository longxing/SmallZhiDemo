package com.voice.voicesoundwave;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;

import com.iii360.sup.common.utl.LogManager;
import com.voice.voicesoundwave.StreamDecoder.StreamDecoderRunnableInterface;

public class SoundWaveControl {
	public static SoundWaveControl mSoundWaveControl = null;
	public static final int SEND_START = -1;
	public static final int SEND_STOP = 1;
	public static final int MSG_RECEIVED_GOOD_BROADCAST = 3;
	public int status = 1;
	public int statusListener = 1;
	private Timer mTimer = null;
	private PlayThread mPlayThread;
	private byte[] mSendArray;
	private String mCorrectBroadcast;
	private MicrophoneListener microphoneListener = null;
	private StreamDecoder sDecoder = null;
	private ByteArrayOutputStream decodedStream = new ByteArrayOutputStream();
	private Context mContext;
	private StreamDecoderInterface mStreamDecoderInterface;
	private StreamDecoderRunnableInterface mStreamDecoderRunnableInterface;
	private StreamDecoderRunnableInterface mStreamDecoderRunnableInterfaceSOS;
	private StringAndByteUtil mStringAndByteUtil;
    private Map<String,Integer> isStop; 
	public interface StreamDecoderInterface {
		public void onResult(String result);
	}

	private void stopListening() {
		try {
			if (microphoneListener != null)
				microphoneListener.quit();

			microphoneListener = null;

			if (sDecoder != null)
				sDecoder.quit();

			sDecoder = null;
		} catch (Exception e) {
		}
	}

	public static SoundWaveControl getInstance(Context context) {
		if (mSoundWaveControl == null) {
			mSoundWaveControl = new SoundWaveControl(context);
		}
		return mSoundWaveControl;
	}

	private SoundWaveControl(Context context) {
		mContext = context;
		mStringAndByteUtil = new StringAndByteUtil(context);
		isStop = new HashMap<String,Integer>();
		statusListener = 1;
		mStreamDecoderRunnableInterface = new StreamDecoderRunnableInterface() {

			@Override
			public void onResult(byte[] result) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onComplent() {
				// TODO Auto-generated method stub
				status = 1;
				stopSendData();
				LogManager.e("decoded status=" + status);
			}

		};
		mStreamDecoderRunnableInterfaceSOS = new StreamDecoderRunnableInterface() {

			@Override
			public void onResult(byte[] result) {
				// TODO Auto-generated method stub
				byte[] res = new byte[result.length / 2]; 
				for (int i = 0 , j = 0;i < result.length;i+=2,j++) {
					res[j] = (byte) ((result[i] & 0x0f) | (result[i+1] << 4));
				}
				mCorrectBroadcast = mStringAndByteUtil.bytesToString(res);
//				try {
//					mCorrectBroadcast = new String(res,"UTF-8");
//				} catch (UnsupportedEncodingException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
				statusListener = 1;
				stopListening();
				AudioManagerStreamVolume.getIntence(mContext).setVolumeMax();
				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						long tPlay = playSOS(0);
						LogManager.e("decoded>>>>>>>>>>>>>>>>>>>>>    tPlay <<<<<<<<<<<<<<<<<<<<<" + tPlay);
						if (tPlay > -1)
						// start listening when playing is finished
						{
							// if (mTimer != null) {
							// mTimer.cancel();
							// }
							mTimer.schedule(new StopSOSTimerTask(), tPlay * 4);
						}
					}
					
				}).start();
			}

			@Override
			public void onComplent() {
				// TODO Auto-generated method stub
				// stopSendData(mContext);
			}

		};
		// mTimer.purge();
	}

	public synchronized void sendData(String data) {
		if (status == SEND_START) {
			return;
		}
		if (mPlayThread != null) {
			mPlayThread.stopPlay();
		}
		AudioManagerStreamVolume.getIntence(mContext).setVolumeMax();
		final long tPlay = playData(data, 0);
		final long millisPlayTime = (long) ((Constants.kPlayJitter + Constants.kDurationsPerSOS)
				* Constants.kSamplesPerDuration / Constants.kSamplingFrequency * 1000);
		status = SEND_START;
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				isStop.put(Thread.currentThread().toString(), SEND_START);
//				while (status == SEND_START) {
				while (isStop.get(Thread.currentThread().toString()) == SEND_START) {
					try {
						Thread.sleep(tPlay);
//						if (status != SEND_STOP) {
						if (isStop.get(Thread.currentThread().toString()) != SEND_STOP) {
							if (mPlayThread != null) {
								mPlayThread.stopPlay();
							}
						    listentSOS();
						} else {
							return;
						}
						Thread.sleep((long) (millisPlayTime * 1.5));
						if (isStop.get(Thread.currentThread().toString()) != SEND_STOP) {
							stopListening();
							Thread.sleep(1);
							if (mPlayThread != null) {
								mPlayThread.stopPlay();
							}
							if (isStop.get(Thread.currentThread().toString()) != SEND_STOP) {
							    mPlayThread = AudioUtils.performData(mSendArray, 0,false);
							}
						} else {
							if (mPlayThread != null) {
								mPlayThread.stopPlay();
							}
							return;
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
					}
				}
			}
		}).start();
	}

	private class StopSOSTimerTask extends TimerTask {

		public StopSOSTimerTask() {
		}

		@Override
		public void run() {
			AudioManagerStreamVolume.getIntence(mContext).seterVolume();
			mStreamDecoderInterface.onResult(mCorrectBroadcast);
			this.cancel();
		}
	}


	public synchronized long playData(String input, long delay) {

		long millisPlayTime = -1;
		try {

			// try to play the file
			ByteArrayOutputStream baos = new ByteArrayOutputStream();  
			DataOutputStream outputstream = new DataOutputStream(baos);  
			outputstream.writeChars(input);  
			  
//			byte[] inputBytes = input.getBytes("UTF-8");
			byte[] inputBytes = mStringAndByteUtil.ascToBytes(input);
			byte[] inputResult = new byte[inputBytes.length * 2];
			for (int i = 0 , j = 0;i < inputBytes.length;i++,j+=2) {
				inputResult[j] = (byte) (inputBytes[i] & 0x0f);
				inputResult[j+1] = (byte) (inputBytes[i] >> 4 & 0x0f);
			}
			mSendArray = AudioUtils.performArray(inputResult, delay);
			mPlayThread = AudioUtils.performData(mSendArray, 0,false);
			/**
			 * length of play time (ms) = nDurations * samples/duration * 1/fs *
			 * 1000
			 */
			millisPlayTime = (long) ((Constants.kPlayJitter
					+ Constants.kDurationsPerHail + Constants.kBytesPerDuration
					* inputBytes.length + Constants.kDurationsPerCRC)
					* Constants.kSamplesPerDuration 
					/ Constants.kSamplingFrequency * 1000);

		}

		catch (Exception e) {
			System.out
					.println("Could not encode " + input + " because of " + e);
		}

		return millisPlayTime + 5;
	}

	public synchronized void stopSendData() {
		stopListening();
		if (null != mPlayThread) {
			mPlayThread.stopPlay();
		}
		AudioManagerStreamVolume.getIntence(mContext).seterVolume();
		Iterator<String> it = isStop.keySet().iterator();
		while(it.hasNext()){
			String key = (String) it.next();
			isStop.put(key, SEND_STOP);
		}
		status = SEND_STOP;
	}

	private synchronized void listentSOS() {
		 stopListening();
		decodedStream.reset();

		// the StreamDecoder uses the Decoder to decode samples put in its
		// AudioBuffer
		// StreamDecoder starts a thread
		sDecoder = new StreamDecoder(decodedStream,
				mStreamDecoderRunnableInterface, true);

		// the MicrophoneListener feeds the microphone samples into the
		// AudioBuffer
		// MicrophoneListener starts a thread
		microphoneListener = new MicrophoneListener(sDecoder.getAudioBuffer());
	}

	public synchronized void listent(StreamDecoderInterface inter) {
		LogManager.e("decoded>>>>>>>>>    listent  <<<<<<<" + statusListener);
		if (statusListener != 1) {
			return;
		}
		statusListener = -1;
		stopListening();
//		LogManager.e("decoded>>>>>>>>>    stop listening  <<<<<<<");
//		Intent intent = new Intent();
//		intent.setAction("VOICESOUNDWAVE_SERVICE_STOP_BACK_WAKE_UP_LISTEN");
//		mContext.sendBroadcast(intent);
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		mTimer = new Timer();
		mStreamDecoderInterface = inter;
		decodedStream.reset();

		// the StreamDecoder uses the Decoder to decode samples put in its
		// AudioBuffer
		// StreamDecoder starts a thread
		sDecoder = new StreamDecoder(decodedStream,
				mStreamDecoderRunnableInterfaceSOS, false);

		// the MicrophoneListener feeds the microphone samples into the
		// AudioBuffer
		// MicrophoneListener starts a thread
		microphoneListener = new MicrophoneListener(sDecoder.getAudioBuffer());
	}

	public synchronized void stopListent() {
//		LogManager.e("decoded>>>>>>>>>    stopListent  <<<<<<<");
//		Intent intent = new Intent();
//		intent.setAction("VOICESOUNDWAVE_SERVICE_START_BACK_WAKE_UP_LISTEN");
//		mContext.sendBroadcast(intent);
		this.stopListening();
		statusListener = 1;
	}

	public long playSOS(long delay) {

		long millisPlayTime = -1;
		try {
			// try to play the file
			AudioUtils.performSOS(delay,true);

			/**
			 * length of play time (ms) = nDurations * samples/duration * 1/fs *
			 * 1000
			 */
			millisPlayTime = (long) ((Constants.kPlayJitter + Constants.kDurationsPerSOS)
					* Constants.kSamplesPerDuration
					/ Constants.kSamplingFrequency * 1000);

		}

		catch (Exception e) {
			LogManager.e("Could not perform SOS because of " + e);
		}

		return millisPlayTime;
	}
}
