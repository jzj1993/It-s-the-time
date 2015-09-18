package com.jzj.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * File文件扩展工具类
 * 
 * @author jzj
 * @since 2014-08-24
 * @see java.nio.charset.Charset
 */
public class FileUtils {

	/**
	 * 从输入流中写数据到指定路径的文件中
	 * 往SDcard中写文件，需加权限
	 * <p>
	 * < uses-permission
	 * android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	 * 
	 * @param is
	 *            输入流
	 * @param dst
	 *            目标文件路径
	 * @param replace
	 *            如果目标文件存在，是否替换
	 * @return
	 */
	public static boolean writeStreamToFile(InputStream is, String dst,
			boolean replace) {
		return writeStreamToFile(is, new File(dst), replace);
	}

	/**
	 * 从输入流中写数据到一个文件中
	 * 往SDcard中写文件，需加权限
	 * <p>
	 * < uses-permission
	 * android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	 * 
	 * @param is
	 *            输入流
	 * @param file
	 *            目标文件
	 * @param replace
	 *            如果目标文件存在，是否替换
	 */
	public static boolean writeStreamToFile(InputStream is, File file,
			boolean replace) {

		OutputStream os = null;

		try {
			if (file.exists()) {
				if (replace) {
					file.delete();
				}
			} else {
				file.getParentFile().mkdirs();
			}
			os = new FileOutputStream(file);
			final byte[] buffer = new byte[1024];
			int read;
			while ((read = is.read(buffer)) != -1) {
				os.write(buffer, 0, read);
			}
			os.flush();
		} catch (Exception e) {
			if (Debug.DBG)
				e.printStackTrace();
			return false;
		} finally {
			try {
				os.close();
				is.close();
			} catch (IOException e) {
				if (Debug.DBG)
					e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 读取文本文件
	 * 
	 * @param in
	 * @param enc
	 *            编码UTF-8,gbk等
	 * @return 文件不存在或出错返回null
	 * @see java.nio.charset.Charset
	 */
	public static String readTextFile(InputStream in, String enc) {
		try {
			InputStreamReader is = new InputStreamReader(in, enc);
			BufferedReader reader = new BufferedReader(is);
			String line;
			StringBuilder b = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				b.append(line);
				b.append('\n');
			}
			return b.toString();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 读取文本文件(UTF-8编码)
	 * 
	 * @param in
	 * @return 文件不存在或出错返回null
	 */
	public static String readTextFile(InputStream in) {
		return readTextFile(in, "UTF-8");
	}

	/**
	 * 读取文本文件
	 * 
	 * @param file
	 * @param enc
	 *            编码UTF-8,gbk等
	 * @return 文件不存在或出错返回null
	 * @see java.nio.charset.Charset
	 */
	public static String readTextFile(File file, String enc) {
		try {
			if (file != null && file.exists() && file.isFile()) {
				InputStream in = new FileInputStream(file);
				return readTextFile(in, enc);
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 读取文本文件(UTF-8编码)
	 * 
	 * @param file
	 * @return 文件不存在或出错返回null
	 */
	public static String readTextFile(File file) {
		return readTextFile(file, "UTF-8");
	}

	/**
	 * 读取文本文件
	 * 
	 * @param filePath
	 * @param enc
	 *            编码UTF-8,gbk等
	 * @return 文件不存在或出错返回null
	 * @see java.nio.charset.Charset
	 */
	public static String readTextFile(String filePath, String enc) {
		return readTextFile(new File(filePath));
	}

	/**
	 * 读取文本文件(UTF-8编码)
	 * 
	 * @param filePath
	 * @return 文件不存在或出错返回null
	 */
	public static String readTextFile(String filePath) {
		return readTextFile(filePath, "UTF-8");
	}
}
