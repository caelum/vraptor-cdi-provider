package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;

@ApplicationScoped
public class CDIRequestInfoFactory {

	private static ThreadLocal<RequestInfo> requests = new ThreadLocal<RequestInfo>();
	
	public void observesRequest(@Observes RequestInfo requestInfo){
		requests.set(requestInfo);
	}
	
	@Produces
	public RequestInfo producesRequestInfo(){
		return requests.get();
	}
	
	@Produces
	@Default
	@VraptorPreference
	public MutableRequest producesRequest(){
		return requests.get().getRequest();
	}
	
	@Produces
	@Default
	@VraptorPreference
	public HttpSession producesSession(){
		return requests.get().getRequest().getSession();
	}
	
	@Produces
	@Default
	@VraptorPreference
	public MutableResponse producesResponse(){
		return requests.get().getResponse();
	}
	
	@Produces
	@VraptorPreference
	@Default
	public FilterChain producesFilterChain(){
		return requests.get().getChain();
	}	
		
	public static void clearRequestInfo(){
		requests.set(null);
	}
	
}