package com.iii.wifi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

public class AudioRecorder {
	// 音频获取源

	private int audioSource = MediaRecorder.AudioSource.MIC;

	// 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025

	private static int sampleRateInHz = 16000;

	// AudioName裸音频数据文件

	private static final String AudioName = "/sdcard/love.raw";

	// 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道

	private static int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;

	// 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。

	private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	private String NewAudioName;

	private int bufferSizeInBytes;
	private AudioRecord audioRecord;
	private static AudioRecorder audioRecorder;

	public static AudioRecorder getAudioRecorder() {
		return audioRecorder;
	}

	public static void setAudioRecorder(AudioRecorder audioRecorder) {
		AudioRecorder.audioRecorder = audioRecorder;
	}

	public AudioRecorder(String path) {

		bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,

		channelConfig, audioFormat);

		// 创建AudioRecord对象

		audioRecord = new AudioRecord(audioSource, sampleRateInHz,

		channelConfig, audioFormat, bufferSizeInBytes);

		this.NewAudioName = sanitizePath(path);
		audioRecorder = this;
	}

	private String sanitizePath(String path) {
		return Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + path + ".mp3";
	}

	private boolean isRecord;

	public void start() throws IOException {
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			throw new IOException("SD Card is not mounted,It is  " + state + ".");
		}
		File directory = new File(NewAudioName).getParentFile();
		if (!directory.exists() && !directory.mkdirs()) {
			throw new IOException("Path to file could not be created");
		}

		audioRecord.startRecording();

		// 让录制状态为true

		isRecord = true;

		// mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		// mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		// mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		// // recorder.setAudioChannels(AudioFormat.CHANNEL_CONFIGURATION_MONO);
		// mMediaRecorder.setAudioSamplingRate(SAMPLE_RATE_IN_HZ);
		//
		new Thread(new AudioRecordThread()).start();
	}
	class AudioRecordThread implements Runnable {

		@Override
		public void run() {

			writeDateTOFile();// 往文件中写入裸数据

			copyWaveFile(AudioName, NewAudioName);// 给裸数据加上头文件

		}

	}
	// 这里得到可播放的音频文件

	private void copyWaveFile(String inFilename, String outFilename) {

		FileInputStream in = null;

		FileOutputStream out = null;

		long totalAudioLen = 0;

		long totalDataLen = totalAudioLen + 36;

		long longSampleRate = sampleRateInHz;

		int channels = 1;

		long byteRate = 16 * sampleRateInHz * channels / 8;

		byte[] data = new byte[bufferSizeInBytes];

		try {

			in = new FileInputStream(inFilename);

			out = new FileOutputStream(outFilename);

			totalAudioLen = in.getChannel().size();

			totalDataLen = totalAudioLen + 36;

			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,

			longSampleRate, channels, byteRate);

			while (in.read(data) != -1) {

				out.write(data);

			}

			in.close();

			out.close();

		} catch (FileNotFoundException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

	}
	public static byte[] generateWAVHeader(int sampleRate,int bitRate,int channel,int audioDataLength) {
		/**
		 * 为什么加24，这个是根据WAV文件格式而来的。
		 */
		int totalDataLength = audioDataLength + 24 ;
		byte[] header = new byte[44];
		header[0] = 'R';
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		
		/**
		 * 写入总数据长度
		 */
		header[4] = (byte) ( totalDataLength & 0xff );
		header[5] = (byte) ((totalDataLength >> 8) & 0xff);
		header[6] = (byte) ((totalDataLength >> 16) & 0xff);
		header[7] = (byte) ((totalDataLength >> 24) & 0xff);
		
		/**
		 * 写入标志
		 */
		header[8] = 'W' ;
		header[9] = 'A' ;
		header[10] = 'V' ;
		header[11] = 'E' ;
		header[12] = 'f' ;
		header[13] = 'm' ;
		header[14] = 't' ;
		header[15] = ' ' ;
		
		header[16] = 16 ;
		header[17] = 0 ;
		header[18] = 0 ;
		header[19] = 0 ;
		
		header[20] = 1 ;
		header[21] = 0 ;
		
		/**
		 * 写入channel
		 */
		header[22] = (byte) channel;
		header[23] = 0 ;
		
		/**
		 * 写入sampleRate
		 */
		header[24] = (byte) (sampleRate & 0xff);
		header[25] = (byte) ((sampleRate>>8) & 0xff);
		header[26] = (byte) ((sampleRate>>16)& 0xff);
		header[27] = (byte) ((sampleRate>>24)& 0xff);
		
		/**
		 * 写入bit rate
		 */
		int another = channel * bitRate *sampleRate / 8 ;
		header[28] = (byte) (another & 0xff);
		header[29] = (byte) ((another>>8) & 0xff);
		header[30] = (byte) ((another>>16) & 0xff);
		header[31] = (byte) ((another>>24) & 0xff);
		
		header[32] = (byte) (channel * bitRate / 8) ;
		header[33] = 0 ;
		
		header[34] = 16 ;
		header[35] = 0 ;
		
		/**
		 * 写入data
		 */
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		/**
		 * 写入数据长度
		 */
		header[40] = (byte) (audioDataLength & 0xff);
		header[41] = (byte) ((audioDataLength>>8) & 0xff);
		header[42] = (byte) ((audioDataLength>>16) & 0xff);
		header[43] = (byte) ((audioDataLength>>24) & 0xff);
		
		return header;
	}
	
	/**
	 * 
	 * 这里提供一个头信息。插入这些信息就可以得到可以播放的文件。
	 * 
	 * 为我为啥插入这44个字节，这个还真没深入研究，不过你随便打开一个wav
	 * 
	 * 音频的文件，可以发现前面的头文件可以说基本一样哦。每种格式的文件都有
	 * 
	 * 自己特有的头文件。
	 */

	private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,

	long totalDataLen, long longSampleRate, int channels, long byteRate)

	throws IOException {

		byte[] header = new byte[44];

		header[0] = 'R'; // RIFF/WAVE header

		header[1] = 'I';

		header[2] = 'F';

		header[3] = 'F';

		header[4] = (byte) (totalDataLen & 0xff);

		header[5] = (byte) ((totalDataLen >> 8) & 0xff);

		header[6] = (byte) ((totalDataLen >> 16) & 0xff);

		header[7] = (byte) ((totalDataLen >> 24) & 0xff);

		header[8] = 'W';

		header[9] = 'A';

		header[10] = 'V';

		header[11] = 'E';

		header[12] = 'f'; // 'fmt ' chunk

		header[13] = 'm';

		header[14] = 't';

		header[15] = ' ';

		header[16] = 16; // 4 bytes: size of 'fmt ' chunk

		header[17] = 0;

		header[18] = 0;

		header[19] = 0;

		header[20] = 1; // format = 1

		header[21] = 0;

		header[22] = (byte) channels;

		header[23] = 0;

		header[24] = (byte) (longSampleRate & 0xff);

		header[25] = (byte) ((longSampleRate >> 8) & 0xff);

		header[26] = (byte) ((longSampleRate >> 16) & 0xff);

		header[27] = (byte) ((longSampleRate >> 24) & 0xff);

		header[28] = (byte) (byteRate & 0xff);

		header[29] = (byte) ((byteRate >> 8) & 0xff);

		header[30] = (byte) ((byteRate >> 16) & 0xff);

		header[31] = (byte) ((byteRate >> 24) & 0xff);

		header[32] = (byte) (2 * 16 / 8); // block align

		header[33] = 0;

		header[34] = 16; // bits per sample

		header[35] = 0;

		header[36] = 'd';

		header[37] = 'a';

		header[38] = 't';

		header[39] = 'a';

		header[40] = (byte) (totalAudioLen & 0xff);

		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);

		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);

		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);

	}

	private void writeDateTOFile() {

		// new一个byte数组用来存一些字节数据，大小为缓冲区大小

		byte[] audiodata = new byte[bufferSizeInBytes];

		FileOutputStream fos = null;

		int readsize = 0;

		try {

			File file = new File(AudioName);

			if (file.exists()) {

				file.delete();

			}

			fos = new FileOutputStream(file);// 建立一个可存取字节的文件

		} catch (Exception e) {

			e.printStackTrace();

		}

		while (isRecord == true) {

			readsize = audioRecord.read(audiodata, 0, bufferSizeInBytes);

			if (AudioRecord.ERROR_INVALID_OPERATION != readsize) {

				try {

					fos.write(audiodata);

				} catch (IOException e) {

					e.printStackTrace();

				}

			}

		}

		try {

			fos.close();// 关闭写入流

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	public void stop() throws IOException {

		if (audioRecord != null) {

			System.out.println("stopRecord");

			isRecord = false;// 停止文件写入

			audioRecord.stop();

			audioRecord.release();// 释放资源
			new File(AudioName).deleteOnExit();
		}

	}

}