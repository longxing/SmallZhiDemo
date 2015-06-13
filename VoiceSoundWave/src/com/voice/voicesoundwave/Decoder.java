
package com.voice.voicesoundwave;

/**
 * Copyright 2002 by the authors. All rights reserved.
 *
 * Author: Cristina V Lopes
 * (Modified by Jonas Michel, 2012)
 */


import java.io.ByteArrayOutputStream;


/**
 * This class contains the signal processing functions.
 *
 * @author CVL
 */
public class Decoder implements Constants {

    /**
     * @param signal the audio samples to search
     * @param signalStrengths this will be filled in with the strengths for each frequency (NOTE THIS SIDE EFFECT)
     * @param granularity a correlation will be determined every granularity samples (lower is slower)
     * @return the index in signal of the key sequence, or -1 if it wasn't found (in which case signalStrengths is trashed)
     */
    public static int findKeySequence(byte[] signal, double[] signalStrengths, int granularity, int keyFrequency){
    if (signal == null || signalStrengths == null) {
    	return -1;
    }
	int maxCorrelationIndex = -1;
	double maxCorrelation = -1;
//	double minSignal = 0.003;
	double acceptedSignal = 0.1;
	int i=0;
	for(i = 0; i <= signal.length - kSamplesPerDuration; i += granularity){ 
	    //test the correlation
	    byte[] partialSignal = ArrayUtils.subarray(signal, i, kSamplesPerDuration);
	    double corr = complexDetect(partialSignal, keyFrequency) /* * 4 */;
	    if (corr > maxCorrelation){
		maxCorrelation = corr;
		maxCorrelationIndex = i;
	    }
	    if(granularity <= 0){
		break;
	    }
	}

	if (maxCorrelation < acceptedSignal && maxCorrelation > -1){
	    maxCorrelationIndex = -1;
	}

	return maxCorrelationIndex;
    }

    /**
     * @param startSignals the signal strengths of each of the frequencies
     * @param samples the samples
     * @return the decoded bytes
     */
    public static byte[] decode(double[] startSignals, byte[] samples){
	return decode(startSignals, getSignalStrengths(samples));
    }

    /**
     * @param startSignals the signal strengths of each of the frequencies
     * @param signal the signal strengths for each frequency for each duration [strength][duration index]
     * SIDE EFFECT: THE signal PARAMETER WILL BE SCALED BY THE STARTSIGNALS
     * @return the decoded bytes
     */
    private static byte[] decode(double[] startSignals, double[][] signal){
	//normalize to the start signals
	for(int i = 0; i < (kBitsPerByte * kBytesPerDuration); i++){
	    for(int j = 0; j < signal[i].length; j++){
		signal[i][j] = signal[i][j] / startSignals[i];
	    }
	}

	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	for(int i = 0; i < signal[0].length; i++){
	    for(int k = 0; k < kBytesPerDuration; k++){
		byte value = 0;
		for(int j = 0; j < kBitsPerByte; j++){
		    if(signal[(k * kBitsPerByte) + j][i] > 0.4){ // detection threshold
			value = (byte)(value | ( 1 << j));
		    } else {
		    }
		}
		baos.write(value);
	    }
	}

	return baos.toByteArray();

    }

    /**
     * @param input audio sample array
     * @return the signal strengths of each frequency in each duration: [signal strength][duration index]
     */
    private static double[][] getSignalStrengths(byte[] input){
	//detect the signal strength of each frequency in each duration
	int durations = input.length / kSamplesPerDuration;

	// rows are durations, cols are bit strengths
	double[][] signal = new double[kBitsPerByte * kBytesPerDuration][durations]; 

	//for each duration, check each bit for representation in the input
	for(int i=0; i < durations; i++){
	    //separate this duration's input into its own array
	    byte[] durationInput = ArrayUtils.subarray(input, i * kSamplesPerDuration, kSamplesPerDuration);

	    //for each bit represented, detect
	    for(int j = 0; j < kBitsPerByte * kBytesPerDuration; j++){
		signal[j][i] = 
		    complexDetect(durationInput, Encoder.getFrequency(j));
	    }
	}
	return signal;
    }

    public static void getKeySignalStrengths(byte[] signal, double[] signalStrengths){
	byte[] partialSignal = ArrayUtils.subarray(signal, 0, kSamplesPerDuration);
	for(int j = 1; j < kBitsPerByte * kBytesPerDuration; j += 2){
	    signalStrengths[j] = complexDetect(partialSignal, Encoder.getFrequency(j));
	}
	
	byte[] partialSignal2 = ArrayUtils.subarray(signal, kSamplesPerDuration, kSamplesPerDuration);
	for(int j = 0; j < kBitsPerByte * kBytesPerDuration; j += 2){
	    signalStrengths[j] = complexDetect(partialSignal2, Encoder.getFrequency(j));
	}
    }
    
    /**
     * @param input array of bytes with CRC appended at the end
     * @return true if appended CRC == calculated CRC, false otherwise
     */
    public static boolean crcCheckOk(byte[] input) {
    	return (byte)((input[input.length - 9] & 0x0f | (input[input.length - 8] << 4)) ^ CRCGen.crc_8_ccitt(input, input.length - 9)) == 0;
    }
    
    public static byte[] removeCRC(byte[] input) {
    	return ArrayUtils.subarray(input, 0, input.length - 9);
    }

    /**
     * @param signal audio samples
     * @param frequence the frequency to search for in signal
     * @return the strength of the correlation of the frequency in the signal
     */
    
    // original implementaiton from ask-simple-java :
    private static double complexDetect(byte[] signal, double frequency){
    	double realSum = 0;
    	double imaginarySum = 0;
    	double u = 2 * Math.PI * frequency / kSamplingFrequency;
    	// y = e^(ju) = cos(u) + j * sin(u) 

    	for(int i = 0; i < signal.length; i++){
    	    realSum = realSum + (Math.cos(i * u) * (signal[i]/(float)Constants.kFloatToByteShift));
    	    imaginarySum = imaginarySum + (Math.sin(i * u) * (signal[i]/(float)Constants.kFloatToByteShift));
    	}
     	double realAve = realSum/signal.length;
     	double imaginaryAve = imaginarySum/signal.length;
    	return Math.sqrt( (realAve * realAve) + (imaginaryAve * imaginaryAve) );
        }
    
    
}
