package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.servlet.FilterChain;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ComponentFactory;

@ApplicationScoped
public class CDIFilterChainFactory implements ComponentFactory<FilterChain>{

	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;

	@VraptorPreference
	@Default
	public FilterChain getInstance(){
		return cdiRequestInfoFactory.producesRequestInfo().getChain();
	}
}
