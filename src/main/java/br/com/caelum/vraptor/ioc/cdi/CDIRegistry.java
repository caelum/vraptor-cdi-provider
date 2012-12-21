package br.com.caelum.vraptor.ioc.cdi;

import java.util.Arrays;
import java.util.Collection;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;

import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.ioc.ComponentFactory;

public class CDIRegistry {

	private BeforeBeanDiscovery discovery;
	private BeanManager bm;

	public CDIRegistry(BeforeBeanDiscovery discovery, BeanManager bm) {
		this.discovery = discovery;
		this.bm = bm;
	}
	
	public void configure(){
		registerApplicationComponents();
		registerRequestComponents();
		registerPrototypeComponents();	
		registerCustomProducers();
		registerConverters();
		register(CDIBasedContainer.class);
		register(CDIRequestInfoFactory.class);
		register(ServletContextFactory.class);
		
	}

	private void registerConverters() {
		registerComponents(BaseComponents.getBundledConverters());
	}

	private void registerCustomProducers() {
		register(SerializationsFactory.class);
		register(XStreamConvertersFactory.class);
	}

	private void registerPrototypeComponents() {
		registerComponents(BaseComponents.getPrototypeScoped().values());
	}

	private void registerRequestComponents() {
		registerComponents(BaseComponents.getRequestScoped().values());
	}

	private void registerApplicationComponents() {
		registerComponents(BaseComponents.getApplicationScoped().values());
		registerComponents(Arrays.asList(BaseComponents.getStereotypeHandlers()));
	}
	
	private <T> void registerComponents(Collection<Class<? extends T>> toRegister) {
		for (Class<?> component : toRegister){
			register(component);
		}
	}
	
	private void register(Class<?> component) {
		if(ComponentFactory.class.isAssignableFrom(component)){
			//have to register here because the container does not fire ProcessAnnotatedType for custom components.
			discovery.addAnnotatedType(new ComponentFactoryAnnotatedTypeCreator().create(component));
		}
		else{
			discovery.addAnnotatedType(bm.createAnnotatedType(component));
		}
	}

}
