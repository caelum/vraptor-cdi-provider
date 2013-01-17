package br.com.caelum.cdi.component;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.GenericContainerTest.MyRequestComponent;
import br.com.caelum.vraptor.ioc.cdi.ComponentToBeProduced;

import com.google.inject.Inject;

@Component
public class CDIComponent implements ComponentFactory {

	private final MyRequestComponent component;
	
	@Inject
	public CDIComponent(MyRequestComponent component) {
		this.component = component;
	}

	public ComponentToBeProduced getInstance() {		
		return new ComponentToBeProduced();
	}
}
