package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessProducer;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.MySessionComponent;
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

public class TestExtension implements Extension{
	
	public void beforeBeanDiscovey(@Observes BeforeBeanDiscovery discovery, BeanManager bm) {
		CDIRegistry registry = new CDIRegistry(discovery, bm);
		registry.configure();
		//just test objects
		AnnotatedType<ServletContainerFactory> containerType = bm.createAnnotatedType(ServletContainerFactory.class);
		discovery.addAnnotatedType(containerType);
		discovery.addAnnotatedType(bm.createAnnotatedType(DummyComponentFactory.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(TheComponentFactory.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(ComponentFactoryInTheClasspath.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(ResourceInTheClasspath.class));
		
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
		discovery.addAnnotatedType(bm.createAnnotatedType(MySessionComponent.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(TheComponentFactory.class));
		discovery.addAnnotatedType(bm.createAnnotatedType(RequestInfo.class));		
		discovery.addAnnotatedType(bm.createAnnotatedType(DependentOnSomethingFromComponentFactory.class));
	}	
	public void afterDeployment(@Observes AfterDeploymentValidation validation, BeanManager beanManager,Instance<StereotypeHandler> stereotypesHandler){
		new StereotypesRegistry(beanManager).configure(stereotypesHandler);
	}

}
