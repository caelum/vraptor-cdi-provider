package br.com.caelum.vraptor.ioc.cdi;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.servlet.FilterChain;

import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.RequestScoped;

@RequestScoped
@Alternative
@Priority(1000)
public class CDIFilterChainFactory implements ComponentFactory<FilterChain>{

	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;

	public FilterChain getInstance(){
		return cdiRequestInfoFactory.producesRequestInfo().getChain();
	}
}
