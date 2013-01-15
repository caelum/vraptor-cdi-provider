package br.com.caelum.vraptor.ioc.cdi;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.Bytecode;
import javassist.bytecode.ClassFile;
import javassist.bytecode.DuplicateMemberException;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.Opcode;
import net.vidageek.mirror.dsl.Matcher;
import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.http.route.DefaultRouter;

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

					CtConstructor oneParameterConstructor = new CtConstructor(new CtClass[]{classPool.get("java.lang.Short")}, ctClass);
					CtConstructor defaultConstructor = new CtConstructor(new CtClass[]{}, ctClass);
					ctClass.addConstructor(defaultConstructor);
					ctClass.addConstructor(oneParameterConstructor);
					oneParameterConstructor.setBody("{this();}");					
					defaultConstructor.setBody("{this(null);}");
//					byte[] bytecode = ctClass.toBytecode();
//					ByteArrayOutputStream os = new ByteArrayOutputStream(bytecode.length);
//					os.write(bytecode);
//					FileOutputStream arquivo = new FileOutputStream("/Users/albertoluizsouza/ambiente/desenvolvimento/java/vraptor/Teste.class");
//					os.writeTo(arquivo);
//					arquivo.close();
//					os.close();					
					Class adapted = ctClass.toClass();
					System.out.println(adapted.getConstructors().length);
					adaptedClasses.put(cdiClassName, adapted);
					return adapted;
				
			} catch (Exception e) {
				throw new VRaptorException(e);
			}
		}
		return klass;
	}
	
	private void doNotCallSuperFor(CtConstructor defaultConstructor) throws CannotCompileException {
		defaultConstructor.setBody("{this((byte)0);}");
	}

	private static class NoArgsConstructor implements Matcher<Constructor> {

		public boolean accepts(Constructor element) {
			return element.getParameterTypes().length == 0;
		}
	}
	
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		new ConstructorAdapter().tryToAddCDIConstructorFor(DefaultRouter.class);
	}

}
