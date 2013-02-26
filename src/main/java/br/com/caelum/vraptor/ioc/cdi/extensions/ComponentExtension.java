package br.com.caelum.vraptor.ioc.cdi.extensions;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.util.AnnotationLiteral;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.ioc.cdi.ScopeInfo;
import br.com.caelum.vraptor.ioc.cdi.ScopesUtil;

public class ComponentExtension implements Extension {
	
	@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
	public void processAnnotatedType(@Observes final ProcessAnnotatedType pat) {		
		final AnnotatedType defaultType = pat.getAnnotatedType();
		if (pat.getAnnotatedType().getJavaClass()
				.isAnnotationPresent(Component.class)) {
			AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder();
			builder.readFromType(defaultType);
			ScopesUtil registry = new ScopesUtil();			
			ScopeInfo scopeInfoFromTheClass = registry.isScoped(defaultType.getJavaClass());
			if(!scopeInfoFromTheClass.hasScope()){
				builder.addToClass(new ScopeInfo(RequestScoped.class).getLiteral());				
			}
			pat.setAnnotatedType(builder.create());
		}
	}
}
