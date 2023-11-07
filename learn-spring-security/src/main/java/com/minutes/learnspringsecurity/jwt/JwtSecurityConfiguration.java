package com.minutes.learnspringsecurity.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableMethodSecurity
@Configuration
public class JwtSecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth) ->
                auth
                        //.requestMatchers(new AntPathRequestMatcher("/h2-console/**"))
                        .anyRequest().authenticated()
        );

        //http.formLogin(withDefaults());
        http.sessionManagement(
                session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );
        http.httpBasic(withDefaults());
        http.csrf(AbstractHttpConfigurer::disable);
        http.headers(headers ->headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));
        //decoder가 없으면, 이 resource server가 받은 jwt토큰을 디코딩하지 못한다.
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }
    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource) {
        var user = User.withUsername("in28minutes")
                //.password("{noop}dummy")
                .password("dummy")
                .passwordEncoder(str -> passwordEncoder().encode(str))
                .roles("USER")
                .build();
// 실제 코드 작성시에는 EUNM을 작성해서 적는 것이 좋다.
        var admin = User.withUsername("admin")
                //.password("{noop}dummy")
                .password("dummy")
                .passwordEncoder(str -> passwordEncoder().encode(str))
                .roles("ADMIN")
                .build();

        var tom = User.withUsername("tom")
                //.password("{noop}dummy")
                .password("dummy")
                .passwordEncoder(str -> passwordEncoder().encode(str))
                .roles("TOM")
                .build();
        var jdbcUserDetailManager = new JdbcUserDetailsManager(dataSource);
        jdbcUserDetailManager.createUser(user);
        jdbcUserDetailManager.createUser(admin);
        jdbcUserDetailManager.createUser(tom);
        return jdbcUserDetailManager;
    }

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
                .build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //decoder step 1 Create Key Pair
    @Bean
    public KeyPair keyPair()  {
        try{
            var keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    // decoder step 2 Create RSA Key object using Key Pair
    @Bean
    public RSAKey rsaKey(KeyPair keyPair) {
        return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .privateKey(keyPair.getPrivate())
                .keyID(UUID.randomUUID().toString())
                .build();
    }

    // decoder step 3 Create JWKSource
    @Bean
    public JWKSource<SecurityContext> jwkSource(RSAKey rsaKey) {
        var jwkSet = new JWKSet((rsaKey));
        return (jwkSelector,context) ->jwkSelector.select(jwkSet);
    }

    // decoder step 4 Use RSA Public Key for Decoding
    @Bean
    public JwtDecoder jwtDecoder(RSAKey rsaKey) throws JOSEException {
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey())
                .build();
    }
    // encoder
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }
}


