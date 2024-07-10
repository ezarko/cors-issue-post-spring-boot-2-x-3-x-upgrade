package com.example.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.Authentication;

/**
 * Configures global com.example.web.security characteristics of Example Web Security chain
 * Use this component when you want to retain the default configuration provided by this starter but customize
 * the com.example.web.security chain.
 */
public interface ExampleWebSecurityConfigurer {

  /**
   * Allows customization of the {@link org.springframework.security.core.Authentication} using
   * the provided {@link Authentication}
   *
   * @param auth the {@link Authentication}
   * @throws Exception
   */
  default void configure(Authentication auth) throws Exception {
    Logger LOGGER = LoggerFactory.getLogger(ExampleWebSecurityConfigurer.class);
    LOGGER.error("triggered default configure(auth) method");

  }

  /**
   * Allows customization of the global web com.example.web.security filter chain using the provided {@link WebSecurity} object
   *
   * @param web the {@link WebSecurity}
   * @throws Exception
   */
  default void configure(WebSecurity web) throws Exception {
    Logger LOGGER = LoggerFactory.getLogger(ExampleWebSecurityConfigurer.class);
    LOGGER.error("triggered default configure(web) method");
  }

  /**
   * Allows customization of the global http com.example.web.security chain using the provided {@link HttpSecurity} object
   *
   * @param http the {@link HttpSecurity}
   * @throws Exception
   */
  default void configure(HttpSecurity http) throws Exception {
    Logger LOGGER = LoggerFactory.getLogger(ExampleWebSecurityConfigurer.class);
    LOGGER.error("triggered default configure(http) method");
  }

}
