package com.jzj.alarm.voice;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.iflytek.cloud.speech.ErrorCode;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechListener;
import com.iflytek.cloud.speech.SpeechUnderstander;
import com.iflytek.cloud.speech.SpeechUnderstanderListener;
import com.iflytek.cloud.speech.SpeechUser;
import com.iflytek.cloud.speech.SpeechUser.Login_State;
import com.iflytek.cloud.speech.UnderstanderResult;

import com.jzj.util.Debug;

/**
 * 语义理解
 * <p>
 * 注意：
 * <p>
 * 1.需要库文件libs/Msc.jar, libs/armeabi/libmsc.so
 * <p>
 * 2.需要权限
 * <p>
 * <!-- 网络访问 -->
 * <p>
 * <uses-permission android:name="android.permission.INTERNET" />
 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
 * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
 * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
 * <p>
 * <!-- 录音 -->
 * <p>
 * <uses-permission android:name="android.permission.RECORD_AUDIO" />
 * <p>
 * 
 * @author jzj
 */
public class Understander {

	// public static final int MSG_INIT_SUCCESS = 0;
	// public static final int MSG_INIT_FAILED = 1;

	public static final int MSG_GET_RESULT = 2;
	public static final int MSG_NO_RESULT = 3;
	public static final int MSG_NET_ERROR = 4;
	public static final int MSG_OTHER_ERROR = 6;

	private static Understander ud;

	private final static String appKey = "5354ce18";
	// private final String appKey = "532d6f16"; // TODO

	private final SpeechUnderstander speechUd;
	private Handler handler;

	/**
	 * 实例化 并登陆
	 * 
	 * @param context
	 * @param handler
	 */
	private Understander(Context context) {
		speechUd = SpeechUnderstander.createUnderstander(context);
		if (!hasLogined())
			login(context);
		this.setParam();
	}

	private final void setParam() {
		speechUd.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);
		speechUd.setParameter(SpeechConstant.DOMAIN, "iat");
		speechUd.setParameter(SpeechConstant.VAD_BOS, "3000");
		speechUd.setParameter(SpeechConstant.VAD_EOS, "2000");
		speechUd.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
	}

	public static final Understander getInstance(Context context) {
		if (ud == null)
			ud = new Understander(context);
		return ud;
	}

	public static final void login(Context context) {
		SpeechUser.getUser().login(context, null, null, "appid=" + appKey,
				new SpeechListener() {

					@Override
					public void onEvent(int arg0, Bundle arg1) {
					}

					@Override
					public void onData(byte[] arg0) {
					}

					@Override
					public void onCompleted(SpeechError arg0) {
					}
				});
	}

	public static final boolean hasLogined() {
		return SpeechUser.getUser().getLoginState() == Login_State.Logined;
	}

	public final void testJson(String s) {
		if (Debug.DBG)
			Debug.log(this, s);
		sendMsg(MSG_GET_RESULT, SemanticResult.fromJson(s));
	}

	/** 启动语义理解 **/
	public final void start(Handler handler) {
		if (handler != null && this.handler != handler) {
			this.handler = handler;
		}
		if (speechUd.isUnderstanding()) {
			speechUd.cancel();
		}
		speechUd.startUnderstanding(listener);
	}

	/** 正在理解 **/
	public final boolean isUnderstanding() {
		return this.speechUd.isUnderstanding();
	}

	/** 取消识别 **/
	public final void cancel() {
		speechUd.cancel();
	}

	/** 停止 **/
	public final void stop() {
		speechUd.stopUnderstanding();
	}

	/** 应用停止时释放资源 **/
	public final void destroy() {
		speechUd.destroy();
	}

	private final void sendMsg(int what) {
		if (handler != null)
			Message.obtain(handler, what).sendToTarget();
	}

	private final void sendMsg(int what, Object obj) {
		if (handler != null)
			Message.obtain(handler, what, obj).sendToTarget();
	}

	private SpeechUnderstanderListener listener = new SpeechUnderstanderListener() {

		@Override
		public void onEvent(int eventType, int arg1, int arg2, String msg) {
		}

		@Override
		public void onBeginOfSpeech() {
		}

		@Override
		public void onVolumeChanged(int volume) {
		}

		@Override
		public void onEndOfSpeech() {
		}

		@Override
		public void onError(SpeechError e) {
			int error = MSG_OTHER_ERROR;
			if (e != null) {
				switch (e.getErrorCode()) {
				case ErrorCode.ERROR_NET_EXPECTION: // 网络异常
				case ErrorCode.ERROR_NETWORK_TIMEOUT: // 网络连接超时
				case ErrorCode.ERROR_NO_NETWORK: // 无有效的网络连接
					error = MSG_NET_ERROR;
					break;
				// case ErrorCode.ERROR_NO_SPPECH:
				// case 10118: // 您好像没有说话哦.
				// default:
				// error = MSG_NO_SPEECH;
				// break;
				}
			}
			sendMsg(error);
		}

		@Override
		public void onResult(UnderstanderResult result) {
			if (result != null) {
				final String s = result.getResultString();
				Debug.log(this, s);
				sendMsg(MSG_GET_RESULT, SemanticResult.fromJson(s));
			} else {
				sendMsg(MSG_NO_RESULT);
			}
		}
	};
}
