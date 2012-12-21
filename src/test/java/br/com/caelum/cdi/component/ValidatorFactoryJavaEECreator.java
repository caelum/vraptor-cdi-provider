package br.com.caelum.cdi.component;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.ValidatorFactory;

import org.mockito.Mockito;

import br.com.caelum.vraptor.ioc.ComponentFactory;

public class ValidatorFactoryJavaEECreator implements ComponentFactory<ValidatorFactory>{
	
	public static ValidatorFactory factory = Mockito.mock(ValidatorFactory.class); 

	@ApplicationScoped
	public ValidatorFactory getInstance() {
		return factory;
	}

}
