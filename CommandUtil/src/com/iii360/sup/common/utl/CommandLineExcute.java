package com.iii360.sup.common.utl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class CommandLineExcute {
	public static String execCommand(String command) throws IOException {

		// String[] args = new String[]{"sh", "-c", command};
		Runtime runtime = Runtime.getRuntime();
		Process proc = runtime.exec(command); // 这句话就是shell与高级语言间的调用
												// 如果有参数的话可以用另外一个被重载的exec方法

		// 实际上这样执行时启动了一个子进程,它没有父进程的控制台
		// 也就看不到输出,所以我们需要用输出流来得到shell执行后的输出
		InputStream inputstream = proc.getInputStream();
		InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
		BufferedReader bufferedreader = new BufferedReader(inputstreamreader);

		// read the output
		String line = "";
		StringBuilder sb = new StringBuilder(line);
		while ((line = bufferedreader.readLine()) != null) {
			sb.append(line);
			sb.append('\n');
		}

		bufferedreader.close();
		inputstreamreader.close();
		inputstream.close();
		// --------------
		InputStream errorinputstream = proc.getErrorStream();
		InputStreamReader errorinputstreamreader = new InputStreamReader(errorinputstream);
		BufferedReader errorReader  = new BufferedReader(errorinputstreamreader);
		while ((line = errorReader.readLine()) != null) {
			System.out.append(line);
			System.out.append('\n');
		}
		errorReader.close();
		errorinputstreamreader.close();
		errorinputstream.close();
		// 使用exec执行不会等执行成功以后才返回,它会立即返回
		// 所以在某些情况下是很要命的(比如复制文件的时候)
		// 使用wairFor()可以等待命令执行完成以后才返回
		try {
			if (proc.waitFor() != 0) {
				LogManager.e("exit value = " + proc.exitValue());
			}
		} catch (InterruptedException e) {
			LogManager.printStackTrace(e);
		}
		return sb.toString();
	}
}
