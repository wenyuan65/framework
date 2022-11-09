package com.panda.framework.mvc.view;

import java.util.HashMap;
import java.util.Map;

public class ViewManager {
	
	private View defaultView = null;
	
	private Map<String, View> viewMap = new HashMap<>();
	
	public ViewManager(View defaultView) {
		this.defaultView = defaultView;
	}

	public void addView(String name, View view) {
		viewMap.put(name, view);
	}
	
	public View getView(String name) {
		View result = viewMap.get(name);
		return result != null ? result : defaultView;
	}

}
