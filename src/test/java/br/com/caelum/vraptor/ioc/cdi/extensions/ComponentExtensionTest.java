package br.com.caelum.vraptor.ioc.cdi.extensions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;

import org.junit.Test;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.ioc.SessionScoped;

@SuppressWarnings("serial")
public class ComponentExtensionTest {

	@Test
	public void shouldAddRequestAndDefaultForComponents() {
		ProcessAnnotatedTypeMock pat = ProcessAnnotatedTypeFactory.create(MyRequestComponent.class);
		ComponentExtension extension = new ComponentExtension();
		extension.processAnnotatedType(pat);
		assertTrue(pat.getAnnotatedType().getAnnotations().contains(new AnnotationLiteral<RequestScoped>() {}));
		assertTrue(pat.getAnnotatedType().getAnnotations().contains(new AnnotationLiteral<Default>() {}));
	}
	
	@Test
	public void shouldNotAddRequestIfAnotherScopeWasUsed() {
		ProcessAnnotatedTypeMock pat = ProcessAnnotatedTypeFactory.create(MySessionComponent.class);
		ComponentExtension extension = new ComponentExtension();
		extension.processAnnotatedType(pat);
		assertFalse(pat.getAnnotatedType().getAnnotations().contains(new AnnotationLiteral<RequestScoped>() {}));
		assertTrue(pat.getAnnotatedType().getAnnotations().contains(new AnnotationLiteral<SessionScoped>() {}));
		assertTrue(pat.getAnnotatedType().getAnnotations().contains(new AnnotationLiteral<Default>() {}));
	}

	@Component
	private static class MyRequestComponent {

	}
	
	@Component
	@SessionScoped
	private static class MySessionComponent {
		
	}
}
