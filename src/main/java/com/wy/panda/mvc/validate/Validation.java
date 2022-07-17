package com.wy.panda.mvc.validate;

public class Validation {

	private Rule rule;
	
	private String value;

	public Validation(Rule rule, String value) {
		this.rule = rule;
		this.value = value;
	}
	
	public byte[] check() {
		return this.rule.check(value);
	}

	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
