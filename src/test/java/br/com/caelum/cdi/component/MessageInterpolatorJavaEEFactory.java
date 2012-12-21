package br.com.caelum.cdi.component;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.MessageInterpolator;

import org.mockito.Mockito;

import br.com.caelum.vraptor.ioc.ComponentFactory;

public class MessageInterpolatorJavaEEFactory implements ComponentFactory<MessageInterpolator>{

	@ApplicationScoped
	public MessageInterpolator getInstance() {
		return Mockito.mock(MessageInterpolator.class);
	}

}
