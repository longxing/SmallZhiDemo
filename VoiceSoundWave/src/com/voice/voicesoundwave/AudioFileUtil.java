package com.voice.voicesoundwave;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Queue;

public class AudioFileUtil {

	/**
	 * 
	 * @param fileName 全路径
	 * @param bufferQueue 音频缓冲区队列
	 * @param bufferLength 音频缓冲区队列总长度
	 * @throws Exception
	 */
	public static void generateWAVFile(String fileName,Queue<byte[]> bufferQueue,long bufferLength) 
																			throws Exception {
		byte [] header = generateWAVHeader(16000, 16, 1, (int)bufferLength);
		File file = new File(fileName);
		if( file.exists() ) {
			file.delete();
		}
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		raf.write(header);
		byte [] buffer = null ;
		while( (buffer = bufferQueue.poll()) != null ) {
			raf.write(buffer);
		}
		raf.close();
	}
	/**
	 * 
	 * @param sampleRate 采样率16k,8k
	 * @param bitRate 16b 8b
	 * @param channel 单声道为1，双声道为2.
	 * @param audioDataLength 文件长度
	 * @return 编码后的WAV头部
	 */
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
	 * integer 2 byte[]
	 * @param pInt
	 * @return
	 */
	public static byte[] intToByteArray(int pInt) {
	    byte[] arrayOfByte = new byte[4];
	    arrayOfByte[0] = (byte)(pInt & 0xFF);
	    arrayOfByte[1] = (byte)(0xFF & pInt >> 8);
	    arrayOfByte[2] = (byte)(0xFF & pInt >> 16);
	    arrayOfByte[3] = (byte)(0xFF & pInt >> 24);
	    return arrayOfByte;
	  }

	/**
	 * short 2 byte[]
	 * @param pShort
	 * @return
	 */
	  public static byte[] shortToByteArray(short pShort) {
	    byte[] arrayOfByte = new byte[2];
	    arrayOfByte[0] = (byte)(pShort & 0xFF);
	    arrayOfByte[1] = (byte)(0xFF & pShort >>> 8);
	    return arrayOfByte;
	  }


}
