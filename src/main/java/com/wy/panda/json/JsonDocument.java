package com.wy.panda.json;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.util.IOUtils;

/**
 * json组件
 * @author wenyuan
 */
public class JsonDocument {
	
	private static final char BRACES = '{';
	private static final char ANTI_BRACKETS = '}';
	private static final char MIDDLE_BRACKETS = '[';
	private static final char ANTI_MIDDLE_BRACKETS = ']';
	private static final char QUOTATION = '"';
	private static final char COLON = ':';
	private static final char COMMA = ',';
	
	private SerializeWriter out = new SerializeWriter();
	private JSONSerializer serializer = new JSONSerializer(out);
	private boolean first = true;
	
	public void append(Object element) {
		if (!first) {
			out.append(COMMA);
		}
		
		first = false;
		doAppendObject(element);
	}
	
	private void doAppendObject(Object element) {
		try {
			serializer.write(element);
		} finally {
//			out.close();
		}
	}
	
	public void append(byte[] data) {
		if (!first) {
			out.append(COMMA);
		}
		
		first = false;
		out.append(new String(data));
	}

	public void append(String title, byte[] msg) {
		if (!first) {
			out.append(COMMA);
		}
		
		first = false;
		out.append(QUOTATION).append(title).append(QUOTATION).append(COLON);
		
		if (msg != null && msg.length > 0) {
			out.append(new String(msg));
		} else {
			out.append(QUOTATION).append(QUOTATION);
		}
	}
	
	public void append(String title, String msg) {
		if (!first) {
			out.append(COMMA);
		}
		
		first = false;
		out.append(QUOTATION).append(title).append(QUOTATION).append(COLON);
		
		if (StringUtils.isNoneBlank(msg)) {
			serializer.write(msg);
		} else {
			out.append(QUOTATION).append(QUOTATION);
		}
	}
	
	public void append(String title, Object element) {
		if (!first) {
			out.append(COMMA);
		}
		
		first = false;
		out.append(QUOTATION).append(title).append(QUOTATION).append(COLON);
		
		serializer.write(element);
	}
	
	public void append(String title, JsonDocument doc) {
		append(title, doc.toByte());
	}
	
	public void createElement(String title, Object element) {
		if (!first) {
			out.append(COMMA);
		}
		
		first = false;
		out.append(QUOTATION).append(title).append(QUOTATION).append(COLON);
		
		doAppendObject(element);
	}
	
	public void createElement(String title, String element) {
		if (!first) {
			out.append(COMMA);
		}
		
		first = false;
		out.append(QUOTATION).append(title).append(QUOTATION).append(COLON);
		
		serializer.write(element);
	}
	
	public void startObject() {
		if (!first) {
			out.append(COMMA);
			first = true;
		}
		
		out.append(BRACES);
	}
	
	public void startObject(String title) {
		if (!first) {
			out.append(COMMA);
			first = true;
		}
		
		out.append(QUOTATION).append(title).append(QUOTATION).append(COLON).append(BRACES);
	}
	
	public void endObject() {
		out.append(ANTI_BRACKETS);
	}
	
	public void startArray() {
		if (!first) {
			out.append(COMMA);
			first = true;
		}
		
		out.append(MIDDLE_BRACKETS);
	}
	
	public void startArray(String title) {
		if (!first) {
			out.append(COMMA);
			first = true;
		}
		
		out.append(QUOTATION).append(title).append(QUOTATION).append(COLON).append(MIDDLE_BRACKETS);
	}
	
	public void endArray() {
		out.append(ANTI_MIDDLE_BRACKETS);
	}
	
	public byte[] toByte() {
		return out.toBytes(IOUtils.UTF8);
	}
	
	public String toString() {
		return new String(toByte(), IOUtils.UTF8);
	}
	
	public static void main(String[] args) {
		JsonDocument doc2 = new JsonDocument();
		doc2.startObject();
		doc2.createElement("k", "v");
		doc2.endObject();
		
		JsonDocument doc = new JsonDocument();
		doc.startObject();
//		doc.append("em", "");
//		doc.append("em3", "sdsd");
//		doc.append("em4", (String)null);
//		doc.createElement("em2", "");
		
		doc.append("f", doc2);
		doc.endObject();
		
		System.out.println(doc.toString());
	}
	
}
