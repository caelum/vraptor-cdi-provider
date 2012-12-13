package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.spring.VRaptorRequestHolder;

public class CDIProvider implements ContainerProvider {

	private BeanManager beanManager;
	private CDIBasedContainer container;
	
	public void setBeanManager(BeanManager beanManager) {
		this.beanManager = beanManager;
	}

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
		container = new CDIBasedContainer(beanManager);
	}

	public Container getContainer() {
		return container;
	}

}
