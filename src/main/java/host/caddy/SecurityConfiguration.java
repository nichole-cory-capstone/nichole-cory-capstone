package host.caddy;

import host.caddy.services.UserDetailsLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
    public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

        private UserDetailsLoader usersLoader;

        public SecurityConfiguration(UserDetailsLoader usersLoader) {
            this.usersLoader = usersLoader;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth
                    .userDetailsService(usersLoader) // How to find users by their username
                    .passwordEncoder(passwordEncoder()) // How to encode and verify passwords
            ;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/user/profile") // user's home page
                    .permitAll() // Anyone can go to the login page
                    .and()
                    // Allow without login
                    .authorizeRequests()
                    .antMatchers("/","/search/**", "/static/**", "/logout")
                    .permitAll()
                    .and()
                    // restricted area
                    .authorizeRequests()
                    .antMatchers("/collection/**", "/profile/**","/user/**")
                    .authenticated()
                    .and()
                    //logout
                    .logout()
                    .logoutSuccessUrl("/login?logout")
            ;
        }
    }
