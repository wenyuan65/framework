package com.wy.panda.json;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.wy.panda.serializable.Persion;

public class JsonTest {

	public static void main(String[] args) {
		Persion p = new Persion();
		p.setName("dady");
		p.setAge(38);
		List<Persion> list = new ArrayList<>();
		list.add(new Persion("tony", 10, null));
		list.add(new Persion("jack", 5, null));
		p.setChildren(list);
		
		String jsonString = JSON.toJSONString(p);
		System.out.println(jsonString);
		
//		JsonDocument doc = new JsonDocument();
//		doc.startObject();
//		doc.createElement("name", "dady");
//		doc.createElement("age", 38);
//		doc.createElement("children", list);
//		
////		doc.startArray("children");
////		doc.startObject();
////		doc.createElement("name", "tony");
////		doc.createElement("age", "10");
////		doc.createElement("children", null);
////		doc.endObject();
////		doc.startObject();
////		doc.createElement("name", "jack");
////		doc.createElement("age", "5");
////		doc.createElement("children", null);
////		doc.endObject();
////		doc.endArray();
//		doc.createElement("attach", 123);
//		doc.endObject();
//		System.out.println(doc.toString());
		
		JsonDocument doc2 = new JsonDocument();
		doc2.startArray();
		doc2.append(list.get(0));
		doc2.append(list.get(1));
		doc2.endArray();
		
		JsonDocument doc = new JsonDocument();
		doc.startObject();
		doc.createElement("name", "dady");
		doc.createElement("age", 38);
//		doc.createElement("children", list);
		doc.append("children", doc2);
//		doc.append("children", doc2.toString());
		doc.createElement("attach", 123);
		doc.endObject();
		System.out.println(doc.toString());
	}
	
}
