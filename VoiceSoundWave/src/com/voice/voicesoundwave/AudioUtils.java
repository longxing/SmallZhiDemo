package com.voice.voicesoundwave;

/**
 * Copyright 2002 by the authors. All rights reserved.
 *
 * Author: Cristina V Lopes
 */



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


/**
 * 
 * @author CVL
 */

public class AudioUtils {

    public static void encodeFileToWav(File inputFile, File outputFile)
	throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	Encoder.encodeStream(new FileInputStream(inputFile), baos);
	// patched out for Android
	// writeWav(outputFile, baos.toByteArray());
    }
    
	

    
    public static PlayThread performData(byte[] data, long delay,boolean isClient)
	throws IOException {

    	PlayThread p = new PlayThread( data, delay ,isClient);
    	return p;
	
    }

    public static void performFile(File file, long delay,boolean isClient) 
	throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	Encoder.encodeStream(new FileInputStream(file), baos);
	performData(baos.toByteArray(), delay,isClient);
    }
    
    public static byte[] performArray(byte[] array, long delay) 
	throws IOException {
	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	Encoder.encodeStream(new ByteArrayInputStream(Encoder.appendCRC(array)), baos);
	return baos.toByteArray();
//	return performData(baos.toByteArray(), delay);
    }
    
    public static void performSOS(long delay,boolean isClient) throws IOException {
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
    	Encoder.generateSOS(baos);
    	performData(baos.toByteArray(), delay,isClient);
    }
}
