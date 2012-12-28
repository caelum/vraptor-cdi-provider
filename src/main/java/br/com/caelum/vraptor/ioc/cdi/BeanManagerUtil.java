package br.com.caelum.vraptor.ioc.cdi;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

public class BeanManagerUtil {

	private BeanManager beanManager;

	public BeanManagerUtil(BeanManager beanManager) {
		this.beanManager = beanManager;
	}

	public <T> T instanceFor(Class<T> type) {
		Set beans = getBeans(type);
		Bean<T> bean = (Bean<T>) beanManager.resolve(beans);
		return instanceFor(bean);
	}
	
	public <T> T instanceFor(Bean<?> bean){
		CreationalContext ctx = beanManager.createCreationalContext(bean);
		return (T) beanManager.getReference(bean, bean.getBeanClass(), ctx);
		
	}
	
	public Set<Bean<?>> getBeans(Class<?> type){
		return beanManager.getBeans(type);
	}
	
}
