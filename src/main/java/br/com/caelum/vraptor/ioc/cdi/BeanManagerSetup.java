package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
/**
 * 
 * @author Alberto Souza and Mario Amaral
 *
 */
public class BeanManagerSetup implements ServletContextListener {

	@Inject
	private BeanManager beanManager;

	public void contextDestroyed(ServletContextEvent event) {
	}

	public void contextInitialized(ServletContextEvent event) {
		if (event.getServletContext().getAttribute(CDIProvider.BEAN_MANAGER_KEY) == null) {
			event.getServletContext().setAttribute(CDIProvider.BEAN_MANAGER_KEY, beanManager);
		}
	}

}
