package br.com.caelum.vraptor.ioc.cdi;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import net.vidageek.mirror.dsl.Matcher;
import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ResourceHandler;

//TODO unit tests
public class ConstructorAdapter {
	private static ClassPool classPool = ClassPool.getDefault();
	private static Map<String,Class> adaptedClasses = new HashMap<String, Class>();

	static{
		classPool.appendSystemPath();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Class tryToAddCDIConstructorFor(Class klass) {
		String cdiClassName = klass.getCanonicalName() + "CDIEyes";
		if(adaptedClasses.containsKey(cdiClassName)){
			return adaptedClasses.get(cdiClassName);
		}
		boolean thereIsNotNoArgsConstructor = new Mirror().on(klass)
				.reflectAll().constructorsMatching(new NoArgsConstructor())
				.isEmpty();
		if (thereIsNotNoArgsConstructor) {
			try {					
					CtClass ctClass = classPool.getAndRename(klass.getCanonicalName(),cdiClassName);
					ctClass.setSuperclass(classPool.get(klass
							.getCanonicalName()));

					CtConstructor defaultConstructor = new CtConstructor(new CtClass[]{}, ctClass);
					doNotCallSuperFor(defaultConstructor);
					ctClass.addConstructor(defaultConstructor);					
					Class adapted = ctClass.toClass();
					System.out.println(adapted.getName()+"="+adapted.getGenericInterfaces()[0]);
					adaptedClasses.put(cdiClassName, adapted);
					return adapted;
				
			} catch (Exception e) {
				throw new VRaptorException(e);
			}
		}
		return klass;
	}

	private void doNotCallSuperFor(CtConstructor defaultConstructor) throws CannotCompileException {
		defaultConstructor.setBody("int a = 0;");
	}

	private static class NoArgsConstructor implements Matcher<Constructor> {

		public boolean accepts(Constructor element) {
			return element.getParameterTypes().length == 0;
		}
	}

}
