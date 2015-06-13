package com.voice.voicesoundwave;

/**
 * Copyright 2002 by the authors. All rights reserved.
 *
 * Author: Cristina V Lopes
 * (Modified by Jonas Michel, 2012)
 */


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

import android.os.Environment;

import com.iii360.sup.common.utl.LogManager;

/**
 * This starts a Thread which decodes data in an AudioBuffer and writes it to an OutputStream.
 * StreamDecoder holds the buffer where the MicrophoneListener puts bytes.
 *
 */
public class StreamDecoder implements Runnable {

  public static String kThreadName = "StreamDecoder";
  
  private Thread myThread = null;
  private Object runLock = new Object();
  private boolean running = false;

  private AudioBuffer buffer = new AudioBuffer(); // THE buffer where bytes are being put
  private ByteArrayOutputStream out = null;
  
  private boolean hasKey = false;
  private byte[] receivedBytes = null;
  private boolean mIsSOS;
  private boolean contendingForSOS = false;
  private StreamDecoderRunnableInterface mStreamDecoderRunnableInterface;
	public interface StreamDecoderRunnableInterface {
		public void onResult(byte[] result);
		public void onComplent();
	}
  
//  private Handler handler = null;
  
    /**
     * This creates and starts the decoding Thread
     * @param _out the OutputStream which will receive the decoded data
     */
    public StreamDecoder(ByteArrayOutputStream _out, StreamDecoderRunnableInterface inter,boolean isSOS) {
	out = _out;
	mIsSOS = isSOS;
	mStreamDecoderRunnableInterface = inter;
	myThread = new Thread(this, kThreadName);
	myThread.start();
    }

    public String getStatusString()
    {
    	String s = "";
    	
    	int backlog = (int) ((1000 * buffer.size()) / Constants.kSamplingFrequency);
    	
    	if( backlog > 0 )
    		s += "Backlog: " + backlog + " mS ";

    	if( hasKey )
    		s += "Found key sequence ";

    	return s;
    }
    
    public AudioBuffer getAudioBuffer(){
	return buffer;
    }
    
    public boolean getHasKey() {
	return hasKey;
    }
    
    public byte[] getReceivedBytes() {
	return receivedBytes;
    }

    public void run() {
	synchronized(runLock){
	    running = true;
	}

	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
	
	int durationsToRead = Constants.kDurationsPerHail;
	int deletedSamples = 0;
	
	double[] startSignals = new double[Constants.kBitsPerByte * Constants.kBytesPerDuration];
	boolean notEnoughSamples = true;
	byte samples[] = null;
    int currentNum = 0;
	hasKey = false;
	Queue<byte[]> queues = new LinkedList<byte[]>(); 
	byte current = 1;
	int length = 0 ;
	while(running)
	{
	  notEnoughSamples = true;
	  while (notEnoughSamples && running) 
	  {
		samples = buffer.read(Constants.kSamplesPerDuration * durationsToRead);
	    if (samples != null)
	    	notEnoughSamples = false;
		else
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//	    	Thread.yield();
	  }
	  
	  if(hasKey)
	  { //we found the key, so decode this duration
	    byte[] decoded = Decoder.decode(startSignals, samples);
	    try {
	      buffer.delete(samples.length);
	      deletedSamples += samples.length;
		  out.write(decoded);
		  queues.add(samples);
		  length += samples.length;
		  LogManager.e(decoded[0] +"decoded " + decoded.length + " bytes");
		  byte[] samp = out.toByteArray();
		    if(decoded[0] == 0 && samp.length > 4 && (samp[samp.length - 1] | samp[samp.length - 2] | samp[samp.length - 3] | samp[samp.length - 4] | samp[samp.length - 5] | decoded[0]) == 0){ //we are receiving no signal, so go back to key detection mode

		      if (Decoder.crcCheckOk(out.toByteArray())) {
		    	  // signal received correctly
		    	  receivedBytes = Decoder.removeCRC(out.toByteArray());
		    	  mStreamDecoderRunnableInterface.onResult(receivedBytes);
		    	  
//		          String tempPath = Environment.getExternalStorageDirectory().getPath() + "/record/";
//		          File filePath = new File(tempPath);
//		          if (!filePath.exists()) {
//		        	  filePath.mkdirs();
//		          }
//		          String tempFileName = tempPath + "成功录音_" +
//		         		 System.currentTimeMillis() + ".wav";
//		          
//		         		 try {
//		         		 AudioFileUtil.generateWAVFile(tempFileName, queues,
//		         				length);
//		         		 } catch (Exception e) {
//		         			 e.printStackTrace();
//		         		 }
//		         		 LogManager.e("decoded>>" + tempFileName);
		    	  LogManager.e("decoded>>>>>>>>>>    secuss <<<<<<<<<<<<<<<<<<<<<");
//		    	  handler.sendEmptyMessage(SoundWaveControl.MSG_RECEIVED_GOOD_BROADCAST);
		      } else {
		    	  // enter contention for an SOS slot
		    	  contendingForSOS = true;
//		    	  String tempPath = Environment.getExternalStorageDirectory().getPath() + "/record/";
//		          File filePath = new File(tempPath);
//		          if (!filePath.exists()) {
//		        	  filePath.mkdirs();
//		          }
//		    	  String tempFileName = tempPath + "失败录音_" +
//			         		 System.currentTimeMillis() + ".wav";
//			         		 try {
//			         		 AudioFileUtil.generateWAVFile(tempFileName, queues,
//			         				length);
//			         		 } catch (Exception e) {
//			         			 e.printStackTrace();
//			         		 }
//			         		LogManager.e("decoded>>" + tempFileName);
		      }
		      buffer.reset();
		      out.reset();
		      LogManager.e("decoded>>>buffer size is " + buffer.size());
		      hasKey = false;
		      durationsToRead = Constants.kDurationsPerHail;
		    } else if (decoded[0] == -1) {
		    	contendingForSOS = true;
		    	buffer.reset();
			      out.reset();
		    	 LogManager.e("decoded>>>buffer size is " + buffer.size());
			      hasKey = false;
			      durationsToRead = Constants.kDurationsPerHail;
		    }
		    current =  decoded[0];
	    } catch (IOException e){
	      LogManager.e("IOException while decoding:" + e);
	      break;
	    }

	    try{ 
	      //this provides the audio sampling mechanism a chance to maintain continuity
	      Thread.sleep(10); 
	    } catch(InterruptedException e){
	      LogManager.e("Stream Decoding thread interrupted:" + e);
	      break;
	    }
	    continue;
	  }

	  //we don't have the key, so we are in key detection mode from this point on
	  int initialGranularity = 400;
	  int finalGranularity = 20;
	  
	  // detect SOS key
	  int sosIndex = -1;
	  if (mIsSOS) {
	      sosIndex = Decoder.findKeySequence(samples, startSignals, initialGranularity, Constants.kSOSFrequency);
	  }
	  // detect Hail key
	  int hailIndex = -1;
	  if (!mIsSOS) {
	     hailIndex = Decoder.findKeySequence(samples, startSignals, initialGranularity, Constants.kHailFrequency);
	  }
	  LogManager.e("decoded>>>>>sosIndex<<" + sosIndex + ">>> hailIndex >>" + hailIndex);
	  LogManager.e("decoded>>>buffer size is " + buffer.size());
	  if (sosIndex > -1
			&& ((hailIndex > -1 && sosIndex < hailIndex) || hailIndex == -1)) {
		try {
			buffer.delete(Constants.kSamplesPerDuration * durationsToRead);
			deletedSamples += Constants.kSamplesPerDuration * durationsToRead;
		} catch (IOException e) {
		}
		
		if (contendingForSOS) // someone else beat us to it
			contendingForSOS = false;
		else {// heard an cry for help{
			hasKey = false;
		    deletedSamples = 0;
		    mStreamDecoderRunnableInterface.onComplent();
		    LogManager.e("decoded>>>>>>>>>>>>>>>>>>>>>    onComplent <<<<<<<<<<<<<<<<<<<<<");
	  }
//			handler.sendEmptyMessage(SessionService.MSG_RECEIVED_SOS);
		
		continue;
	  }
	  
	  if(hailIndex > -1)
	  {
	    LogManager.e("\nRough Start Index: " + (deletedSamples + hailIndex));
	    //LogManager.e("Rough Start Time: " 
	    //	   + (deletedSamples + startIndex) / (float)Constants.kSamplingFrequency);

	    int shiftAmount = hailIndex /* - (Constants.kSamplesPerDuration)*/; 
	    if(shiftAmount < 0){
	      shiftAmount = 0;
	    }
	    LogManager.e("Shift amount: " + shiftAmount);
	    try { buffer.delete(shiftAmount);} catch (IOException e){}
	    deletedSamples += shiftAmount;
	    
	    durationsToRead = Constants.kDurationsPerHail ;
	    notEnoughSamples = true;
	    while (notEnoughSamples  && running) {
	      samples = buffer.read(Constants.kSamplesPerDuration * durationsToRead);
	      if (samples != null)
		notEnoughSamples = false;
	      else
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//	      else Thread.yield();
	    }

	    //LogManager.e("Search Start: " + deletedSamples + " End: " + (deletedSamples + samples.length));
	    //LogManager.e("Search Time: " + ((float)deletedSamples / Constants.kSamplesPerDuration) + " End: " 
	    //		   + ((float)(deletedSamples + samples.length) / Constants.kSamplingFrequency));
	    
	    hailIndex = Decoder.findKeySequence(samples, startSignals, finalGranularity, Constants.kHailFrequency);
	    try {
	      notEnoughSamples = true;
	      while (notEnoughSamples  && running) {
		samples = buffer.read(hailIndex + (Constants.kSamplesPerDuration * Constants.kDurationsPerHail));
		if (samples != null)
		  notEnoughSamples = false;
		else
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		else Thread.yield();
	      }
		  
	      samples = ArrayUtils.subarray(samples, hailIndex + Constants.kSamplesPerDuration, 
						  2 * Constants.kSamplesPerDuration);
	      Decoder.getKeySignalStrengths(samples, startSignals);
	      /*
		" f(2): " + startSignals[2] + " f(3): " + startSignals[3] +
		" f(4): " + startSignals[4] + " f(5): " + startSignals[5] +
		" f(6): " + startSignals[6] + " f(7): " + startSignals[7]);
	      */

	      buffer.delete(hailIndex + (Constants.kSamplesPerDuration * Constants.kDurationsPerHail));
	      deletedSamples += hailIndex + (Constants.kSamplesPerDuration * Constants.kDurationsPerHail);
	    } catch (IOException e){}
	        hasKey = true;
	        contendingForSOS = false;
	    
	    LogManager.e("decoded>>>>>>>>>>>>>>>>>>>>>    found key <<<<<<<<<<<<<<<<<<<<<");
	    length = 0;
	    
	        durationsToRead = 1;
	  } else {
		  if (contendingForSOS) {
			  hasKey = false;
		      deletedSamples = 0;
		      contendingForSOS = false;
		      LogManager.e("decoded>>>>>>>>>>>>>>>>>>>>>    contendingForSOS <<<<<<<<<<<<<<<<<<<<<");
		      continue;
//			  handler.sendEmptyMessage(SessionService.MSG_RECEIVED_BAD_BROADCAST);
		  }
	    try {
	      buffer.delete(Constants.kSamplesPerDuration);
	      deletedSamples += Constants.kSamplesPerDuration;
	    } catch (IOException e){}
	    
	    contendingForSOS = false;
	  }
	}
    }

    public void quit(){
//	synchronized(runLock){
	    running = false;
	    buffer.close();
//	}
    }
}
