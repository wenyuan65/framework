package com.wy.panda.mvc.validate;

import org.apache.commons.lang3.StringUtils;

import com.wy.panda.common.TextUtil;

public class FuncOpenRule implements Rule {

	@Override
	public byte[] check(String condition) {
		if (StringUtils.isBlank(condition)) {
			return null;
		}
		
		int func = Integer.parseInt(condition);
		if (func < 10) {
			return TextUtil.toByte("FUNC NOT OPEN");
		}
		//TODO 功能开放检查
		return null;
	}

}
