package org.dkn.cartrello.config; // Декларира, че този клас принадлежи към пакета org.dkn.cartrello.config

import org.springframework.context.annotation.Configuration; // Импортира анотацията @Configuration, за да маркира класа като конфигурационен
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry; // Импортира класа, който се използва за регистриране на статични ресурси
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer; // Импортира интерфейса, който позволява персонализиране на Spring MVC конфигурацията

@Configuration // Маркира този клас като конфигурационен, така че Spring автоматично да го открие и използва
public class WebConfig implements WebMvcConfigurer { // Класът имплементира WebMvcConfigurer, за да може да променя конфигурацията на уеб слоя

    @Override // Презаписва метода от интерфейса WebMvcConfigurer
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**") // Казва на Spring да обработва всички заявки към URL-и, започващи с /uploads/
                .addResourceLocations("file:uploads/"); // Указва физическото местоположение на ресурсите – папката "uploads/" в корена на проекта (на файловата система)
    }
}
