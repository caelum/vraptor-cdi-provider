package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Default;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor.ioc.ComponentFactory;

@ApplicationScoped
@Alternative
public class ServletContextFactory implements ComponentFactory<ServletContext>{

	private ServletContext context;
	
	public void observesContext(@Observes ServletContext context){
		this.context = context;
	}
	
	@ApplicationScoped
	@Default
	@VraptorPreference
	public ServletContext getInstance(){
		return this.context;
	}
}
