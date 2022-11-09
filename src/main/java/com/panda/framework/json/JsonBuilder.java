package com.panda.framework.json;

public class JsonBuilder {

	public static byte[] getJson(State state, byte[] msg) {
		JsonDocument doc = new JsonDocument();
		doc.startObject();
		doc.createElement("state", state.getState());
		doc.append("data", msg);
		doc.endObject();
		
		return doc.toByte();
	}
	
	public static byte[] getJson(State state, String msg) {
		JsonDocument doc = new JsonDocument();
		doc.startObject();
		doc.createElement("state", state.getState());
		doc.append("data", msg);
		doc.endObject();
		
		return doc.toByte();
	}
	
	public static byte[] getJson(State state, JsonDocument msg) {
		JsonDocument doc = new JsonDocument();
		doc.startObject();
		doc.createElement("state", state.getState());
		doc.append("data", msg);
		doc.endObject();
		
		return doc.toByte();
	}
	
}
