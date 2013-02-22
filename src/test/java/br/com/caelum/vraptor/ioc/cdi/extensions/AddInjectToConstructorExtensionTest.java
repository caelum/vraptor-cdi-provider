package br.com.caelum.vraptor.ioc.cdi.extensions;

import java.util.Iterator;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.inject.Inject;

import org.junit.Test;

import br.com.caelum.vraptor.ioc.Component;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AddInjectToConstructorExtensionTest {

	@Test
	public void shouldAddInjectToConstructorWithArgs(){
		ProcessAnnotatedTypeMock pat = ProcessAnnotatedTypeFactory.create(WithArgsConstructor.class);
		AddInjectToConstructorExtension extension = new AddInjectToConstructorExtension();
		extension.processAnnotatedType(pat);
		AnnotatedConstructor<?> argsConstructor = withArgs(pat.getAnnotatedType().getConstructors());
		AnnotatedConstructor<?> withoutArgsConstructor = withoutArgs(pat.getAnnotatedType().getConstructors());
		assertTrue(argsConstructor.isAnnotationPresent(Inject.class));
		assertFalse(withoutArgsConstructor.isAnnotationPresent(Inject.class));
		
	}
	
	@Test
	public void shouldNotAddInjectToConstructorWithoutArgs(){
		ProcessAnnotatedTypeMock pat = ProcessAnnotatedTypeFactory.create(WithNonArgsConstructor.class);
		AddInjectToConstructorExtension extension = new AddInjectToConstructorExtension();
		extension.processAnnotatedType(pat);
		AnnotatedConstructor<?> withoutArgsConstructor = withoutArgs(pat.getAnnotatedType().getConstructors());
		assertFalse(withoutArgsConstructor.isAnnotationPresent(Inject.class));
	}

	
	@Component
	private static class WithNonArgsConstructor{
		
	}
	
	@Component
	public static class WithArgsConstructor {
		private String field;

		public WithArgsConstructor(String field) {
			this.field = field;
		}
		
		public WithArgsConstructor() {
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private AnnotatedConstructor<?> withArgs(Set constructors) {
		Iterator<AnnotatedConstructor> annotatedConstructors = ((Set<AnnotatedConstructor>)constructors).iterator();
		while(annotatedConstructors.hasNext()){
			AnnotatedConstructor annotatedConstructor = annotatedConstructors.next();
			if(annotatedConstructor.getParameters().size() > 0){
				return annotatedConstructor;
			}
		}
		throw new RuntimeException("You should test a Class with at least one non args constructor");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private AnnotatedConstructor<?> withoutArgs(Set constructors) {
		Iterator<AnnotatedConstructor> annotatedConstructors = ((Set<AnnotatedConstructor>)constructors).iterator();
		while(annotatedConstructors.hasNext()){
			AnnotatedConstructor annotatedConstructor = annotatedConstructors.next();
			if(annotatedConstructor.getParameters().size() == 0){
				return annotatedConstructor;
			}
		}
		throw new RuntimeException("You should test a Class with at least one non args constructor");
	}
}
