package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import br.com.caelum.vraptor.ioc.ComponentFactory;

public class ComponentFactoryExtension implements Extension{

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addProducesToComponentFactory(@Observes ProcessAnnotatedType pat){
		final AnnotatedType defaultType = pat.getAnnotatedType();		
		if(ComponentFactory.class.isAssignableFrom(pat.getAnnotatedType().getJavaClass())){
			pat.setAnnotatedType(new ComponentFactoryAnnotatedTypeCreator().create(defaultType.getJavaClass()));
		}
	}
}
