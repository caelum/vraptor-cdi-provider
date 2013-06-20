package br.com.caelum.vraptor.ioc.cdi;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.RequestScoped;

/**
 * It is a isolated factory because some containers could provide 
 * @author Alberto Souza
 *
 */
@RequestScoped
@Alternative
@Priority(1000)
public class CDIHttpServletRequestFactory implements ComponentFactory<MutableRequest>{

	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;
	
	public MutableRequest getInstance(){
		return cdiRequestInfoFactory.producesRequestInfo().getRequest();
	}

}
