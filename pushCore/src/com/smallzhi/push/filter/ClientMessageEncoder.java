package com.smallzhi.push.filter;



import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.smallzhi.push.util.LogManager;


/**
 *  客户端消息发送前进行编码,可在此加密消息
 *  @author 3979434@qq.com
 *
 */
public class ClientMessageEncoder extends ProtocolEncoderAdapter {


	@Override
	public void encode(IoSession iosession, Object message, ProtocolEncoderOutput out) throws Exception {
	        	IoBuffer buff = IoBuffer.allocate(320).setAutoExpand(true);
				//buff.putString( message.toString(), charset.newEncoder());
	        	//编码的时候加上长度信息4位
	        	byte[] bytes = message.toString().getBytes("UTF-8"); 
	        	LogManager.d("clientEncode:"+message);
	        	byte[] sizeBytes = intToByte(bytes.length);
	        	
	        	buff.put(sizeBytes);//内容个数
	        	buff.put(bytes);
				buff.flip();
				out.write(buff);
	}
	 public static byte[] intToByte(int i) {
	    	
	        byte[] abyte0 = new byte[4];

	        abyte0[0] = (byte) (0xff & i);

	        abyte0[1] = (byte) ((0xff00 & i) >> 8);

	        abyte0[2] = (byte) ((0xff0000 & i) >> 16);

	        abyte0[3] = (byte) ((0xff000000 & i) >> 24);

	        return abyte0;

	    }
}
