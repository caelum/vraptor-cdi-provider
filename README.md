vraptor-cdi-provider
====================

A Container Provider for VRaptor 3 based on CDI 1.1

In order to use the CDI Provider with VRaptor you have to follow the steps below:

1- Add the vraptor-cdi-provider entry in your pom.xml
```xml
	<dependency>
		<groupId>br.com.caelum.vraptor</groupId>
		<artifactId>vraptor-cdi-provider</artifactId>
		<version>1.0.0</version>
	</dependency>
```

Change VRaptor version to 3.5.2 with cdi classifier.
```xml
	<dependency>
		<groupId>br.com.caelum.vraptor</groupId>
		<artifactId>vraptor</artifactId>
		<version>3.5.2</version>
		<classifier>cdi</classifier>
	</dependency>
```

2- Configure the Provider in web.xml
```xml
	<context-param>
		<param-name>br.com.caelum.vraptor.provider</param-name>
		<param-value>br.com.caelum.vraptor.ioc.cdi.CDIProvider</param-value>
	</context-param>
```

3- Configure the Listener that has to make BeanManager availabe in ServletContext. If you are in a Servlet Container, this listener must be placed
   after Weld Listener.
```xml
	<listener>
		<listener-class>br.com.caelum.vraptor.ioc.cdi.BeanManagerSetup</listener-class>
	</listener>
```

4- Create the beans.xml file in WEB-INF:
```xml
	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://xmlns.jcp.org/xml/ns/javaee"
	       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	       xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
	       http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd"
	       version="1.1" bean-discovery-mode="all">
	            
	</beans>
```

5- If you want to override any VRaptor component you must use the @Alternative + @Priority annotations. For instance:
```java
	@Alternative @Priority(Interceptor.Priority.APPLICATION)
	public class CustomPathResolver extends DefaultPathResolver{
	
	  @Inject
	  public CustomPathResolver(FormatResolver resolver) {
	  	super(resolver);
	  }
	
	  @Override
	  protected String getPrefix() {
	  	return "/WEB-INF/paginas/";
	  }
	}
```

6- CDI implementations obligate you to use a Zero Args constructor for every bean that is non Dependent Scope. So instead
    of obligate users to create this constructor, a Java Agent is used to instrument all classes on the load time of 
    Server. This is the same approach used by other projects, like New Relic, JProfile, etc...
    Download it here: https://github.com/caelum/vraptor-cdi-provider/blob/master/cdiagent.jar?raw=true
    
7- To enable the CDI agent, you have to provide this VM argument when starting up the server:
```sh
    -javaagent:path/to/cdiagent.jar
```

8- VRaptor provides some components that, maybe, are already provided for Application Servers. HttpServletRequest, 
    HttpSession. Other examples are Bean Validation classes, like Validator and ValidatorFactory. In oder to use Validator
    and ValidatorFactory you must declare them as alternatives on beans.xml.
```xml
    <alternatives>
      <class>br.com.caelum.vraptor.validator.MethodValidatorFactoryCreator</class>
      <class>br.com.caelum.vraptor.validator.ValidatorCreator</class>
    </alternatives>
```

9- List of tested servers

- WildFly
- Tomcat 7.0.x
- Jetty 8.
