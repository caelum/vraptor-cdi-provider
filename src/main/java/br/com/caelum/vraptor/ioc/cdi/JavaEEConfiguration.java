package br.com.caelum.vraptor.ioc.cdi;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("beans")
public class JavaEEConfiguration {
	@XStreamAlias("disable-list")	
	private List<BeanClass> disableList = new ArrayList<BeanClass>();
	
	public boolean isBeanDisabled(Class<?> componentClass){
		return disableList.contains(new BeanClass(componentClass));
	}
}
