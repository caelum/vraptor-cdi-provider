package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

import br.com.caelum.vraptor.core.RequestInfo;

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
		
	public static void clearRequestInfo(){
		requests.set(null);
	}
	
}
