package br.com.caelum.vraptor.ioc.cdi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessManagedBean;
import javax.enterprise.inject.spi.ProcessProducer;
import javax.inject.Inject;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.converter.jodatime.LocalDateConverter;
import br.com.caelum.vraptor.converter.jodatime.LocalTimeConverter;
import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.core.DefaultInterceptorHandlerFactory;
import br.com.caelum.vraptor.core.DefaultMethodInfo;
import br.com.caelum.vraptor.core.InterceptorHandlerFactory;
import br.com.caelum.vraptor.core.JstlLocalization;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.EncodingHandlerFactory;
import br.com.caelum.vraptor.interceptor.DefaultInterceptorRegistry;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.FakeInterceptorHandlerFactory;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.GenericContainerTest.DisposableComponent;
import br.com.caelum.vraptor.ioc.MySessionComponent;
import br.com.caelum.vraptor.ioc.ResourceHandler;
import br.com.caelum.vraptor.ioc.StereotypeHandler;
import br.com.caelum.vraptor.ioc.TheComponentFactory;
import br.com.caelum.vraptor.ioc.fixture.ComponentFactoryInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.ConverterInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.CustomComponentInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.CustomComponentWithLifecycleInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.DependentOnSomethingFromComponentFactory;
import br.com.caelum.vraptor.ioc.fixture.InterceptorInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.ResourceInTheClasspath;
import br.com.caelum.vraptor.ioc.spring.components.DummyComponentFactory;
import br.com.caelum.vraptor.serialization.HibernateProxyInitializer;
import br.com.caelum.vraptor.serialization.NullProxyInitializer;
import br.com.caelum.vraptor.serialization.ProxyInitializer;
import br.com.caelum.vraptor.util.test.MockLocalization;
import br.com.caelum.vraptor.validator.MessageInterpolatorFactory;
import br.com.caelum.vraptor.validator.MethodValidatorCreator;
import br.com.caelum.vraptor.validator.ValidatorCreator;
import br.com.caelum.vraptor.validator.ValidatorFactoryCreator;

//TODO tem que tomar cuidado para não registrar o mesmo objeto duas vezes. Infelizmente o beforeBean não permite registrar
public class TestExtension implements Extension{
	
	public void beforeBeanDiscovey(@Observes BeforeBeanDiscovery discovery, BeanManager bm) {
		discovery.addAnnotatedType(bm.createAnnotatedType(ServletContainerFactory.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(DummyComponentFactory.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(TheComponentFactory.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(ComponentFactoryInTheClasspath.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(ResourceInTheClasspath.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(XStreamConvertersFactory.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(SerializationsFactory.class));
		for(Class sterotypeClass : Arrays.asList(BaseComponents.getStereotypeHandlers())){
			discovery.addAnnotatedType(bm.createAnnotatedType(sterotypeClass));
		}
		for (Class<?> cachedComponent : BaseComponents.getCachedComponents().values()) {
			discovery.addAnnotatedType(bm.createAnnotatedType(cachedComponent));
		}		
		for (Entry<Class<?>, Class<?>> entry : BaseComponents
				.getApplicationScoped().entrySet()) {
			boolean isProxyInitializer = entry.getValue().equals(
					HibernateProxyInitializer.class);
			boolean isInterceptorHandlerFactorty = entry.getValue().equals(
					DefaultInterceptorHandlerFactory.class);
			if (!isProxyInitializer && !isInterceptorHandlerFactorty) {
				discovery.addAnnotatedType(bm.createAnnotatedType(entry.getValue()));
			}
		}
		for (Entry<Class<?>, Class<?>> entry : BaseComponents
				.getRequestScoped().entrySet()) {
			if (!entry.getValue().equals(JstlLocalization.class)) {
				discovery.addAnnotatedType(bm.createAnnotatedType(entry.getValue()));
			}
		}
		for (Entry<Class<?>, Class<?>> entry : BaseComponents
				.getPrototypeScoped().entrySet()) {
			discovery.addAnnotatedType(bm.createAnnotatedType(entry.getValue()));
		}
		discovery.addAnnotatedType(bm.createAnnotatedType(CDIBasedContainer.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(GenericContainerTest.MyAppComponent.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(GenericContainerTest.MyAppComponentWithLifecycle.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(GenericContainerTest.MyRequestComponent.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(GenericContainerTest.MyPrototypeComponent.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(GenericContainerTest.DisposableComponent.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(GenericContainerTest.StartableComponent.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(InterceptorInTheClasspath.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(ConverterInTheClasspath.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(CustomComponentInTheClasspath.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(CustomComponentWithLifecycleInTheClasspath.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(MockLocalization.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(LocalDateConverter.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(LocalTimeConverter.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(NullProxyInitializer.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(FakeInterceptorHandlerFactory.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(MySessionComponent.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(TheComponentFactory.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(RequestInfo.class));		
		discovery.addAnnotatedType(bm.createAnnotatedType(DependentOnSomethingFromComponentFactory.class));
	}	
	public void afterDeployment(@Observes AfterDeploymentValidation validation, BeanManager beanManager,Instance<StereotypeHandler> stereotypesHandler){
		Set<Bean<?>> beans = beanManager.getBeans(Object.class);		
		for (Bean<?> bean : beans) {
			for (StereotypeHandler handler : stereotypesHandler) {
				if (bean.getBeanClass().isAnnotationPresent(handler.stereotype())) {
					handler.handle(bean.getBeanClass());
				}
			}
		}
	}
	
	public void processAnnotatedType(@Observes ProcessBean<?> pb){
		
	}

}
