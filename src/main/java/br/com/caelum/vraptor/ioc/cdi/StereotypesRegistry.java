package br.com.caelum.vraptor.ioc.cdi;

import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import br.com.caelum.vraptor.ioc.StereotypeHandler;

public class StereotypesRegistry {

	private BeanManager bm;

	public StereotypesRegistry(BeanManager bm) {
		this.bm = bm;
	}
	
	public void configure(Iterable<StereotypeHandler> stereotypesHandler){
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
