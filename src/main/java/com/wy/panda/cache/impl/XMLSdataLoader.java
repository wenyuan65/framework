package com.wy.panda.cache.impl;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.wy.panda.cache.SdataLoader;
import com.wy.panda.jdbc.TableFactory;
import com.wy.panda.jdbc.entity.FieldEntity;
import com.wy.panda.jdbc.entity.TableEntity;

public class XMLSdataLoader implements SdataLoader {

	/** 静态库压缩文件中，静态库文件的名称 */
	private static final String XML_FILE_NAME = "sdata.xml";
	/** column对应的静态库字段field */
	private Map<String, Field> columnToFieldMap = new HashMap<>();
	
	private TableFactory tableFactory;
	
	private String path;
	
	@Override
	public <T> List<T> getTable(Class<T> clazz) {
		List<T> resultList = null;
		try {
			InputStream inputStream = clazz.getClassLoader().getResourceAsStream(path);
			// 取zip文件中的xml
			ZipInputStream zipInputStream = new ZipInputStream(inputStream);
			ZipEntry entry = null;
			while ((entry = zipInputStream.getNextEntry()) != null) {
				if (XML_FILE_NAME.equalsIgnoreCase(entry.getName())) {
					resultList = parseXML(zipInputStream, clazz);
					break;
				}
			}
			
			zipInputStream.close();
		} catch (Exception e) {
			throw new RuntimeException("parse xml sdata error, " + clazz.getName() + ":" + e.getMessage());
		}
		
		return resultList != null ? resultList : Collections.emptyList();
	}
	
	private <T> List<T> parseXML(InputStream inputStream, Class<T> clazz) throws Exception {
		TableEntity tableEntity = tableFactory.getTableEntity(clazz);
		String tableName = tableEntity.getTableName();
		
		SAXReader reader = new SAXReader();
		Document doc = reader.read(inputStream);
		Element rootElement = doc.getRootElement();
		Iterator<Element> it = rootElement.elementIterator();
		while (it.hasNext()) {
			Element tableElement = it.next();
			Attribute tableNameAttr = tableElement.attribute("name");
			String value = tableNameAttr.getValue();
			
			if (tableName.equalsIgnoreCase(value)) {
				return parseTableElement(tableElement, clazz);
			}
		}
		
		return Collections.emptyList();
	}

	private <T> List<T> parseTableElement(Element tableElement, Class<T> clazz) throws Exception {
		List<T> resultList = new ArrayList<>();
		TableEntity tableEntity = tableFactory.getTableEntity(clazz);
		
		Iterator<Element> rowIt = tableElement.elementIterator();
		while (rowIt.hasNext()) {
			Element rowElement = rowIt.next();
			
			T instance = clazz.newInstance();
			
			Iterator<Attribute> attrIt = rowElement.attributeIterator();
			while (attrIt.hasNext()) {
				Attribute columnAttr = attrIt.next();
				String columnName = columnAttr.getName();
				String value = columnAttr.getValue();
				// 缓存
				Field field = columnToFieldMap.get(columnName);
				if (field == null) {
					String propertyName = tableEntity.getPropertyName(columnName);
					FieldEntity fieldEntity = tableEntity.getFieldEntity(propertyName);
					field = fieldEntity.getField();
					field.setAccessible(true);
					
					columnToFieldMap.put(columnName, field);
				}
				
				if (field.getType() == int.class) {
					field.setInt(instance, Integer.parseInt(value));
				} else if (field.getType() == String.class) {
					field.set(instance, value);
				} else if (field.getType() == long.class) {
					field.setLong(instance, Long.parseLong(value));
				} else if (field.getType() == double.class) {
					field.setDouble(instance, Double.parseDouble(value));
				} else if (field.getType() == float.class) {
					field.setFloat(instance, Float.parseFloat(value));
				} else {
					throw new RuntimeException("unkown field type:" + field.getType().getName());
				}
			}
			
			resultList.add(instance);
		}
		
		return resultList;
	}
	
	public TableFactory getTableFactory() {
		return tableFactory;
	}

	public void setTableFactory(TableFactory tableFactory) {
		this.tableFactory = tableFactory;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
}
