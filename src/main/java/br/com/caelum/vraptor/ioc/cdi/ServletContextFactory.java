package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.servlet.ServletContext;

@ApplicationScoped
public class ServletContextFactory {

	private ServletContext context;
	
	public void observesContext(@Observes ServletContext context){
		this.context = context;
	}
	
	@Produces
	@ApplicationScoped
	@Default
	@VraptorPreference
	public ServletContext getContext(){
		return this.context;
	}
}
