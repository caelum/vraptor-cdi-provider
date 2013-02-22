package br.com.caelum.vraptor.ioc.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import net.vidageek.mirror.dsl.Matcher;
import net.vidageek.mirror.dsl.Mirror;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;

import br.com.caelum.vraptor.core.BaseComponents;

import com.google.common.collect.Sets;

//TODO unit tests
@SuppressWarnings({ "rawtypes","unchecked","serial" })
public class AddInjectToConstructorExtension implements Extension{

	public void processAnnotatedType(@Observes final ProcessAnnotatedType pat) {
		HashSet<Class<? extends Annotation>> stereotypes = Sets.newHashSet(BaseComponents.getStereotypes());
		for (Class<? extends Annotation> stereotype : stereotypes) {
			for(Annotation foundAnnotation : pat.getAnnotatedType().getAnnotations()){
				if(foundAnnotation.annotationType().equals(stereotype)){
					AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder();
					builder.readFromType(pat.getAnnotatedType());
					tryToDefineInjectConstructor(pat, builder);
					pat.setAnnotatedType(builder.create());
					return ;
				}
			}
		}
	}
	
	private void tryToDefineInjectConstructor(ProcessAnnotatedType pat,
			AnnotatedTypeBuilder builder) {
		Class componentClass = pat.getAnnotatedType().getJavaClass();
		List<Constructor> constructors = new Mirror().on(componentClass).reflectAll().constructorsMatching(new ArgsAndNoInjectConstructorMatcher());
		boolean hasArgsConstructorAndNoInjection = !constructors.isEmpty();
		if(hasArgsConstructorAndNoInjection){
			Constructor constructor = constructors.get(0);
			builder.addToConstructor(constructor, new AnnotationLiteral<Inject>() {});			
		}
	}	
	
	private static class ArgsAndNoInjectConstructorMatcher implements Matcher<Constructor>{

		public boolean accepts(Constructor constructor) {
			boolean hasInject = constructor.isAnnotationPresent(Inject.class);
			boolean hasParameters = constructor.getParameterTypes().length > 0;
			return !hasInject && hasParameters;
		}
		
	}	
}
