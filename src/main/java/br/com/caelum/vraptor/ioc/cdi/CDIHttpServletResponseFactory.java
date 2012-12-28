package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ComponentFactory;

@ApplicationScoped
public class CDIHttpServletResponseFactory implements ComponentFactory<HttpServletResponse>{

	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;

	@Default
	@VraptorPreference
	public MutableResponse getInstance(){
		return cdiRequestInfoFactory.producesRequestInfo().getResponse();
	}
}
