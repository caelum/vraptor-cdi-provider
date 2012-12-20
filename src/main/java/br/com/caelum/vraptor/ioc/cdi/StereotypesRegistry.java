package br.com.caelum.vraptor.ioc.cdi;

import java.util.ArrayList;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.ioc.StereotypeHandler;

public class StereotypesRegistry {

	private BeanManager bm;
	private BeanManagerUtil beanManagerUtil;

	public StereotypesRegistry(BeanManager bm) {
		this.bm = bm;
		beanManagerUtil = new BeanManagerUtil(bm);
	}
	
	public void configure(){
		ArrayList<StereotypeHandler> stereotypesHandler = new ArrayList<StereotypeHandler>();
		for(Class<? extends StereotypeHandler> klass : BaseComponents.getStereotypeHandlers()){
			stereotypesHandler.add(beanManagerUtil.instanceFor(klass));
		}

		Set<Bean<?>> beans = bm.getBeans(Object.class);		
		for (Bean<?> bean : beans) {
			for (StereotypeHandler handler : stereotypesHandler) {
				if (bean.getBeanClass().isAnnotationPresent(handler.stereotype())) {
					handler.handle(bean.getBeanClass());
				}
			}
		}		
	}
	
	
}
