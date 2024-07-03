package com.example.web.security.autoconfigure;

import com.example.web.config.ExampleWebSecurityConfigurer;
import com.example.web.config.RegexCorsConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Collection;

@Order(50) // allow other implementations to take precedence; needs to be above {@link WebSecurityConfigurerAdapter}
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@EnableAspectJAutoProxy
@EnableWebSecurity
public class ExampleWebSecurityAutoConfiguration extends WebSecurityConfigurerAdapter {

  private final Collection<ExampleWebSecurityConfigurer> configurers;

  public ExampleWebSecurityAutoConfiguration(
      final Collection<ExampleWebSecurityConfigurer> configurers) {
    this.configurers = configurers;
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    for (final ExampleWebSecurityConfigurer configurer : configurers) {
      configurer.configure(auth);
    }
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    for (final ExampleWebSecurityConfigurer configurer : configurers) {
      configurer.configure(http);
    }
    http
        .authorizeRequests()
          .anyRequest().authenticated()
        .and()
          .csrf().disable()
          .cors()
        .and()
          .sessionManagement()
          .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

  // exclude HandlerMappingIntrospector which implements a default Spring CORS policy. We only want to exclude this
  // configuration when a user includes their own customized CORS policy
  @Bean
  @ConditionalOnMissingBean(ignored = HandlerMappingIntrospector.class)
  public CorsConfigurationSource corsConfigurationSource() {
    final RegexCorsConfiguration config = new RegexCorsConfiguration();
    config.setAllowCredentials(true);
    config.addAllowedOrigin(".*?://(.+\\.)*example\\.com");
    config.addAllowedMethod(CorsConfiguration.ALL);
    config.addAllowedHeader(CorsConfiguration.ALL);
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    for (final ExampleWebSecurityConfigurer configurer : configurers) {
      configurer.configure(web);
    }
  }

  @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }
}
