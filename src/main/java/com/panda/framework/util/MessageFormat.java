package com.panda.framework.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 消息模板格式化工具
 * @author wenyuan
 */
public class MessageFormat {
	
	private static Pattern splitPattern = Pattern.compile("\\{(\\d+?)\\}");
	
	private static Map<String, MessageFormat> patternCache = new HashMap<>(128);
	
	private String pattern;
	
	private MessageSegement[] segementList;
	
	private MessageFormat(String pattern) {
		this.pattern = pattern;
	}
	
	private void parse() {
		int index = 0;
		List<Object> segementList = new ArrayList<>();
		Matcher m = splitPattern.matcher(pattern);
        while(m.find()) {
        	if (m.start() > index) {
        		segementList.add(new ContentSegement(pattern.substring(index, m.start())));
        	}
        	int orderIdx = Integer.parseInt(pattern.substring(m.start() + 1, m.end() - 1));
        	segementList.add(new IndexSegement(orderIdx));
        	
        	index = m.end();
        }
        
        if (index < pattern.length()) {
        	segementList.add(new ContentSegement(pattern.substring(index)));
        }
        
        this.segementList = segementList.toArray(new MessageSegement[0]);
	}
	
	private String format(Object... args) {
		if (args == null || args.length == 0 || segementList.length == 0) {
			return pattern;
		}
		
		StringBuilder sb = new StringBuilder(pattern.length() + 4);
		for (int i = 0; i < segementList.length; i++) {
			MessageSegement segement = segementList[i];
			segement.format(sb, args);
		}
		
		return sb.toString();
	}

	/**
	 * 格式化消息
	 * @param pattern
	 * @param args
	 * @return
	 */
	public static String format(String pattern, Object... args) {
		MessageFormat formatter = patternCache.get(pattern);
		if (formatter == null) {
			synchronized (MessageFormat.class) {
				formatter = patternCache.get(pattern);
				if (formatter == null) {
					formatter = new MessageFormat(pattern);
					formatter.parse();
					
					patternCache.put(pattern, formatter);
				}
			}
		}
		
		return formatter.format(args);
	}
	
	private interface MessageSegement {
		void format(StringBuilder appender, Object[] args);
	}
	
	private class ContentSegement implements MessageSegement {
		
		private String content;
		
		public ContentSegement(String content) {
			this.content = content;
		}

		@Override
		public void format(StringBuilder appender, Object[] args) {
			appender.append(content);
		}
		
	}
	
	private class IndexSegement implements MessageSegement {
		private int index;
		
		public IndexSegement(int index) {
			this.index = index;
		}

		@Override
		public void format(StringBuilder appender, Object[] args) {
			if (index >= 0 && index < args.length) {
				appender.append(args[index]);
			} else {
				appender.append('{').append(index).append('}');
			}
		}
		
	}
	
}
