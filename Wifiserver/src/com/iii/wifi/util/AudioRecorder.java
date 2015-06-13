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
	// ��Ƶ��ȡԴ

	private int audioSource = MediaRecorder.AudioSource.MIC;

	// ������Ƶ�����ʣ�44100��Ŀǰ�ı�׼������ĳЩ�豸��Ȼ֧��22050��16000��11025

	private static int sampleRateInHz = 16000;

	// AudioName����Ƶ�����ļ�

	private static final String AudioName = "/sdcard/love.raw";

	// ������Ƶ��¼�Ƶ�����CHANNEL_IN_STEREOΪ˫������CHANNEL_CONFIGURATION_MONOΪ������

	private static int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;

	// ��Ƶ���ݸ�ʽ:PCM 16λÿ����������֤�豸֧�֡�PCM 8λÿ����������һ���ܵõ��豸֧�֡�

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

		// ����AudioRecord����

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

		// ��¼��״̬Ϊtrue

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

			writeDateTOFile();// ���ļ���д��������

			copyWaveFile(AudioName, NewAudioName);// �������ݼ���ͷ�ļ�

		}

	}
	// ����õ��ɲ��ŵ���Ƶ�ļ�

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
		 * Ϊʲô��24������Ǹ���WAV�ļ���ʽ�����ġ�
		 */
		int totalDataLength = audioDataLength + 24 ;
		byte[] header = new byte[44];
		header[0] = 'R';
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		
		/**
		 * д�������ݳ���
		 */
		header[4] = (byte) ( totalDataLength & 0xff );
		header[5] = (byte) ((totalDataLength >> 8) & 0xff);
		header[6] = (byte) ((totalDataLength >> 16) & 0xff);
		header[7] = (byte) ((totalDataLength >> 24) & 0xff);
		
		/**
		 * д���־
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
		 * д��channel
		 */
		header[22] = (byte) channel;
		header[23] = 0 ;
		
		/**
		 * д��sampleRate
		 */
		header[24] = (byte) (sampleRate & 0xff);
		header[25] = (byte) ((sampleRate>>8) & 0xff);
		header[26] = (byte) ((sampleRate>>16)& 0xff);
		header[27] = (byte) ((sampleRate>>24)& 0xff);
		
		/**
		 * д��bit rate
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
		 * д��data
		 */
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		/**
		 * д�����ݳ���
		 */
		header[40] = (byte) (audioDataLength & 0xff);
		header[41] = (byte) ((audioDataLength>>8) & 0xff);
		header[42] = (byte) ((audioDataLength>>16) & 0xff);
		header[43] = (byte) ((audioDataLength>>24) & 0xff);
		
		return header;
	}
	
	/**
	 * 
	 * �����ṩһ��ͷ��Ϣ��������Щ��Ϣ�Ϳ��Եõ����Բ��ŵ��ļ���
	 * 
	 * Ϊ��Ϊɶ������44���ֽڣ��������û�����о�������������һ��wav
	 * 
	 * ��Ƶ���ļ������Է���ǰ���ͷ�ļ�����˵����һ��Ŷ��ÿ�ָ�ʽ���ļ�����
	 * 
	 * �Լ����е�ͷ�ļ���
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

		// newһ��byte����������һЩ�ֽ����ݣ���СΪ��������С

		byte[] audiodata = new byte[bufferSizeInBytes];

		FileOutputStream fos = null;

		int readsize = 0;

		try {

			File file = new File(AudioName);

			if (file.exists()) {

				file.delete();

			}

			fos = new FileOutputStream(file);// ����һ���ɴ�ȡ�ֽڵ��ļ�

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

			fos.close();// �ر�д����

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	public void stop() throws IOException {

		if (audioRecord != null) {

			System.out.println("stopRecord");

			isRecord = false;// ֹͣ�ļ�д��

			audioRecord.stop();

			audioRecord.release();// �ͷ���Դ
			new File(AudioName).deleteOnExit();
		}

	}

}