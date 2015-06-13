package com.iii360.external.recognise.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.iii360.base.common.utl.LogManager;
import com.iii360.sup.common.utl.ShellUtils;

/**
 * FTP 封装类
 * 
 * @author Peter
 */
public class FTP {
	/**
	 * 服务器名.
	 */
	private String hostName;

	/**
	 * 用户名.
	 */
	private String userName;

	/**
	 * 密码.
	 */
	private String password;

	/**
	 * FTP连接.
	 */
	private FTPClient ftpClient;

	/**
	 * FTP列表.
	 */
	private List<FTPFile> list;

	/**
	 * FTP根目录.
	 */
	public static final String REMOTE_PATH = "/";

	/**
	 * FTP当前目录.
	 */
	private String currentPath = "";

	/**
	 * 统计流量.
	 */
	private double response;

	private boolean isDeleteFileAfterUpload = true;

	/**
	 * 构造函数.
	 * 
	 * @param host hostName 服务器名
	 * @param user userName 用户名
	 * @param pass password 密码
	 */
	public FTP(String host, String user, String pass) {
		this.hostName = host;
		this.userName = user;
		this.password = pass;
		this.ftpClient = new FTPClient();
		this.list = new ArrayList<FTPFile>();
	}

	public void setDeleteFileAfterUpload(boolean deleteFile) {
		this.isDeleteFileAfterUpload = deleteFile;
	}

	/**
	 * 打开FTP服务.
	 * 
	 * @throws IOException
	 */
	public void openConnect() throws IOException {
		int reply; // 服务器响应值
		ftpClient.connect(hostName);
		reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			// 断开连接
			ftpClient.disconnect();
			LogManager.e("connect fail: " + reply);
		}
		// 登录到服务器
		ftpClient.login(userName, password);
		reply = ftpClient.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftpClient.disconnect();
			LogManager.e("ftp login fail: " + reply);
		} else {
			FTPClientConfig config = new FTPClientConfig(ftpClient.getSystemType().split(" ")[0]);
			config.setServerLanguageCode("zh");
			ftpClient.configure(config);
			ftpClient.enterLocalPassiveMode();
			// 二进制文件支持
			ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
			LogManager.d("ftp login success !");
		}
	}

	/**
	 * 关闭FTP服务.
	 * 
	 * @throws IOException
	 */
	public void closeConnect() throws IOException {
		if (ftpClient != null) {
			ftpClient.logout();
			ftpClient.disconnect();
			LogManager.e("logout");
		}
	}

	/**
	 * 列出FTP下所有文件.
	 * 
	 * @param remotePath 服务器目录
	 * @return FTPFile集合
	 * @throws IOException
	 */
	public List<FTPFile> listFiles(String remotePath) throws IOException {
		FTPFile[] files = ftpClient.listFiles(remotePath);
		for (FTPFile file : files) {
			list.add(file);
		}
		return list;
	}

	/**
	 * 下载.
	 * 
	 * @param remotePath FTP目录
	 * @param fileName 文件名
	 * @param localPath 本地目录
	 * @return Result
	 * @throws IOException
	 */
	public Result download(String remotePath, String fileName, String localPath) throws IOException {
		boolean flag = true;
		Result result = null;
		currentPath = remotePath;
		response = 0;
		ftpClient.changeWorkingDirectory(remotePath);
		FTPFile[] ftpFiles = ftpClient.listFiles();
		for (FTPFile ftpFile : ftpFiles) {
			if (ftpFile.getName().equals(fileName)) {
				System.out.println("download...");
				File file = new File(localPath + "/" + fileName);
				Date startTime = new Date();
				if (ftpFile.isDirectory()) {
					flag = downloadMany(file);
				} else {
					flag = downloadSingle(file, ftpFile);
				}
				Date endTime = new Date();
				result = new Result(flag, Util.getFormatTime(endTime.getTime() - startTime.getTime()), Util.getFormatSize(response));
			}
		}
		return result;
	}

	/**
	 * 下载单个文件.
	 * 
	 * @param localFile 本地目录
	 * @param ftpFile FTP目录
	 * @return true下载成功, false下载失败
	 * @throws IOException
	 */
	private boolean downloadSingle(File localFile, FTPFile ftpFile) throws IOException {
		boolean flag = true;
		OutputStream outputStream = new FileOutputStream(localFile);
		response += ftpFile.getSize();
		flag = ftpClient.retrieveFile(localFile.getName(), outputStream);
		outputStream.close();
		return flag;
	}

	/**
	 * 下载多个文件.
	 * 
	 * @param localFile 本地目录
	 * @return true下载成功, false下载失败
	 * @throws IOException
	 */
	private boolean downloadMany(File localFile) throws IOException {
		boolean flag = true;
		if (!currentPath.equals(REMOTE_PATH)) {
			currentPath = currentPath + REMOTE_PATH + localFile.getName();
		} else {
			currentPath = currentPath + localFile.getName();
		}
		localFile.mkdir();
		ftpClient.changeWorkingDirectory(currentPath);
		FTPFile[] ftpFiles = ftpClient.listFiles();
		for (FTPFile ftpFile : ftpFiles) {
			File file = new File(localFile.getPath() + "/" + ftpFile.getName());
			if (ftpFile.isDirectory()) {
				flag = downloadMany(file);
			} else {
				flag = downloadSingle(file, ftpFile);
			}
		}
		return flag;
	}

	/**
	 * 上传.
	 * 
	 * @param localFile 本地文件
	 * @param remotePath FTP目录
	 * @return Result
	 * @throws IOException
	 */
	public Result uploading(File localFile, String remotePath) throws IOException {
		boolean flag = true;
		Result result = null;
		currentPath = remotePath;
		response = 0;
		// 二进制文件支持
		ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
		ftpClient.enterLocalPassiveMode();
		// 设置模式
		ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
		String dateFile = REMOTE_PATH + new SimpleDateFormat("yyyyMMdd").format(new Date());
		String snFile = dateFile + "/" + ShellUtils.readSerialNumber();
		if (ftpClient.changeWorkingDirectory(dateFile)) {
			if (!ftpClient.changeWorkingDirectory(snFile)) {
				ftpClient.makeDirectory(snFile);
			}
		} else {
			ftpClient.makeDirectory(dateFile);
			ftpClient.changeWorkingDirectory(dateFile);
			ftpClient.makeDirectory(snFile);
		}
		ftpClient.changeWorkingDirectory(snFile);
		Date startTime = new Date();
		if (localFile.isDirectory()) {
			flag = uploadingMany(localFile);
		} else {
			flag = uploadingSingle(localFile);
		}
		Date endTime = new Date();
		result = new Result(flag, Util.getFormatTime(endTime.getTime() - startTime.getTime()), Util.getFormatSize(response));
		return result;
	}

	/**
	 * 上传.
	 * 
	 * @param localFile 本地文件
	 * @param remotePath FTP目录
	 * @return Result
	 * @throws IOException
	 */
	public Result uploadingForZipFile(File localFile, String remotePath) throws IOException {
		boolean flag = true;
		Result result = null;
		currentPath = remotePath;
		response = 0;
		// 二进制文件支持
		ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
		ftpClient.enterLocalPassiveMode();
		ftpClient.setControlEncoding("UTF-8");
		// 设置模式
		ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
		ftpClient.changeWorkingDirectory(REMOTE_PATH);
		Date startTime = new Date();
		if (localFile.isDirectory()) {
			flag = uploadingMany(localFile);
		} else {
			flag = uploadingSingle(localFile);
			if (flag && isDeleteFileAfterUpload && localFile.exists()) {
				localFile.delete();
			}
		}
		Date endTime = new Date();
		result = new Result(flag, Util.getFormatTime(endTime.getTime() - startTime.getTime()), Util.getFormatSize(response));
		return result;
	}

	/**
	 * 上传单个文件.
	 * 
	 * @param localFile 本地文件
	 * @return true上传成功, false上传失败
	 * @throws IOException
	 */
	private boolean uploadingSingle(File localFile) throws IOException {
		boolean flag = true;
		InputStream inputStream = new FileInputStream(localFile);
		response += (double) inputStream.available() / 1;
		String remoteName = currentPath + "/" + localFile.getName();
		remoteName = new String(remoteName.getBytes("UTF-8"), "iso-8859-1");
		flag = ftpClient.storeFile(remoteName, inputStream);
		inputStream.close();
		return flag;
	}

	/**
	 * 上传多个文件.
	 * 
	 * @param localFile 本地文件夹
	 * @return true上传成功, false上传失败
	 * @throws IOException
	 */
	private boolean uploadingMany(File localFile) throws IOException {
		boolean flag = true;
		if (!currentPath.equals(REMOTE_PATH)) {
			currentPath = currentPath + REMOTE_PATH + localFile.getName();
		} else {
			currentPath = currentPath + localFile.getName();
		}
		if (!ftpClient.changeWorkingDirectory(currentPath)) {
			ftpClient.makeDirectory(currentPath);
			ftpClient.changeWorkingDirectory(currentPath);
		}
		File[] files = localFile.listFiles();
		for (File file : files) {
			if (file.isHidden()) {
				continue;
			}
			if (file.isDirectory()) {
				flag = uploadingMany(file);
			} else {
				flag = uploadingSingle(file);
				if (flag && isDeleteFileAfterUpload && file.exists()) {
					file.delete();
				}
			}
		}
		return flag;
	}
}
