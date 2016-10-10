package tw.com.softleader.ethweb.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.stereotype.Controller;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.module.SimpleModule;

import tw.com.softleader.commons.base.Translatable;
import tw.com.softleader.commons.i18n.TranslateFromResourceBundle;
import tw.com.softleader.commons.json.jackson.JacksonObjectMapper;
import tw.com.softleader.commons.json.jackson.TranslatableJsonSerializer;
import tw.com.softleader.data.page.SLPageableHandlerMethodArgumentResolver;
import tw.com.softleader.web.mvc.config.WebMvcConfiguration;

@Configuration
@EnableWebMvc
@Primary
@ComponentScan(basePackages = "tw.com.softleader.ethweb.**.web", useDefaultFilters = false,
    includeFilters = @Filter(Controller.class))
public class WebMvcConfig extends WebMvcConfiguration {

  @Bean
  public TranslateFromResourceBundle translater() {
    return new TranslateFromResourceBundle("translation");
  }

  @Bean
  public JacksonObjectMapper jacksonObjectMapper(TranslateFromResourceBundle serializer) {
    return new JacksonObjectMapper(new SimpleModule().addSerializer(Translatable.class,
        new TranslatableJsonSerializer(serializer, "value", "label")));
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    final SLPageableHandlerMethodArgumentResolver resolver =
        new SLPageableHandlerMethodArgumentResolver();
    resolver.setParameterSupportsClass(Pageable.class);
    argumentResolvers.add(resolver);
    argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
  }
    
}