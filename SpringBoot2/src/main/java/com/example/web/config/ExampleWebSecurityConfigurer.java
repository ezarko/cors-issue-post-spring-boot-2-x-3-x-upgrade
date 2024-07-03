package com.example.web.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;

/**
 * Configures global com.example.web.security characteristics of Example Web Security chain
 * Use this component when you want to retain the default configuration provided by this starter but customize
 * the com.example.web.security chain. If you want more granular control or the default chain is inappropriate, consider implementing
 * a {@link org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter} instead
 */
public interface ExampleWebSecurityConfigurer {

  /**
   * Allows customization of the {@link org.springframework.security.authentication.AuthenticationManager} using
   * the provided {@link AuthenticationManagerBuilder}
   *
   * @param auth the {@link AuthenticationManagerBuilder}
   * @throws Exception
   */
  default void configure(AuthenticationManagerBuilder auth) throws Exception {

  }

  /**
   * Allows customization of the global web com.example.web.security filter chain using the provided {@link WebSecurity} object
   *
   * @param web the {@link WebSecurity}
   * @throws Exception
   */
  default void configure(WebSecurity web) throws Exception {

  }

  /**
   * Allows customization of the global http com.example.web.security chain using the provided {@link HttpSecurity} object
   *
   * @param http the {@link HttpSecurity}
   * @throws Exception
   */
  default void configure(HttpSecurity http) throws Exception {

  }

}
