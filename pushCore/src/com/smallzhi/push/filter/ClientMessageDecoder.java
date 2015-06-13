package com.smallzhi.push.filter;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.smallzhi.push.entity.Message;
import com.smallzhi.push.util.LogManager;
/**
 *  客户端消息解码
 *  @author 3979434@qq.com
 *
 */
public class ClientMessageDecoder extends CumulativeProtocolDecoder {
	private Gson gson = new Gson();
	@Override
	public boolean doDecode(IoSession iosession, IoBuffer iobuffer,
			ProtocolDecoderOutput out) throws Exception {

		while (iobuffer.remaining()>0) {
			//有数据时，读取4字节判断消息长度
			byte [] sizeBytes = new byte[4]; 
			iobuffer.mark();//标记当前位置，以便reset 
			iobuffer.get(sizeBytes);//读取前4字节  
			//CommonUtil是自己写的一个int转byte[]的一个工具类
			int size = bytesToInt(sizeBytes); 
			if(size > iobuffer.remaining()){//如果消息内容不够，则重置，相当于不读取size  
				iobuffer.reset();  
				return false;//接收新数据，以拼凑成完整数据  
			}else{//内容填满  
				byte[] bytes = new byte[size];   
				iobuffer.get(bytes, 0, size);  
				String xmlStr = new String(bytes,"UTF-8"); 
				LogManager.d("ClientMessageDecoder:" + xmlStr);
				if(xmlStr != null && !"".equals(xmlStr)){
					Message msg = gson.fromJson(xmlStr, Message.class);
					out.write(msg);
				}
				if(iobuffer.remaining() > 0){//如果读取内容后还粘了包，就让父类再给俺一次，进行下一次解析  
					return true;  
				}  
			}  
		}  
		return false;//处理成功，让父类进行接收下个包  
	}

	 public  static int bytesToInt(byte[] bytes) {

	        int addr = bytes[0] & 0xFF;

	        addr |= ((bytes[1] << 8) & 0xFF00);

	        addr |= ((bytes[2] << 16) & 0xFF0000);

	        addr |= ((bytes[3] << 24) & 0xFF000000);

	        return addr;

	    }
}
