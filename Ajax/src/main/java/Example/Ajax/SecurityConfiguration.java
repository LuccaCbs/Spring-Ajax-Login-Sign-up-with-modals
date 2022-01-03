
package Example.Ajax;

import Example.Ajax.services.UserService;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)

public class SecurityConfiguration extends WebSecurityConfigurerAdapter { /*Esta extensión nos permite configurar las peticiones http*/

    @Autowired
    private UserService userService;
    
    @Autowired
    private LoginSuccessMessage successMessage;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userService)
            .passwordEncoder(new BCryptPasswordEncoder());
    }
    
    @Override  /*Este método se sobreescribe desde: 'boton derecho-insert code-override methods' */ 
    protected void configure(HttpSecurity http) throws Exception {
        
        /*Primero ponemos al home o index como público, es decir que cualquier tipo de usuario podría visitarlo, junto con las carpetas necesarias para las vistas, como CSS, IMAGES y JS*/
        http.headers()
        .addHeaderWriter(new StaticHeadersWriter("X-Content-Security-Policy","script-src 'self' 'unsafe-inline' *", "frame-ancestors 'self' *.mercadolibre.com")).frameOptions().sameOrigin().and()
                .authorizeRequests()
                        .antMatchers("/", "/home" , "/index", "/resources/**")
                        .permitAll()  // "/resources/**" : los dos asteriscos luego de la barra indican que está permitido todo lo contenido en esa carpeta.
//                        .anyRequest().authenticated()
                .and().formLogin()
                        .successHandler(successMessage)
                        .loginPage("/")
                        .loginProcessingUrl("/logincheck")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/")                    
                        .permitAll() /*Establecemos el mapa del método "login" del controller correspondiente, para que nos redireccione a una vista creada por nosotros y no la de Spring*/
                .and().logout()
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                .and().csrf().disable().cors();
                
    }

}
