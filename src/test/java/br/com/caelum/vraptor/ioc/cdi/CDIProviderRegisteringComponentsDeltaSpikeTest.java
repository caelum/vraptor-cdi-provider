package br.com.caelum.vraptor.ioc.cdi;

import java.util.concurrent.Callable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.WhatToDo;
import br.com.caelum.vraptor.ioc.spring.SpringProviderRegisteringComponentsTest;
import br.com.caelum.vraptor.ioc.spring.VRaptorRequestHolder;
import br.com.caelum.vraptor.serialization.xstream.XStreamConverters;
import br.com.caelum.vraptor.test.HttpServletRequestMock;
import br.com.caelum.vraptor.test.HttpSessionMock;
import static org.mockito.Mockito.mock;

public class CDIProviderRegisteringComponentsDeltaSpikeTest extends
		SpringProviderRegisteringComponentsTest {
	
	
	
	public static void main(String[] args) {
		CdiContainer cdiContainer = CdiContainerLoader.getCdiContainer();

		// now we gonna boot the CDI container. This will trigger the classpath scan, etc
		cdiContainer.boot();
		CDIBasedContainer vraptorContainer = new CDIBasedContainer(cdiContainer.getBeanManager());

		// and finally we like to start all built-in contexts
		cdiContainer.getContextControl().startContext(RequestScoped.class);
		DisposableComponent instance = vraptorContainer.instanceFor(DisposableComponent.class);
		System.out.println(instance.isDestroyed());
//		Bean<?> bean = cdiContainer.getBeanManager().getBeans(CDIBasedContainer.class).iterator().next();
//		System.out.println(bean.getScope());
		cdiContainer.getContextControl().stopContext(RequestScoped.class);
		
		// now we can use CDI in our SE application. 
		// And there is not a single line of OWB or Weld specific code in your project!
		// finally we gonna stop the container 
		cdiContainer.shutdown();		
	}
		
	private int counter;
	@Inject
	private BeanManager beanManager;

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

}
