package com.jzj.util;

import java.io.File;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.content.pm.PackageInfo;

/**
 * App错误控制
 *
 * @author jzj
 * @since 2014-08-24
 */
public class AppErrorController {

	/**
	 * 设置在发生未捕获的异常时，写入日志文件并退出程序
	 *
	 * @param context
	 */
	public static void setLogAndExitForException(final Context context) {

		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				try {
					PrintWriter writer = new PrintWriter(
							getErrorLogFilePath(context));
					ex.printStackTrace(writer);
					writer.flush();
					writer.close();
				} catch (Exception e) {
				}
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
	}

	/**
	 * 获取程序上次运行错误日志并删除日志文件
	 *
	 * @param context
	 * @return 如果运行正常，没有错误日志，返回null
	 */
	public static String getLastErrorLogAndDel(Context context) {
		File f = new File(getErrorLogFilePath(context));
		final String ex = FileUtils.readTextFile(f);
		if (f.exists())
			f.delete();
		if (ex != null && ex.length() > 0)
			return ex;
		else
			return null;
	}

	/**
	 * 发送错误报告给开发者
	 * 内容格式：前缀+App信息+设备信息+Exception信息
	 *
	 * @param context
	 *            context
	 * @param appNameResId
	 *            appName
	 * @param prefix
	 *            邮件内容前缀
	 * @param ex
	 *            错误信息
	 * @param title
	 *            打开Intent选择邮件App的标题
	 * @param subject
	 *            邮件主题
	 * @param to
	 *            邮箱
	 */
	public static void sendBugReportEmail(Context context, int appNameResId,
										  String prefix, String ex, String title, String subject,
										  String... to) {

		final String spliter = "\n\n-----------------------\n\n";

		StringBuilder b = new StringBuilder();

		// Prefix
		if (prefix != null && prefix.length() > 0) {
			b.append(prefix);
			b.append(spliter);
		}

		// AppInfo
		b.append("AppName: ").append(context.getString(appNameResId));
		try {
			PackageInfo pi = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			b.append("\nPackageName: ").append(pi.packageName);
			b.append("\nVersionCode: ").append(pi.versionCode);
			b.append("\nVersionName: ").append(pi.versionName);
		} catch (Exception e) {
			if (Debug.DBG)
				e.printStackTrace();
		}
		b.append(spliter);

		// DeviceInfo
		b.append("Device Infomation:\n\n");
		b.append(SystemUtils.getDeviceInfo());
		b.append(spliter);

		// Exception
		b.append("Exception.printStackTrace:\n\n").append(ex);
		b.append(spliter);

		final String content = b.toString();

		SystemUtils.sendEmail(context, title, subject, content, to);
	}

	private static final String getErrorLogFilePath(Context context) {
		return context.getFilesDir().getPath() + "/error.log";
	}
}
