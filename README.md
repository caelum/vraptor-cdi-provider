vraptor-cdi-provider
====================

A Container Provider for VRaptor 3 based on CDI 1.1

In order to use the CDI Provider with VRaptor you have to follow the steps below:

1- Add the vraptor-cdi-provider entry in your pom.xml

2- Configure the Provider in web.xml

	<context-param>
		<param-name>br.com.caelum.vraptor.provider</param-name>
		<param-value>br.com.caelum.vraptor.ioc.cdi.CDIProvider</param-value>
	</context-param>

3- Configure the Listener that has to make BeanManager availabe in ServletContext

	<listener>
		<listener-class>br.com.caelum.vraptor.ioc.cdi.BeanManagerSetup</listener-class>
	</listener>
    

4- Create the beans.xml file in WEB-INf folder as follow:

	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://xmlns.jcp.org/xml/ns/javaee"
	       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	       xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
	       http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd"
	       version="1.1" bean-discovery-mode="all">
	            
	</beans>

5- If you want to override any VRaptor component you must use the @Alternative + @Priority annotations. For instance:

	@Alternative @Priority(Interceptor.Priority.LIBRARY_AFTER)
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
    
Now you have to add the Alternative in beans.xml
    
	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://xmlns.jcp.org/xml/ns/javaee"
	       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	       xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
	       http://xmlns.jcp.org/xml/ns/javaee/beans_1_1.xsd"
	       version="1.1" bean-discovery-mode="all">
	
		<alternatives>
		    <class>app.extension.CustomPathResolver</class>
		</alternatives>
	            
	</beans>

6- CDI implementations obligate you to use a Zero Args constructor for every bean that is non DependentScope. So instead
    of obligate users to create this constructor, a Java Agent is used to instrument all classes on the load time of 
    Server. This is the same approach used by other projects, like New Relic, JProfile, etc...
    
7- Here are some links to guide you in order to enable the agent for your server    

8- VRaptor provides some components that, maybe, are already provided for Application Servers. HttpServletRequest, 
    HttpSession. Other examples are Bean Validation classes, like Validator and ValidatorFactory. In oder to use Validator
    and ValidatorFactory you must declare them as alternatives on beans.xml.
    
    <alternatives>
      <class>br.com.caelum.vraptor.validator.MethodValidatorFactoryCreator</class>
      <class>br.com.caelum.vraptor.validator.ValidatorCreator</class>
    </alternatives>

9- List of tested servers
    * WildFly
    * GlassFish4
    * Tomcat 7.0.x
    * Jetty 9.
   
   
