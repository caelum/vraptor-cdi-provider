package br.com.caelum.vraptor.ioc.cdi;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.NormalScope;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;
import br.com.caelum.vraptor.ioc.SessionScoped;

import com.google.common.collect.Sets;

/**
 * This class should be used for bean registration at startup time
 * 
 * @author Alberto Souza
 * 
 */
//TODO create unit tests. Scopes, qualifiers, stereotypes, etc...
public class CDIRegistry implements Extension {

	private AfterBeanDiscovery discovery;
	private BeanManager beanManager;
	private Map<Class,Class> vraptorToCdiScope = new HashMap<Class, Class>();
	
	{
		vraptorToCdiScope.put(ApplicationScoped.class,javax.enterprise.context.ApplicationScoped.class);
		vraptorToCdiScope.put(SessionScoped.class,javax.enterprise.context.SessionScoped.class);
		vraptorToCdiScope.put(PrototypeScoped.class,Dependent.class);
	}

	public void register(final Class<?> requiredType,
			final Class<?> componentType) {
		final InjectionTarget injectionTarget = beanManager
				.createInjectionTarget(beanManager
						.createAnnotatedType(componentType));

		discovery.addBean(new Bean() {

			public Object create(CreationalContext ctx) {
				Object instance = injectionTarget.produce(ctx);
				injectionTarget.inject(instance, ctx);
				injectionTarget.postConstruct(instance);
				return instance;
			}

			public void destroy(Object instance, CreationalContext ctx) {
				injectionTarget.preDestroy(instance);
				injectionTarget.dispose(instance);
				ctx.release();
			}

			public Class getBeanClass() {
				return componentType;
			}

			public Set getInjectionPoints() {
				return injectionTarget.getInjectionPoints();
			}

			public String getName() {
				return componentType.getSimpleName();
			}

			public Set<Annotation> getQualifiers() {
				HashSet<Annotation> qualifiers = findAnnotations(componentType,
						Arrays.asList(Qualifier.class));
				if(qualifiers.isEmpty()){
					qualifiers.add(new AnnotationLiteral<Default>() {});
					qualifiers.add(new AnnotationLiteral<Any>() {});
				}
				return qualifiers;
			}

			private HashSet<Annotation> findAnnotations(
					final Class<?> componentType, final List annotationTypes) {
				Annotation[] annotations = componentType.getAnnotations();
				HashSet<Annotation> result = new HashSet<Annotation>();
				for (Annotation annotation : annotations) {
					for(Class findedAnnotation : (List<Class>)annotationTypes){
						if(annotation.annotationType().isAnnotationPresent(findedAnnotation) || annotation.annotationType().equals(findedAnnotation)){
							result.add(annotation);
						}
					}
				}
				return result;
			}

			public Class getScope() {
				HashSet<Annotation> cdiScopes = findCDIScopes(componentType);
				Class scope = RequestScoped.class;
				if (cdiScopes.iterator().hasNext()) {
					scope = cdiScopes.iterator().next().annotationType();
				}
				HashSet<Annotation> vraptorScopes = findVraptorScopes(componentType);
				if (vraptorScopes.iterator().hasNext()) {
					Annotation vraptorScope = vraptorScopes.iterator().next();					
					scope = vraptorToCdiScope.get(vraptorScope.annotationType());
				}
				HashSet<Annotation> componentScope = findAnnotations(componentType, Arrays.asList(Component.class));
				if (componentScope.iterator().hasNext()) {
					scope = RequestScoped.class;
				}			
				return scope;
			}

			private HashSet<Annotation> findCDIScopes(
					final Class<?> componentType) {
				return findAnnotations(componentType,
						Arrays.asList(NormalScope.class,Dependent.class));
			}

			private HashSet<Annotation> findVraptorScopes(
					final Class<?> componentType) {
				return findAnnotations(componentType,
						Arrays.asList(ApplicationScoped.class,
								SessionScoped.class, PrototypeScoped.class));
			}

			public Set getStereotypes() {
				return findAnnotations(componentType, Arrays.asList(Stereotype.class));
			}

			public Set getTypes() {
				//TODO pegar as interfaces
				return Sets.newHashSet(requiredType,componentType);
			}

			public boolean isAlternative() {
				return false;
			}

			public boolean isNullable() {
				return false;
			}
		});
	}

	public void init(@Observes AfterBeanDiscovery discovery,
			BeanManager beanManager) {
		this.discovery = discovery;
		this.beanManager = beanManager;
	}

}
