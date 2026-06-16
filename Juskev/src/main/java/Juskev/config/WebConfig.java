package Juskev.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Sirve las imágenes subidas por el admin desde ruta absoluta
        String uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize().toUri().toString();
        if (!uploadPath.endsWith("/")) uploadPath += "/";

        registry.addResourceHandler("/uploads/productos/**")
                .addResourceLocations(uploadPath);

        // Recursos estáticos normales
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/");
    }
}