package com.iii360.sup.common.utl.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ObjUtil {
	public static void saveTo(String modelfile, Object save) throws IOException {
		File f = new File(modelfile);
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}

		if (!f.exists()) {
			f.createNewFile();
		}
		ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new GZIPOutputStream(
				new FileOutputStream(modelfile))));
		out.writeObject(save);
		out.close();
	}

	public static Object loadFrom(String modelfile) throws Exception {
		try {
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new GZIPInputStream(
					new FileInputStream(modelfile))));
			Object obj = in.readObject();
			in.close();
			return obj;
		} catch (Exception e) {
			throw e;
		}
	}

}
