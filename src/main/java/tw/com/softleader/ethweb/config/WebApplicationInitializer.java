package tw.com.softleader.ethweb.config;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebApplicationInitializer
    extends AbstractAnnotationConfigDispatcherServletInitializer {

  @Override
  protected Class<?>[] getRootConfigClasses() {
    return new Class<?>[] {EthereumConfig.class, WebSocketConfig.class, DataSourceConfig.class, WebSecurityConfig.class, tw.com.softleader.domain.config.DefaultDomainConfiguration.class, ServiceConfig.class};
  }

  @Override
  protected Class<?>[] getServletConfigClasses() {
    return new Class<?>[] {WebMvcConfig.class};
  }

  @Override
  protected Filter[] getServletFilters() {
    return new Filter[] {new org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter()};
  }
  
  @Override
  protected String[] getServletMappings() {
    return new String[] {"/"};
  }
  
  @Override
  protected void customizeRegistration(Dynamic registration) {
    super.customizeRegistration(registration);

    String location = ""; // the directory location where files will be stored
    long maxFileSize = -1; // the maximum size allowed for uploaded files
    long maxRequestSize = -1; // the maximum size allowed for multipart/form-data requests
    int fileSizeThreshold = 0; // the size threshold after which files will be written to disk
    registration.setMultipartConfig(
        new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold));
  }
  
  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
      super.onStartup(servletContext);
      servletContext.addListener(new HttpSessionListener() {
        
        @Override
        public void sessionCreated(HttpSessionEvent event) {
          event.getSession().setMaxInactiveInterval(5*60);
        }
        
        @Override
        public void sessionDestroyed(HttpSessionEvent event) {
          // do nothing
        }
        
      });
  }
}