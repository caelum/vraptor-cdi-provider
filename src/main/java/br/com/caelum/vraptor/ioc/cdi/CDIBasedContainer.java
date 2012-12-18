package br.com.caelum.vraptor.ioc.cdi;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.ioc.Container;

public class CDIBasedContainer implements Container, ComponentRegistry {

	private BeanManager beanManager;
	private static final Logger logger = LoggerFactory
			.getLogger(CDIBasedContainer.class);

	@Inject
	public CDIBasedContainer(BeanManager beanManager) {
		this.beanManager = beanManager;
	}

	@Deprecated
	protected CDIBasedContainer() {
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public <T> T instanceFor(Class<T> type) {
		Set beans = beanManager.getBeans(type);
		Bean bean = (Bean) beanManager.resolve(beans);
		CreationalContext ctx = beanManager.createCreationalContext(bean);
		return (T) beanManager.getReference(bean, type, ctx);
	}

	public <T> boolean canProvide(Class<T> type) {
		return !beanManager.getBeans(type).isEmpty();
	}

	public void register(Class<?> requiredType, Class<?> componentType) {
		// it is not possible using CDI. We can only registrer on the container
		// startup.
		logger.debug("Should register " + requiredType + " associated with "
				+ componentType);
	}

	public void deepRegister(Class<?> componentType) {
		logger.debug("Should deep register " + componentType);
	}

}
