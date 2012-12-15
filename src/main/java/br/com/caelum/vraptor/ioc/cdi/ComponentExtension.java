package br.com.caelum.vraptor.ioc.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.util.AnnotationLiteral;

import br.com.caelum.vraptor.ioc.Component;

//TODO create unit tests
public class ComponentExtension implements Extension {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void processAnnotatedType(@Observes final ProcessAnnotatedType pat) {
		final AnnotatedType defaultType = pat.getAnnotatedType();
		if (pat.getAnnotatedType().getJavaClass()
				.isAnnotationPresent(Component.class)) {
			AnnotatedType wrapperType = new AnnotatedType() {

				public <T extends Annotation> T getAnnotation(Class<T> clazz) {
					return defaultType.getAnnotation(clazz);
				}

				public Set<Annotation> getAnnotations() {
					Set<Annotation> newAnnotations = new HashSet<Annotation>(defaultType.getAnnotations());
					ScopesUtil registry = new ScopesUtil();
					if(newAnnotations.contains(new AnnotationLiteral<Component>() {}) && !registry.isScoped(pat.getAnnotatedType().getJavaClass())){
						newAnnotations.add(new AnnotationLiteral<RequestScoped>() {});
					}
					return newAnnotations;
				}

				public Type getBaseType() {
					return defaultType.getBaseType();
				}

				public Set<Type> getTypeClosure() {
					return defaultType.getTypeClosure();
				}

				public boolean isAnnotationPresent(
						Class<? extends Annotation> annotation) {
					return defaultType.isAnnotationPresent(annotation);
				}

				public Set<AnnotatedConstructor> getConstructors() {
					return defaultType.getConstructors();
				}

				public Set<AnnotatedField> getFields() {
					return defaultType.getFields();
				}

				public Class getJavaClass() {
					return defaultType.getJavaClass();
				}

				public Set<AnnotatedMethod> getMethods() {
					return defaultType.getMethods();
				}
			};
			pat.setAnnotatedType(wrapperType);
		}
	}
}
