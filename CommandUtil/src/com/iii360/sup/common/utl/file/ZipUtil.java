package com.iii360.sup.common.utl.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

import com.iii360.sup.common.utl.LogManager;

public class ZipUtil {
	/**
	 * 自动解apk压缩，使用文件名作为目录名
	 * 
	 * @param path
	 * @param savePath
	 * @return
	 * @throws IOException
	 */
	public static String autoUnapk(String path, String savePath) throws IOException {
		int pi = path.lastIndexOf('.');
		pi = pi < 0 ? path.length() : pi;
		savePath += path.substring(path.lastIndexOf('/'), pi) + "/";
		return unapk(path, savePath);
	}

	/**
	 * 解压apk需要指出完整的解压缩目录
	 * 
	 * @param path
	 * @param savePath
	 * @return
	 * @throws IOException
	 */
	public static String unapk(String path, String savePath) throws IOException {
		savePath = savePath.charAt(savePath.length() - 1) == '/' ? savePath : savePath + '/';
		String zipdir = savePath;
		ZipInputStream zis = null;
		FileOutputStream outputStream = null;
		try {
			zis = new ZipInputStream(new FileInputStream(path));
			java.util.zip.ZipEntry ze = null;
			String fileName = "";
			while ((ze = zis.getNextEntry()) != null) {
				if (ze.isDirectory()) {
					File pa = new File(savePath + ze.getName());
					pa.mkdirs();
				} else {
					fileName = ze.getName();
					int at = fileName.lastIndexOf("/");

					if (at != -1) {
						String filePath = savePath + fileName.substring(0, at);
						File pa = new File(filePath);
						pa.mkdirs();
						outputStream = new FileOutputStream(pa + fileName.substring(at));
						byte[] buffer = new byte[1024];
						int bytesRead = 0;

						while ((bytesRead = zis.read(buffer)) > 0) {
							outputStream.write(buffer, 0, bytesRead);
						}
						outputStream.flush();
					} else {

						outputStream = new FileOutputStream(savePath + fileName);
						byte[] buffer = new byte[1024];
						int bytesRead = 0;
						while ((bytesRead = zis.read(buffer)) > 0) {
							outputStream.write(buffer, 0, bytesRead);
						}
						outputStream.flush();
					}
				}
			}
			return zipdir;
		} finally {
			try {
				if (outputStream != null) {
					outputStream.close();
				}
				if (zis != null) {
					zis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 自动解压缩，使用文件名作为目录名
	 * 
	 * @param path
	 * @param savePath
	 * @return
	 * @throws IOException
	 */
	public static String autoUnzip(String path, String savePath) throws IOException {
		int pi = path.lastIndexOf('.');
		pi = pi < 0 ? path.length() : pi;
		savePath += path.substring(path.lastIndexOf('/'), pi) + '/';
		return unzip(path, savePath);
	}

	/**
	 * 需要指出完整的解压缩目录
	 * 
	 * @param path
	 * @param savePath
	 * @return
	 * @throws IOException
	 */
	public static String unzip(String archive, String decompressDir) throws IOException, FileNotFoundException, ZipException {
		ZipFile zf = null;
		try {
			zf = new ZipFile(archive);// 支持中文

			Enumeration<ZipEntry> e = zf.getEntries();
			while (e.hasMoreElements()) {
				FileOutputStream fout = null;
				BufferedOutputStream bos = null;
				BufferedInputStream bi = null;
				try {
					ZipEntry ze2 = (ZipEntry) e.nextElement();
					String entryName = ze2.getName();
					String path = decompressDir + "/" + entryName;
					if (ze2.isDirectory()) {
						System.out.println("正在创建解压目录 - " + entryName);
						File decompressDirFile = new File(path);
						if (!decompressDirFile.exists()) {
							decompressDirFile.mkdirs();
						}
					} else {
						System.out.println("正在创建解压文件 - " + entryName);
						String fileDir = path.substring(0, path.lastIndexOf("/"));
						File fileDirFile = new File(fileDir);
						if (!fileDirFile.exists()) {
							fileDirFile.mkdirs();
						}
						fout = new FileOutputStream(decompressDir + "/" + entryName);
						bos = new BufferedOutputStream(fout);

						bi = new BufferedInputStream(zf.getInputStream(ze2));
						byte[] readContent = new byte[1024];
						int readCount = bi.read(readContent);
						while (readCount != -1) {
							bos.write(readContent, 0, readCount);
							readCount = bi.read(readContent);
						}
					}
				} finally {
					if (bi != null) {
						bi.close();
					}
					if (bos != null) {
						bos.close();
					}
					if (fout != null) {
						fout.close();
					}
				}
			}
			return decompressDir;
		} finally {
			if (zf != null) {
				zf.close();
			}
		}
	}

	/**
	 * 
	 * @param srcFileString
	 * @param targetFilePath
	 * @param comment
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void appachZipFile(File srcFile, String targetFilePath, String comment) throws IOException {
		FileOutputStream f = new FileOutputStream(targetFilePath);
		CheckedOutputStream csum = new CheckedOutputStream(f, new CRC32());
		ZipOutputStream zos = new ZipOutputStream(csum);
		zos.setEncoding("GBK");
		BufferedOutputStream out = new BufferedOutputStream(zos);
		zos.setComment(comment);
		zos.setMethod(ZipOutputStream.DEFLATED);
		zos.setLevel(Deflater.BEST_COMPRESSION);
		// 开始压缩
		writeZipRecursive(zos, out, srcFile);
		out.close();
	}

	private static void writeZipRecursive(ZipOutputStream zos, BufferedOutputStream bo, File srcFile) throws IOException {
		if (srcFile.isDirectory()) {
			File srcFiles[] = srcFile.listFiles();
			for (int i = 0; i < srcFiles.length; i++) {
				writeZipRecursive(zos, bo, srcFiles[i]);
			}
		} else {
			String filePath = srcFile.getAbsolutePath().substring(srcFile.getAbsolutePath().lastIndexOf("/") + 1);
			BufferedInputStream bi = new BufferedInputStream(new FileInputStream(srcFile));
			ZipEntry zipEntry = new ZipEntry(filePath);
			zos.putNextEntry(zipEntry);
			byte[] buffer = new byte[8192];
			int readCount;
			while ((readCount = bi.read(buffer)) != -1) {
				bo.write(buffer, 0, readCount);
			}
			bo.flush();
			bi.close();
		}

	}

	/**
	 * 
	 * @param file
	 *            要压缩的文件
	 * @param zipFile
	 *            压缩文件存放地方
	 */
	public static void zip(File file, File zipFile) {
		ZipOutputStream outputStream = null;
		try {
			outputStream = new ZipOutputStream(new FileOutputStream(zipFile));
			outputStream.setEncoding("utf-8");
			outputStream.setMethod(ZipOutputStream.DEFLATED);
			outputStream.setLevel(Deflater.BEST_COMPRESSION);
			zipFile(outputStream, file, "");
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
			}
		} catch (IOException e) {
			LogManager.e(e.toString());
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				LogManager.e(e.toString());
			}
		}
	}

	/**
	 * 
	 * @param output
	 *            ZipOutputStream对象
	 * @param file
	 *            要压缩的文件或文件夹
	 * @param basePath
	 *            条目根目录
	 */
	private static void zipFile(ZipOutputStream output, File file, String basePath) {
		FileInputStream input = null;
		ZipEntry zipEntry = null;
		try {
			// 文件为目录
			if (file.isDirectory()) {
				// 得到当前目录里面的文件列表
				File list[] = file.listFiles();
				zipEntry = new ZipEntry(basePath + System.getProperties().getProperty("file.separator"));
				zipEntry.setUnixMode(755);// 解决Linux乱码
				output.putNextEntry(zipEntry);
				basePath = basePath.length() == 0 ? "" : basePath + System.getProperties().getProperty("file.separator");
				// 循环递归压缩每个文件
				for (File f : list) {
					zipFile(output, f, basePath);
				}
			} else {
				// 压缩文件
				basePath = (basePath.length() == 0 ? "" : basePath + "/") + file.getName();
				zipEntry = new ZipEntry(basePath);
				zipEntry.setUnixMode(644); // 解决Linux乱码
				output.putNextEntry(zipEntry);
				input = new FileInputStream(file);
				int readLen = 0;
				byte[] buffer = new byte[1024 * 8];
				while ((readLen = input.read(buffer, 0, 1024 * 8)) != -1) {
					output.write(buffer, 0, readLen);
				}
				if (file.exists()) {
					file.delete();
				}
			}
		} catch (Exception e) {
			LogManager.e(e.toString());
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					LogManager.e(e.toString());
				}
			}
		}
	}

}
