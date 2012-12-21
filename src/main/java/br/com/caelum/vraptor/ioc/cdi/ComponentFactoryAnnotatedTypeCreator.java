package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.util.AnnotationLiteral;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

import br.com.caelum.vraptor.VRaptorException;

public class ComponentFactoryAnnotatedTypeCreator {

	@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
	public AnnotatedType create(Class klass){
		try{
			AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder();
			builder.readFromType(klass);
			builder.addToMethod(klass.getMethod("getInstance"),new AnnotationLiteral<Produces>() {});			
			return builder.create();
		}
		catch(Exception exception){
			throw new VRaptorException(exception);
		}
	}
}
