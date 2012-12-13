package br.com.caelum.vraptor.ioc.cdi;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import br.com.caelum.vraptor.ioc.AbstractComponentRegistry;
import br.com.caelum.vraptor.ioc.Container;

public class CDIBasedContainer extends AbstractComponentRegistry implements Container {

	private BeanManager beanManager;

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
		//TODO tirar esse if daqui... O vraptor deve chamar um canProvide automatico
		if (canProvide(type)) {
			Bean bean = (Bean) beans.iterator().next();
			CreationalContext ctx = beanManager.createCreationalContext(bean);
			return (T) beanManager.getReference(bean,type,ctx);
		}
		return null;
	}

	public <T> boolean canProvide(Class<T> type) {
		return !beanManager.getBeans(type).isEmpty();
	}

	public void register(Class<?> requiredType, Class<?> componentType) {
		//it is not possible using CDI. We can only registrer on the container startup.
		System.out.println("Should register "+requiredType+" associated with "+componentType);
	}

}
