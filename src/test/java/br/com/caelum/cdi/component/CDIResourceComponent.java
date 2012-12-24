package br.com.caelum.cdi.component;

import br.com.caelum.vraptor.Resource;

@Resource
public class CDIResourceComponent {

	private CDIComponent component;
	private boolean initializedDepencies;

	public CDIResourceComponent(CDIComponent component) {
		this.component = component;
		this.initializedDepencies = true;
	}

	@Deprecated
	public CDIResourceComponent() {
	}
	
	public boolean isInitializedDepencies() {
		return initializedDepencies;
	}
	
	
}
