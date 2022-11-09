package com.panda.framework.jdbc.param;

public class ColumnHandlerFactory {
	
	public static ColumnValueHandler createColumnHandler(int sqlType) throws Exception {
		Class<? extends ColumnValueHandler> paramValueSetterClazz = SqlTypeToColumnHandlerMapper.getParamValueSetterClazz(sqlType);
		if (paramValueSetterClazz == null) {
			throw new Exception("no ParamValueSetter for sqlType:" + sqlType);
		}
		
		try {
			return paramValueSetterClazz.newInstance();
		} catch (Exception e) {
			throw new Exception("generate ParamValueSetter error", e);
		}
	}
	
}
