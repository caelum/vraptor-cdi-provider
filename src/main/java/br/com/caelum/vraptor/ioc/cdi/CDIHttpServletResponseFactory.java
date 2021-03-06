package br.com.caelum.vraptor.ioc.cdi;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.RequestScoped;

@RequestScoped
@Alternative
@Priority(1000)
public class CDIHttpServletResponseFactory implements ComponentFactory<HttpServletResponse>{

	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;

	public MutableResponse getInstance(){
		return cdiRequestInfoFactory.producesRequestInfo().getResponse();
	}
}
