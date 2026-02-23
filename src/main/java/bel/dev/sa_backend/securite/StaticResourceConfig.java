package bel.dev.sa_backend.securite;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get("uploads/products"); // dossier local
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        // Expose /files/products/** => fichiers du disque
        registry.addResourceHandler("/files/products/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}