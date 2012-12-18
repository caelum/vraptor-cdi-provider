package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.util.AnnotationLiteral;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

import br.com.caelum.vraptor.ioc.ComponentFactory;

public class ComponentFactoryExtension implements Extension{

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addProducesToComponentFactory(@Observes ProcessAnnotatedType pat){
		final AnnotatedType defaultType = pat.getAnnotatedType();		
		if(ComponentFactory.class.isAssignableFrom(pat.getAnnotatedType().getJavaClass())){
			try{
				AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder();
				builder.readFromType(defaultType);
				builder.addToMethod(defaultType.getJavaClass().getMethod("getInstance"),new AnnotationLiteral<Produces>() {});			
				pat.setAnnotatedType(builder.create());
			}
			catch(Exception exception){
				throw new RuntimeException(exception);
			}
		}
	}
}
