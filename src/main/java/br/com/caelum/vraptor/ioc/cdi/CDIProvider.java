package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.spring.VRaptorRequestHolder;

public class CDIProvider implements ContainerProvider {

	public static final String BEAN_MANAGER_KEY = "javax.enterprise.inject.spi.BeanManager";
	private CDIBasedContainer container;
	
	public <T> T provideForRequest(RequestInfo request, Execution<T> execution) {
		VRaptorRequestHolder.setRequestForCurrentThread(request);
		try {
			return execution.insideRequest(container);
		} finally {
			VRaptorRequestHolder.resetRequestForCurrentThread();
		}
	}

	public void stop() {
	}

	public void start(ServletContext context) {
		BeanManager bm = (BeanManager) context.getAttribute(BEAN_MANAGER_KEY);
		if(bm==null){
			throw new IllegalStateException("ServletContext should have the "+BEAN_MANAGER_KEY+" key");
		}
		container = new CDIBasedContainer(bm);
	}

	public Container getContainer() {
		return container;
	}

}
