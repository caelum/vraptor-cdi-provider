package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

@ApplicationScoped
public class CDIHttpSessionFactory {
	
	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;	

	@Produces
	@Default
	@VraptorPreference
	public HttpSession producesSession(){
		return cdiRequestInfoFactory.producesRequestInfo().getRequest().getSession();
	}
}
