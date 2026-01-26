package com.krabs.Homework.configuration;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurer;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.List;

@Configuration
@EnableWs
public class WebServiceConfiguration implements WsConfigurer {

    private final String WSDL_NAME = "OrderDocumentService";

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(
            ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);

        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean
    public XsdSchema customerContractSchema() {
        return new SimpleXsdSchema(new ClassPathResource(FileConstants.XSD_FILENAME));
    }

    @Bean(name = WSDL_NAME)
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema xsd) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("theDocument");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(FileConstants.NAMESPACE_NAME);
        wsdl11Definition.setSchema(xsd);
        return wsdl11Definition;
    }

    @Bean
    public PayloadValidatingInterceptor validatingInterceptor(XsdSchema customerContractSchema) {
        PayloadValidatingInterceptor interceptor = new PayloadValidatingInterceptor();
        interceptor.setXsdSchema(customerContractSchema);
        interceptor.setValidateRequest(true);
        interceptor.setValidateResponse(false);
        interceptor.setAddValidationErrorDetail(true);
        return interceptor;
    }

    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        interceptors.add(validatingInterceptor(customerContractSchema()));
    }
}
