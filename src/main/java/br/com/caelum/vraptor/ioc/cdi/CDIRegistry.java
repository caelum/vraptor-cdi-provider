package br.com.caelum.vraptor.ioc.cdi;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;

import net.vidageek.mirror.dsl.Mirror;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.ioc.ComponentFactory;

public class CDIRegistry {

	private BeforeBeanDiscovery discovery;
	private BeanManager bm;
	private static final Logger logger = LoggerFactory.getLogger(CDIRegistry.class);
	private static InputStream vraptorEEFile;

	public CDIRegistry(BeforeBeanDiscovery discovery, BeanManager bm) {
		this.discovery = discovery;
		this.bm = bm;
	}
	
	public void configure(){
		registerApplicationComponents();
		registerRequestComponents();
		registerPrototypeComponents();	
		registerConverters();
		registerCDISpecifics();

		
	}

	private void registerConverters() {
		registerComponents(BaseComponents.getBundledConverters());
	}

	private void registerCDISpecifics() {
		register(CDIBasedContainer.class);
		register(CDIRequestInfoFactory.class);
		register(ServletContextFactory.class);
		register(CDIHttpServletRequestFactory.class);
		register(CDIHttpServletResponseFactory.class);
		register(CDIFilterChainFactory.class);
		register(CDIHttpSessionFactory.class);
		register(ListProducer.class);
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
			if(CDIRegistry.vraptorEEPresent()){
				Method method = new Mirror().on(component).reflect().method("getInstance").withoutArgs();
				if(BaseComponents.getJavaEEInterfaces().contains(method.getReturnType().getCanonicalName())){
					logger.info("Ignoring {}. Let's use the ApplicationServer built in Factory");
					return;
				}
			}
			discovery.addAnnotatedType(new ComponentFactoryAnnotatedTypeCreator().create(component));
		}
		else{
			discovery.addAnnotatedType(bm.createAnnotatedType(component));
		}
	}
	
	private static boolean vraptorEEPresent() {
		if(vraptorEEFile==null){
			vraptorEEFile = BaseComponents.class.getResourceAsStream("/vraptor-cdi-ee.xml");
		}
    	if(vraptorEEFile!=null){
    		try {
				vraptorEEFile.close();
			} catch (IOException e) {
				logger.error("The vraptor-cdi-ee was not closed");
			}
    		return true;
    	}
		return false;
	}	

}
