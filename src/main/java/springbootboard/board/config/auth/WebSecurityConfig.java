package springbootboard.board.config.auth;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import springbootboard.board.domain.member.Role;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .httpBasic().disable()
                .authorizeRequests()
                .antMatchers("/hello", "/")
                .permitAll()
                .antMatchers("/admin").hasAnyRole(Role.ADMIN.name())
                .anyRequest().authenticated()
                .and()
                .logout()
                .logoutSuccessUrl("/");

    }
}
