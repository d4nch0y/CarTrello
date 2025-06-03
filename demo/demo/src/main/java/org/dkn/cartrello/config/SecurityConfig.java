package org.dkn.cartrello.config; // Декларира пакетът, в който се намира този конфигурационен клас

import org.springframework.context.annotation.Bean; // Импортира анотацията @Bean, за дефиниране на компоненти в Spring контейнера
import org.springframework.context.annotation.Configuration; // Импортира анотацията @Configuration, за да обозначи този клас като конфигурационен
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // Импортира класа за конфигуриране на HTTP сигурност
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Импортира класа за криптиране на пароли с BCrypt
import org.springframework.security.web.SecurityFilterChain; // Импортира интерфейса, описващ веригата от филтри за сигурност
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration // Обозначава класа като конфигурационен - Spring ще го използва за създаване на bean-ове
public class SecurityConfig {

    @Bean // Декларира BCryptPasswordEncoder като Spring bean, който ще бъде използван за криптиране на пароли
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Връща нов екземпляр на BCryptPasswordEncoder
    }

    @Bean // Декларира филтърната верига за сигурност като Spring bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Конфигурира кои URL заявки са разрешени без автентикация
                .authorizeHttpRequests(auth -> auth
                        // Позволява достъп до тези URL-и без нужда от влизане
                        .requestMatchers("/login", "/register", "/css/**", "/js/**", "/uploads/**").permitAll()
                        // Всички останали заявки изискват автентикация
                        .anyRequest().authenticated()
                )
                // Конфигурира формата за логин
                .formLogin(form -> form
                        // Задава пътя към страницата за вход
                        .loginPage("/login")
                        // Задава страницата, към която ще бъде пренасочен потребителят след успешен вход
                        .defaultSuccessUrl("/home", true)
                        // Позволява достъп до страницата за вход без автентикация
                        .permitAll()
                )
                // Конфигурира изхода от системата
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        // След logout, потребителят се пренасочва към страницата за вход с параметър logout
                        .logoutSuccessUrl("/login?logout")
                        // Позволява достъп до logout функционалността без автентикация
                        .permitAll()
                );

        return http.build(); // Изгражда и връща конфигурацията на филтърната верига
    }
}
