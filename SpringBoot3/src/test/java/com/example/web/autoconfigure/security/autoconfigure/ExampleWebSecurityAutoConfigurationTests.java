package com.example.web.autoconfigure.security.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.web.security.autoconfigure.ExampleWebSecurityAutoConfiguration;
import jakarta.servlet.DispatcherType;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // needed for mvc autoconfiguration
@ContextConfiguration(classes = {
    ExampleWebSecurityAutoConfigurationTests.Config.class,
    ExampleWebSecurityAutoConfigurationTests.SecurityConfig.class
})
@DirtiesContext
class ExampleWebSecurityAutoConfigurationTests {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleWebSecurityAutoConfigurationTests.class);

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void testErrorHandlingUnauthorized() {

    final ResponseEntity<String> unauthorizedError = restTemplate.exchange(
        "/authenticated",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<>() {
        });
    assertThat(unauthorizedError.getStatusCode())
        .isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void testErrorHandlingForbidden() {
    final HttpHeaders basic = new HttpHeaders();
    basic.setBasicAuth("user", "password");
    final ResponseEntity<String> forbiddenError = restTemplate.exchange(
        "/forbidden",
        HttpMethod.GET,
        new HttpEntity<>(basic),
        new ParameterizedTypeReference<>() {
        });
    assertThat(forbiddenError.getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void testCors() {

    final URI uri = restTemplate.getRestTemplate().getUriTemplateHandler().expand("/unauthenticated");
    final ResponseEntity<Void> fromSubdomain = restTemplate.exchange(
        RequestEntity.options(uri)
            .header(HttpHeaders.ORIGIN, "http://stackoverflow.example.com")
            .build(),
        Void.class);
    assertThat(fromSubdomain.getStatusCode())
        .isEqualTo(HttpStatus.OK);

    final ResponseEntity<Void> fromRoot = restTemplate.exchange(
        RequestEntity.options(uri)
            .header(HttpHeaders.ORIGIN, "http://example.com")
            .build(),
        Void.class);
    assertThat(fromRoot.getStatusCode())
        .isEqualTo(HttpStatus.OK);

    final ResponseEntity<Void> fromOther = restTemplate.exchange(
        RequestEntity.options(uri)
            .header(HttpHeaders.ORIGIN, "http://stackoverflow.com")
            .build(),
        Void.class);
    assertThat(fromOther.getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);

  }

  @Configuration(proxyBeanMethods = false)
  @EnableAutoConfiguration
  @EnableMethodSecurity
  static class Config {

    @RestController
    @RequestMapping
    public static class Controller {

      @GetMapping("/unauthenticated")
      public void test() {

      }

      @GetMapping("/authenticated")
      public void unauthorized() {

      }

      @GetMapping("/forbidden")
      @PreAuthorize("hasRole('BURRITO')")
      public void forbidden() {

      }

    }

  }

  @Configuration(proxyBeanMethods = false)
  static class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      LOGGER.debug("called configure(http)");
      http
          .securityMatchers(matchers -> matchers.requestMatchers("/unauthenticated", "/authenticated", "/forbidden"))
          .csrf(AbstractHttpConfigurer::disable)
          .cors(Customizer.withDefaults())
          .authorizeHttpRequests(requests -> requests
            .requestMatchers("/unauthenticated").permitAll()
            .requestMatchers("/authenticated", "/forbidden").authenticated()
          )
          .httpBasic(Customizer.withDefaults());
      return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
      LOGGER.debug("created InMemoryUserDetailsManager");
        UserDetails user = User.withDefaultPasswordEncoder()
            .username("user")
            .password("password")
            .roles("USER")
            .build();
        return new InMemoryUserDetailsManager(user);
    }
  }
}
