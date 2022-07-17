package com.wy.panda.jdbc.param;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import com.wy.panda.log.Logger;
import com.wy.panda.log.LoggerFactory;

public class Params {
	
	private static Logger log = LoggerFactory.getLogger(Params.class);

	public static final Params EMPTY = new Params();
	
	private List<Param> params = new ArrayList<>(4);

	public Params() {
	}

	public void addParam(Object obj, int sqlType) {
		params.add(new Param(obj, sqlType));
	}
	
	public List<Param> getParams() {
		return params;
	}

	public void setParams(List<Param> params) {
		this.params = params;
	}
	
	public void appendParams(PreparedStatement ps) {
		if (this.params.size() == 0) {
			return;
		}
		
		try {
			int size = this.params.size();
			for (int i = 0; i < size; i++) {
				Param param = this.params.get(i);
				ColumnValueHandler paramValueSetter = ColumnHandlerFactory
						.createColumnHandler(param.getSqlType());
				
				paramValueSetter.setParamValue(ps, i + 1, param.getValue());
			} 
		} catch (Exception e) {
			log.error("append Params error", e);
		}
	}
	
}
