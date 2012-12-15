package br.com.caelum.vraptor.ioc.cdi;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.NormalScope;
import javax.enterprise.context.RequestScoped;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.PrototypeScoped;
import br.com.caelum.vraptor.ioc.SessionScoped;

/**
 * This class should be used for bean registration at startup time
 * 
 * @author Alberto Souza
 * 
 */
// TODO create unit tests
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ScopesUtil {

	private List cdiScopes = Arrays.asList(
			javax.enterprise.context.ApplicationScoped.class,
			javax.enterprise.context.SessionScoped.class, Dependent.class,
			RequestScoped.class);

	private List vraptorScopes = Arrays.asList(ApplicationScoped.class,
			SessionScoped.class, br.com.caelum.vraptor.ioc.RequestScoped.class,
			PrototypeScoped.class);

	private HashSet<Annotation> findAnnotations(final Class<?> componentType,
			final List annotationTypes) {
		Annotation[] annotations = componentType.getAnnotations();
		HashSet<Annotation> result = new HashSet<Annotation>();
		for (Annotation annotation : annotations) {
			for (Class findedAnnotation : (List<Class>) annotationTypes) {
				if (annotation.annotationType().equals(findedAnnotation)) {
					result.add(annotation);
				}
			}
		}
		return result;
	}

	private HashSet<Annotation> cdiScopes(final Class<?> componentType) {
		return findAnnotations(componentType, cdiScopes);
	}

	public boolean isScoped(Class<?> clazz) {
		return !cdiScopes(clazz).isEmpty() || !vraptorScopes(clazz).isEmpty();
	}
	
	public boolean isScope(Class<? extends Annotation> annotation){
		return cdiScopes.contains(annotation) || vraptorScopes.contains(annotation);
	}

	private HashSet<Annotation> vraptorScopes(final Class<?> componentType) {
		return findAnnotations(componentType, vraptorScopes);
	}

}
