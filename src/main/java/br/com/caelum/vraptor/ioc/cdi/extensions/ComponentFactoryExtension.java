package br.com.caelum.vraptor.ioc.cdi.extensions;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.cdi.ComponentFactoryAnnotatedTypeBuilderCreator;

//TODO unit tests
public class ComponentFactoryExtension implements Extension{
	
	private final Set<Class<?>> analyzed = new HashSet<Class<?>>();

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addProducesToComponentFactory(@Observes ProcessAnnotatedType pat){
		final AnnotatedType defaultType = pat.getAnnotatedType();		
		Class javaClass = defaultType.getJavaClass();		
		if(!analyzed.contains(javaClass) && ComponentFactory.class.isAssignableFrom(javaClass)){
			AnnotatedTypeBuilder builder = new ComponentFactoryAnnotatedTypeBuilderCreator()
			.create(javaClass);
			pat.setAnnotatedType(builder.create());
		}
		analyzed.add(javaClass);
	}
}
