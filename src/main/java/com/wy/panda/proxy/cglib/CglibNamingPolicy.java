package com.wy.panda.proxy.cglib;

import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.Predicate;

public class CglibNamingPolicy extends DefaultNamingPolicy {

	@Override
	public String getClassName(String prefix, String source, Object key,
			Predicate names) {
		if (prefix == null) {
			String name = this.getClass().getPackage().getName();
            prefix = name + ".Object";
        } else if (prefix.startsWith("java")) {
            prefix = "$" + prefix;
        }
		
        String base = prefix + "Proxy$" +  source.substring(source.lastIndexOf('.') + 1) + getTag();
        
        String attempt = base;
        int index = 2;
        while (names.evaluate(attempt))
            attempt = base + "_" + index++;
        return attempt;
	}

}
