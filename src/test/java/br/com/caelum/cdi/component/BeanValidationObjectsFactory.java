package br.com.caelum.cdi.component;

import javax.enterprise.inject.Produces;
import javax.validation.MessageInterpolator;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.mockito.Mockito;

public class BeanValidationObjectsFactory {

	public static final ValidatorFactory validatorFactory = Mockito.mock(ValidatorFactory.class);
	public static final Validator validator = Mockito.mock(Validator.class);
	public static final MessageInterpolator interpolator = Mockito.mock(MessageInterpolator.class);

	@Produces
	public ValidatorFactory producesValidatorFactory(){
		return validatorFactory;
	}
	
	@Produces
	public Validator producesValidator(){
		return validator;
	}
	
	@Produces
	public MessageInterpolator producesMessageInterpolator(){
		return interpolator;
	}
}
