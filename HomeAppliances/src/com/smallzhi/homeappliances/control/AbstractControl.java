package com.smallzhi.homeappliances.control;

import java.io.IOException;
import java.io.OutputStream;

import com.iii360.base.common.utl.LogManager;
import com.iii360.base.inf.ControlInterface;
import com.smallzhi.homeappliances.util.Client;
import com.smallzhi.homeappliances.util.CoreUtil;
import com.smallzhi.homeappliances.util.NetDataTypeTransform;

public abstract class AbstractControl implements IControlInterface {
	protected static ControlInterface mControlInterface;
	protected OutputStream outputStream;
	protected Client mClient;

	public AbstractControl(Client client) {
		mClient = client;
		init();
	}

	@Override
	public String sendMsg(String msg) {
		if (outputStream != null) {
			String value = msg;
			LogManager.e(value);
			if (value == null) {
				return "没有找到对应设备";
			}

			String[] valueString = value.split("\\|");
			for (String order : valueString) {
				byte[] values = NetDataTypeTransform.intStringToByte(order);
				try {
					outputStream.write(values);
				} catch (IOException e) {
					LogManager.printStackTrace(e);
					return "执行失败,请稍后再试";
				}
			}

			return "";
		}
		return "数据接口没准备好";
	}

}
