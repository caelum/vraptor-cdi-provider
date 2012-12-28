package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ComponentFactory;

/**
 * It is a isolated factory because some containers could provide 
 * @author Alberto Souza
 *
 */
@ApplicationScoped
public class CDIHttpServletRequestFactory implements ComponentFactory<HttpServletRequest>{

	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;
	
	@Default
	@VraptorPreference
	public MutableRequest getInstance(){
		return cdiRequestInfoFactory.producesRequestInfo().getRequest();
	}

}
