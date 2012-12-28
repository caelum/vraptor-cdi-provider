package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

@ApplicationScoped
public class CDIHttpServletResponseFactory {

	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;

	@Produces
	@Default
	@VraptorPreference
	public MutableResponse producesResponse(){
		return cdiRequestInfoFactory.producesRequestInfo().getResponse();
	}
}
