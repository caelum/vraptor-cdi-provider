package br.com.caelum.cdi.component;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.Validator;

import org.mockito.Mockito;

import br.com.caelum.vraptor.ioc.ComponentFactory;

public class ValidatorJavaEECreator implements ComponentFactory<Validator>{

	@ApplicationScoped
	public Validator getInstance() {
		return Mockito.mock(Validator.class);
	}

}
