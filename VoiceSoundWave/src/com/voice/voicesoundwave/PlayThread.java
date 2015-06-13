package com.voice.voicesoundwave;


import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;

import com.iii360.sup.common.utl.LogManager;

public class PlayThread extends Thread {

	private byte[] buffer;
	private long delay = 0;
	public AudioTrack atrack;
	private boolean mIsClient;
	public PlayThread( byte[] b, long d ,boolean isClient)
	{
		buffer = new byte[b.length * 2];
		delay = d;
		mIsClient = isClient;
		// convert from 8 bit per sample to little-endian 16 bit per sample, IOW 16-bit PCM
		int i, j;
		for(i=0, j =0; i < b.length; i++, j += 2)
		{
		    buffer[j] = b[i];
		    buffer[j+1] = b[i];
		}
		
		start();
		LogManager.e("decoded>>>>>>>>>>>>>>>>>>>>>    tPlay start() <<<<<<<<<<<<<<<<<<<<<");
	}
	
	public void run()
	{
		// delay play if necessary
		while (delay > 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			delay -= 1000;
		}
		
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		int bufferSize = AudioTrack.getMinBufferSize(16000,
				AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		if (!mIsClient) {
			bufferSize *= 4;
		}
		atrack = new AudioTrack(AudioManager.STREAM_MUSIC,
	    							16000,
	    							AudioFormat.CHANNEL_OUT_MONO	,
	                                AudioFormat.ENCODING_PCM_16BIT,  // ENCODING_PCM_8BIT sounds very scratchy, so we use 16 bit and double up the data
	                                bufferSize, 
	                                AudioTrack.MODE_STREAM);
		atrack.play();
        try {
        	atrack.write(buffer, 0, buffer.length);
        } catch (Exception e) {
                e.printStackTrace();
                LogManager.e("decoded>>>>>>>>>>>>>>>>>>>>>    tPlay start() <<<<<<<<<<<<<<<<<<<<<" + e.toString());
        }
        
//        atrack.play();
//        Queue<byte[]> queues = new LinkedList<byte[]>(); 
//        queues.add(buffer);
//        int length = buffer.length;
//        String tempPath = Environment.getExternalStorageDirectory().getPath() + "/record/";
//        File filePath = new File(tempPath);
//        if (!filePath.exists()) {
//      	  filePath.mkdirs();
//        }
//        String tempFileName = tempPath + "client_" +
//       		 System.currentTimeMillis() + ".wav";
//       		 try {
//       		 AudioFileUtil.generateWAVFile(tempFileName, queues,
//       				length);
//       		 } catch (Exception e) {
//       			 e.printStackTrace();
//       		 }
        LogManager.e("decoded>>>>>>>>>>>>>>>>>>>>>    tPlay start() end<<<<<<<<<<<<<<<<<<<<<");
	}
	public void stopPlay() {
		if (atrack != null) {
			try {
		    atrack.stop();
		    atrack.flush();
		    atrack.release();
			} catch(Exception e) {
				
			}
		}
	}
	
}
