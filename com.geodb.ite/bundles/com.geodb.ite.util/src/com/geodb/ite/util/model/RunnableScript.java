package com.geodb.ite.util.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import com.geodb.ite.util.services.JS;

public class RunnableScript {

	public static final String HOUR_FORMAT = "HH:mm:ss.SSS";
	private static SimpleDateFormat sdf = new SimpleDateFormat(HOUR_FORMAT);

	static final String FIELD_TIMESTAMP = "timestamp";
	static final String FIELD_CODE = "code";
	static final String FIELD_RESULT = "result";

	private long timestamp;
	private String code;
	private Object result;
	private Exception error;

	@Inject
	private JS js;

	@Inject
	public RunnableScript() {
		timestamp = -1l;
		code = "";
		result = null;
		error = null;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getCode() {
		return code;
	}

	public Object getResult() {
		return result;
	}

	public Exception getError() {
		return error;
	}

	public String getReadable() {
		if (error != null) {
			return date(timestamp) + " - " + error.getLocalizedMessage();
		} else {
			return date(timestamp) + " - " + result;
		}
	}

	private String date(long time) {
		return sdf.format(new Date(time));
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public void setError(Exception error) {
		this.error = error;
	}

	public RunnableScript run() {
		timestamp = System.currentTimeMillis();
		try {
			result = js.eval(code);
		} catch (Exception all) {
			result = null;
			error = all;
		}
		return this;
	}
}
