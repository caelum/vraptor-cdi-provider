package br.com.caelum.vraptor.ioc.cdi;

import java.util.Map.Entry;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessProducer;

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
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.FakeInterceptorHandlerFactory;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.MySessionComponent;
import br.com.caelum.vraptor.ioc.TheComponentFactory;
import br.com.caelum.vraptor.ioc.fixture.ComponentFactoryInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.CustomComponentInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.CustomComponentWithLifecycleInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.DependentOnSomethingFromComponentFactory;
import br.com.caelum.vraptor.ioc.spring.components.DummyComponentFactory;
import br.com.caelum.vraptor.serialization.HibernateProxyInitializer;
import br.com.caelum.vraptor.serialization.NullProxyInitializer;
import br.com.caelum.vraptor.serialization.ProxyInitializer;
import br.com.caelum.vraptor.util.test.MockLocalization;
import br.com.caelum.vraptor.validator.MessageInterpolatorFactory;
import br.com.caelum.vraptor.validator.MethodValidatorCreator;
import br.com.caelum.vraptor.validator.ValidatorCreator;
import br.com.caelum.vraptor.validator.ValidatorFactoryCreator;

public class TestExtension implements Extension{
	
	public void processProducer(@Observes ProcessProducer<?, ?> ppd, BeanManager bm) {
	}
	
	public void beforeBeanDiscovey(@Observes BeforeBeanDiscovery discovery, BeanManager bm) {
		discovery.addAnnotatedType(bm.createAnnotatedType(ServletContainerFactory.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(MessageInterpolatorFactory.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(ValidatorFactoryCreator.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(EncodingHandlerFactory.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(ValidatorCreator.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(DummyComponentFactory.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(MethodValidatorCreator.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(TheComponentFactory.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(ComponentFactoryInTheClasspath.class));
	}	
	
	public void loadClasses(@Observes AfterBeanDiscovery discovery,BeanManager beanManager){
		CDIRegistry cdiRegistry = new CDIRegistry();
		cdiRegistry.init(discovery, beanManager);

		for (Entry<Class<?>, Class<?>> entry : BaseComponents
				.getApplicationScoped().entrySet()) {
			boolean isProxyInitializer = entry.getValue().equals(
					HibernateProxyInitializer.class);
			boolean isInterceptorHandlerFactorty = entry.getValue().equals(
					DefaultInterceptorHandlerFactory.class);
			if (!isProxyInitializer && !isInterceptorHandlerFactorty) {
				cdiRegistry.register(entry.getKey(), entry.getValue());
			}
		}
		for (Entry<Class<?>, Class<?>> entry : BaseComponents
				.getRequestScoped().entrySet()) {
			if (!entry.getValue().equals(JstlLocalization.class)) {
				cdiRegistry.register(entry.getKey(), entry.getValue());
			}
		}
		for (Entry<Class<?>, Class<?>> entry : BaseComponents
				.getPrototypeScoped().entrySet()) {
			cdiRegistry.register(entry.getKey(), entry.getValue());
		}
		cdiRegistry.register(ServletContainerFactory.class,ServletContainerFactory.class);
		cdiRegistry.register(ComponentRegistry.class, CDIRegistry.class);
		cdiRegistry.register(GenericContainerTest.MyAppComponent.class,GenericContainerTest.MyAppComponent.class);
		cdiRegistry.register(GenericContainerTest.MyAppComponentWithLifecycle.class,GenericContainerTest.MyAppComponentWithLifecycle.class);
		cdiRegistry.register(GenericContainerTest.MyRequestComponent.class,GenericContainerTest.MyRequestComponent.class);
		cdiRegistry.register(GenericContainerTest.MyPrototypeComponent.class,GenericContainerTest.MyPrototypeComponent.class);
		cdiRegistry.register(GenericContainerTest.DisposableComponent.class,GenericContainerTest.DisposableComponent.class);
		cdiRegistry.register(GenericContainerTest.StartableComponent.class,GenericContainerTest.StartableComponent.class);
		cdiRegistry.register(CustomComponentInTheClasspath.class,CustomComponentInTheClasspath.class);
		cdiRegistry.register(CustomComponentWithLifecycleInTheClasspath.class,CustomComponentWithLifecycleInTheClasspath.class);
		cdiRegistry.register(Localization.class,MockLocalization.class);
		cdiRegistry.register(LocalDateConverter.class,LocalDateConverter.class);
		cdiRegistry.register(LocalTimeConverter.class,LocalTimeConverter.class);
		cdiRegistry.register(DefaultInterceptorRegistry.class,DefaultInterceptorRegistry.class);
		cdiRegistry.register(ProxyInitializer.class,NullProxyInitializer.class);
		cdiRegistry.register(Container.class,CDIBasedContainer.class);
		cdiRegistry.register(InterceptorHandlerFactory.class,FakeInterceptorHandlerFactory.class);
		cdiRegistry.register(MySessionComponent.class,MySessionComponent.class);
		cdiRegistry.register(TheComponentFactory.class,TheComponentFactory.class);
		cdiRegistry.register(RequestInfo.class,RequestInfo.class);		
		cdiRegistry.register(DependentOnSomethingFromComponentFactory.class,DependentOnSomethingFromComponentFactory.class);
	}
}
