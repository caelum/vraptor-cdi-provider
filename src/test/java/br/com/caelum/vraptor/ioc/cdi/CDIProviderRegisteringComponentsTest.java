package br.com.caelum.vraptor.ioc.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.Bean;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.cdise.weld.ContextController;
import org.hamcrest.MatcherAssert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import br.com.caelum.cdi.component.CDIResourceComponent;
import br.com.caelum.cdi.component.CDISessionComponent;
import br.com.caelum.cdi.component.CDIComponent;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.WhatToDo;
import br.com.caelum.vraptor.ioc.fixture.ComponentFactoryInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.CustomComponentWithLifecycleInTheClasspath;
import br.com.caelum.vraptor.ioc.spring.SpringProviderRegisteringComponentsTest;
import br.com.caelum.vraptor.ioc.spring.VRaptorRequestHolder;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class CDIProviderRegisteringComponentsTest extends
		SpringProviderRegisteringComponentsTest {

	private static CdiContainer cdiContainer;
	private ServletContainerFactory servletContainerFactory = new ServletContainerFactory();

	@BeforeClass
	public static void startCDIContainer() {
		cdiContainer = CdiContainerLoader.getCdiContainer();
		cdiContainer.boot();
		
	}
	
	public static void main(String[] args) throws Exception {
		startCDIContainer();		
		CDIProviderRegisteringComponentsTest test = new CDIProviderRegisteringComponentsTest();
		test.startContexts();
		ContainerProvider provider = test.getProvider();
		provider.start(test.servletContainerFactory.createServletContext());
		Object component = provider.getContainer().instanceFor(CDIComponent.class);
		System.out.println(component);
		test.stopContexts();
	}

	@AfterClass
	public static void shutdownCDIContainer() {
		cdiContainer.shutdown();
	}

	public void startContexts() {
		cdiContainer.getContextControl().startContexts();
	}

	public Map<String,Object> getRequestMap(){
		try{
			Field contextControl = cdiContainer.getContextControl().getClass().getDeclaredField("contextController");
			contextControl.setAccessible(true);
			ContextController contextController = (ContextController) contextControl.get(cdiContainer.getContextControl());
			Field fieldRequestMap = contextController.getClass().getDeclaredField("requestMap");
			fieldRequestMap.setAccessible(true);
			Map<String, Object> requestMap = (Map<String, Object>) fieldRequestMap.get(contextController);
			return requestMap;
			
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	public void stopContexts() {
		cdiContainer.getContextControl().stopContexts();
	}

	public void start(Class<? extends Annotation> scope) {
		cdiContainer.getContextControl().startContext(scope);
	}

	public void stop(Class<? extends Annotation> scope) {
		cdiContainer.getContextControl().stopContext(scope);
	}


	@Override
	protected ContainerProvider getProvider() {
		CDIProvider cdiProvider = new CDIProvider();
		cdiProvider.setBeanManager(cdiContainer.getBeanManager());
		return cdiProvider;
	}

	@Override
	protected <T> T executeInsideRequest(final WhatToDo<T> execution) {
		Callable<T> task = new Callable<T>() {
			public T call() throws Exception {
				start(RequestScoped.class);
				start(SessionScoped.class);				
				RequestInfo request = new RequestInfo(context, null,
						servletContainerFactory.getRequest(),
						servletContainerFactory.getResponse());
				VRaptorRequestHolder.setRequestForCurrentThread(request);

				T result = execution.execute(request, counter);

				VRaptorRequestHolder.resetRequestForCurrentThread();
				stop(SessionScoped.class);
				stop(RequestScoped.class);
				return result;
			}
		};
		try {
			T call = task.call();
			return call;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Object actualInstance(Object instance) {		
		try {
			//sorry, but i have to initialize the weld proxy
			instance.toString();
			java.lang.reflect.Field field = instance.getClass()
					.getDeclaredField("BEAN_INSTANCE_CACHE");
			field.setAccessible(true);
			ThreadLocal mapa = (ThreadLocal) field.get(instance);
			return mapa.get();
		} catch (Exception exception) {
			return instance;
		}
	}
	
	protected <T> T instanceFor(final Class<T> component,
			Container container) {
		T maybeAWeldProxy = container.instanceFor(component);
		return (T)actualInstance(maybeAWeldProxy);
	}	

	@Override
	protected void checkSimilarity(Class<?> component, boolean shouldBeTheSame,
			Object firstInstance, Object secondInstance) {
		if (shouldBeTheSame) {
			MatcherAssert.assertThat("Should be the same instance for "
					+ component.getName(), actualInstance(firstInstance),
					is(equalTo(actualInstance(secondInstance))));
		} else {
			MatcherAssert.assertThat("Should not be the same instance for "
					+ component.getName(), actualInstance(firstInstance),
					is(not(equalTo(actualInstance(secondInstance)))));
		}
	}
	
	@Test
	public void callsPredestroyExactlyOneTime() throws Exception {
		
		MyAppComponentWithLifecycle component = registerAndGetFromContainer(MyAppComponentWithLifecycle.class,
				MyAppComponentWithLifecycle.class);		
		assertThat(component.getCalls(), is(0));
		shutdownCDIContainer();
		assertThat(component.getCalls(), is(1));
		startCDIContainer();
		
	}
	
	@Test
	public void shoudCallPredestroyExactlyOneTimeForComponentsScannedFromTheClasspath() {
		CustomComponentWithLifecycleInTheClasspath component = getFromContainer(CustomComponentWithLifecycleInTheClasspath.class);
		assertThat(component.getCallsToPreDestroy(), is(equalTo(0)));
		shutdownCDIContainer();
		assertThat(component.getCallsToPreDestroy(), is(equalTo(1)));
		startCDIContainer();
	}

	@Test
	public void shoudCallPredestroyExactlyOneTimeForComponentFactoriesScannedFromTheClasspath() {
		ComponentFactoryInTheClasspath componentFactory = getFromContainer(ComponentFactoryInTheClasspath.class);
		assertThat(componentFactory.getCallsToPreDestroy(), is(equalTo(0)));
		shutdownCDIContainer();
		assertThat(componentFactory.getCallsToPreDestroy(), is(equalTo(1)));

		startCDIContainer();
	}
	
	@Ignore
	public void setsAnAttributeOnRequestWithTheObjectTypeName() throws Exception {
	}
	
	@Ignore
	public void setsAnAttributeOnSessionWithTheObjectTypeName() throws Exception {
	}	
	
	@Test
	public void shouldUseComponentFactoryAsProducer() {
		ComponentToBeProduced component = getFromContainer(ComponentToBeProduced.class);
		component.toString();
		assertNotNull(component);
	}
	
	@Test
	public void shouldAddRequestScopeForComponentWithoutScope(){
		Bean<?> bean = cdiContainer.getBeanManager().getBeans(CDIComponent.class).iterator().next();
		assertTrue(bean.getScope().equals(RequestScoped.class));
	}
	
	@Test
	public void shouldNotAddRequestScopeForComponentWithScope(){
		Bean<?> bean = cdiContainer.getBeanManager().getBeans(CDISessionComponent.class).iterator().next();
		assertTrue(bean.getScope().equals(SessionScoped.class));
	}
	
	@Test
	public void shouldStereotypeResourceWithRequestAndNamed(){
		Bean<?> bean = cdiContainer.getBeanManager().getBeans(CDIResourceComponent.class).iterator().next();
		System.out.println(bean.getName());
		assertTrue(bean.getScope().equals(RequestScoped.class));
	}
	

}
