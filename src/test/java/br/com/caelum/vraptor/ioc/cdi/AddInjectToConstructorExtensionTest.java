package br.com.caelum.vraptor.ioc.cdi;

import java.util.Iterator;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.inject.Inject;
import javax.resource.spi.IllegalStateException;

import org.apache.deltaspike.core.util.metadata.builder.AnnotatedTypeBuilder;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import br.com.caelum.cdi.component.CDIComponent;
import br.com.caelum.vraptor.ioc.Component;

public class AddInjectToConstructorExtensionTest {

	@Test
	public void shouldAddInjectToConstructorWithArgs(){
		AddInjectToConstructorExtension extension = new AddInjectToConstructorExtension();
		AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder();
		AnnotatedType annotatedType = builder.readFromType(WithArgsConstructor.class).create();
		ProcessAnnotatedTypeMock pat = new ProcessAnnotatedTypeMock(annotatedType);
		extension.processAnnotatedType(pat);
		AnnotatedConstructor<?> argsConstructor = withArgs(pat.getAnnotatedType().getConstructors());
		AnnotatedConstructor<?> withoutArgsConstructor = withoutArgs(pat.getAnnotatedType().getConstructors());
		assertTrue(argsConstructor.isAnnotationPresent(Inject.class));
		assertFalse(withoutArgsConstructor.isAnnotationPresent(Inject.class));
		
	}
	
	@Test
	public void shouldNotAddInjectToConstructorWithoutArgs(){
		ProcessAnnotatedTypeMock pat = createProcessAnotatedType(WithNonArgsConstructor.class);
		AddInjectToConstructorExtension extension = new AddInjectToConstructorExtension();
		extension.processAnnotatedType(pat);
		AnnotatedConstructor<?> withoutArgsConstructor = withoutArgs(pat.getAnnotatedType().getConstructors());
		assertFalse(withoutArgsConstructor.isAnnotationPresent(Inject.class));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ProcessAnnotatedTypeMock createProcessAnotatedType(Class<?> klass) {
		AnnotatedTypeBuilder builder = new AnnotatedTypeBuilder();
		AnnotatedType annotatedType = builder.readFromType(klass).create();
		ProcessAnnotatedTypeMock pat = new ProcessAnnotatedTypeMock(annotatedType);
		return pat;
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
