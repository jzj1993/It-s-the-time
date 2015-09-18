package com.jzj.alarm.voice;

import org.json.JSONObject;

/**
 * 语义结果类(json格式语义-->解析出的语义)
 * <p>
 * (由科大讯飞语义理解返回的json解析产生)
 * <p>
 *
 * @author jzj
 */
public class SemanticResult {

	// 必须参数
	public int rc; // 应答码,操作成功为0
	public String text; // 用户输入(经过语言纠错后)
	public String service; // 服务名称 "weather"
	public String operation; // 操作编码 "QUERY"

	// 非必须参数
	public Semantic semantic; // 语义结构化表示

	private SemanticResult() {
	}

	public static final SemanticResult fromJson(String json) {
		if (json == null || json.length() == 0)
			return null;
		try {
			return parse(json);
			// return new Gson().fromJson(json, SemanticResult.class);
		} catch (Exception e) {
			return null;
		}
	}

	public Semantic.Slots getSlots() {
		if (semantic == null)
			return null;
		return semantic.slots;
	}

	public static class Semantic {

		public Slots slots;

		public static class Slots {

			public Datetime datetime;
			public String repeat; // 闹铃重复
			public String content; // 闹铃提醒

			public static class Datetime {
				public String date;
				public String time;
			}
		}
	}

	protected static SemanticResult parse(String json) throws Exception {
		SemanticResult r = null;
		JSONObject o = new JSONObject(json);
		int rc = o.getInt("rc");
		if (rc != 0)
			return null;
		r = new SemanticResult();
		if (o.has("text"))
			r.text = o.getString("text");
		if (o.has("service"))
			r.service = o.getString("service");
		if (o.has("operation"))
			r.operation = o.getString("operation");
		if (o.has("semantic")) {
			r.semantic = new Semantic();
			o = o.getJSONObject("semantic");
			if (o.has("slots")) {
				Semantic.Slots s = r.semantic.slots = new Semantic.Slots();
				o = o.getJSONObject("slots");
				if (o.has("repeat"))
					s.repeat = o.getString("repeat");
				if (o.has("content"))
					s.content = o.getString("content");
				if (o.has("datetime")) {
					s.datetime = new Semantic.Slots.Datetime();
					o = o.getJSONObject("datetime");
					if (o.has("date"))
						s.datetime.date = o.getString("date");
					if (o.has("time"))
						s.datetime.time = o.getString("time");
				}
			}
		}
		return r;
	}
}
