package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.FilterChain;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

@ApplicationScoped
public class CDIFilterChainFactory {

	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;

	@Produces
	@VraptorPreference
	@Default
	public FilterChain producesFilterChain(){
		return cdiRequestInfoFactory.producesRequestInfo().getChain();
	}
}
