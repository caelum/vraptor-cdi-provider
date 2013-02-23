In order to use the CDI provider, the follow steps should be executed:

1 - Configure the BeanManager listener in web.xml.
	<listener>
	    <listener-class>br.com.caelum.vraptor.ioc.cdi.BeanManagerSetup</listener-class>
	</listener>

2 - Configure the CDIProvider class in web.xml
	<context-param>
	    <param-name>br.com.caelum.vraptor.provider</param-name>
	    <param-value>br.com.caelum.vraptor.ioc.cdi.CDIProvider</param-value>
	</context-param>

3 - Use the beans.xml configured with VRaptor factories. Just copy the beans.xml to META-INF folder 	
		 