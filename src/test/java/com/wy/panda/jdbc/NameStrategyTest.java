package com.wy.panda.jdbc;

import com.wy.panda.jdbc.name.DefaultNameStrategy;

import junit.framework.Assert;

public class NameStrategyTest {

	public static void main(String[] args) {
		DefaultNameStrategy strategy = new DefaultNameStrategy();
		assert strategy.checkColumnName("aaa_bb_cc");
		assert strategy.checkColumnName("aaa_bb2_cc");
		assert !strategy.checkColumnName("aaa_b2_cc");
		assert !strategy.checkColumnName("aaa_b_cc");
		assert !strategy.checkColumnName("aaa_bB_cc");
		assert !strategy.checkColumnName("aaa_Bb_cc");
		
		assert strategy.columnsNameToPropertyName("aaa_bb_cc").equals("aaaBbCc");
		
		assert strategy.propertyNameToColumnsName("aaaBbCc").equals("aaa_bb_cc");
		assert strategy.propertyNameToColumnsName("aaaBb").equals("aaa_bb");
		assert strategy.propertyNameToColumnsName("aaa").equals("aaa");
		
		assert strategy.tableNameToClassName("player").equals("Player");
		assert strategy.tableNameToClassName("player_attribute").equals("PlayerAttribute");
		assert strategy.tableNameToClassName("player_login_info").equals("PlayerLoginInfo");

		assert strategy.classNameToTableName("Player").equals("player");
		assert strategy.classNameToTableName("PlayerAttribute").equals("player_attribute");
		assert strategy.classNameToTableName("PlayerLoginInfo").equals("player_login_info");
	}
	
}
