package br.com.caelum.vraptor.ioc.cdi;

import java.util.Map.Entry;
import java.util.concurrent.Callable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.weld.context.bound.BoundRequestContext;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.converter.jodatime.LocalDateConverter;
import br.com.caelum.vraptor.converter.jodatime.LocalTimeConverter;
import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.core.DefaultInterceptorHandlerFactory;
import br.com.caelum.vraptor.core.DefaultMethodInfo;
import br.com.caelum.vraptor.core.JstlLocalization;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.EncodingHandlerFactory;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.interceptor.DefaultInterceptorRegistry;
import br.com.caelum.vraptor.interceptor.TopologicalSortedInterceptorRegistry;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.FakeInterceptorHandlerFactory;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.MySessionComponent;
import br.com.caelum.vraptor.ioc.NeedsCustomInstantiation;
import br.com.caelum.vraptor.ioc.TheComponentFactory;
import br.com.caelum.vraptor.ioc.WhatToDo;
import br.com.caelum.vraptor.ioc.fixture.ComponentFactoryInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.CustomComponentInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.CustomComponentWithLifecycleInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.DependentOnSomethingFromComponentFactory;
import br.com.caelum.vraptor.ioc.spring.SpringProviderRegisteringComponentsTest;
import br.com.caelum.vraptor.ioc.spring.VRaptorRequestHolder;
import br.com.caelum.vraptor.serialization.HibernateProxyInitializer;
import br.com.caelum.vraptor.serialization.NullProxyInitializer;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import br.com.caelum.vraptor.test.HttpSessionMock;
import br.com.caelum.vraptor.util.test.MockLocalization;
import static org.mockito.Mockito.mock;

@RunWith(Arquillian.class)
public class CDIProviderRegisteringComponentsTest extends
		SpringProviderRegisteringComponentsTest {

	private int counter;
	@Inject
	private BeanManager beanManager;
	private @Inject BoundRequestContext requestContext;	

	@Produces
	@SessionScoped
	public HttpSessionMock getSession() {
		return new HttpSessionMock(context, "session" + ++counter);
	}

	@Produces
	@RequestScoped
	public HttpServletRequestMock getRequest() {
		return new HttpServletRequestMock(getSession(), mock(
				MutableRequest.class, "request" + counter));
	}

	@Produces
	@RequestScoped
	public final MutableResponse getResponse() {
		return mock(MutableResponse.class, "response" + counter);
	}

	@Produces
	@RequestScoped
	public FilterChain getFilterChain() {
		return mock(FilterChain.class);
	}

	@Produces
	@ApplicationScoped
	public ServletContext producesServletContext() {
		return this.context;
	}

	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive jar = ShrinkWrap.create(JavaArchive.class);
		jar.addPackages(true, "com.thoughtworks.paranamer", "net.sf.cglib",
				"com.thoughtworks.xstream");
		for (Entry<Class<?>, Class<?>> entry : BaseComponents
				.getApplicationScoped().entrySet()) {
			boolean isProxyInitializer = entry.getValue().equals(
					HibernateProxyInitializer.class);
			boolean isInterceptorHandlerFactorty = entry.getValue().equals(
					DefaultInterceptorHandlerFactory.class);
			if (!isProxyInitializer && !isInterceptorHandlerFactorty) {
				jar.addClasses(entry.getKey(), entry.getValue());
			}
		}
		for (Entry<Class<?>, Class<?>> entry : BaseComponents
				.getRequestScoped().entrySet()) {
			if (!entry.getValue().equals(JstlLocalization.class)) {
				jar.addClasses(entry.getKey(), entry.getValue());
			}
		}
		for (Entry<Class<?>, Class<?>> entry : BaseComponents
				.getPrototypeScoped().entrySet()) {
			jar.addClasses(entry.getKey(), entry.getValue());
		}
		jar.addClasses(ComponentRegistry.class, CDIRegistry.class);
		jar.addClasses(GenericContainerTest.MyAppComponent.class);
		jar.addClasses(GenericContainerTest.MyAppComponentWithLifecycle.class);
		jar.addClasses(GenericContainerTest.MyRequestComponent.class);
		jar.addClasses(GenericContainerTest.MyPrototypeComponent.class);
		jar.addClasses(GenericContainerTest.DisposableComponent.class);
		jar.addClasses(GenericContainerTest.StartableComponent.class);
		jar.addClasses(CustomComponentInTheClasspath.class);
		jar.addClasses(ComponentFactoryInTheClasspath.Provided.class);
		jar.addClasses(CustomComponentWithLifecycleInTheClasspath.class);
		jar.addClass(ComponentFactoryInTheClasspath.class);
		jar.addClass(EncodingHandlerFactory.class);
		jar.addClass(DefaultMethodInfo.class);
		jar.addClass(LocalDateConverter.class);
		jar.addClass(LocalTimeConverter.class);
		jar.addClass(DefaultInterceptorRegistry.class);
		jar.addClass(NullProxyInitializer.class);
		jar.addClasses(CDIBasedContainer.class);
		jar.addClasses(FakeInterceptorHandlerFactory.class);
		jar.addClass(TopologicalSortedInterceptorRegistry.class);
		jar.addClass(MySessionComponent.class);
		jar.addClass(TheComponentFactory.class);
		jar.addClass(RequestInfo.class);
		jar.addClass(NeedsCustomInstantiation.class);
		jar.addClass(DependentOnSomethingFromComponentFactory.class);
		jar.addClass(MockLocalization.class);
		jar.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml").addAsServiceProvider(Extension.class,CDIRegistry.class);
		return jar;
	}

	@Override
	protected ContainerProvider getProvider() {
		CDIProvider cdiProvider = new CDIProvider();
		cdiProvider.setBeanManager(beanManager);
		return cdiProvider;
	}

	@Override
	protected <T> T executeInsideRequest(final WhatToDo<T> execution) {
		Callable<T> task = new Callable<T>() {
			public T call() throws Exception {
				T result = null;
				RequestInfo request = new RequestInfo(context, null,
						getRequest(), getResponse());
				VRaptorRequestHolder.setRequestForCurrentThread(request);
				
				result = execution.execute(request, counter);

				VRaptorRequestHolder.resetRequestForCurrentThread();
				return result;
			}
		};
		try {
			return task.call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Test
	public void shouldDisposeAfterRequest() {
		requestContext.activate();
		DisposableComponent comp = provider.getContainer().instanceFor(DisposableComponent.class);
		requestContext.invalidate();
		requestContext.deactivate();
		
		requestContext.activate();
		DisposableComponent comp2 = provider.getContainer().instanceFor(DisposableComponent.class);
		requestContext.invalidate();
		requestContext.deactivate();
		System.out.println(comp==comp2);
//		assertTrue(comp.isDestroyed());
	}	

}
