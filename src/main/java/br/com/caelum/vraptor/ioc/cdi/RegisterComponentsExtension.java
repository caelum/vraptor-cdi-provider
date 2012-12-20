package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

public class RegisterComponentsExtension implements Extension{

	public void beforeBeanDiscovey(@Observes BeforeBeanDiscovery discovery, BeanManager bm) {
		CDIRegistry registry = new CDIRegistry(discovery, bm);
		registry.configure();
	}
	
	public void afterDeployment(@Observes AfterDeploymentValidation validation,BeanManager beanManager){
		new StereotypesRegistry(beanManager).configure();
	}
}
