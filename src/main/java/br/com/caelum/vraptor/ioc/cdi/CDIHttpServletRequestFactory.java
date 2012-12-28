package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * It is a isolated factory because some containers could provide 
 * @author Alberto Souza
 *
 */
@ApplicationScoped
public class CDIHttpServletRequestFactory {

	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;
	
	@Produces
	@Default
	@VraptorPreference
	public MutableRequest producesRequest(){
		return cdiRequestInfoFactory.producesRequestInfo().getRequest();
	}

}
