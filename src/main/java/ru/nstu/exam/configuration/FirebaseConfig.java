package ru.nstu.exam.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import ru.nstu.exam.notification.firebase.ClientTokenRegistry;
import ru.nstu.exam.notification.firebase.PushNotificationService;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "firebase", name = "enabled", havingValue = "true")
public class FirebaseConfig {

    @Value("${firebase.config}")
    private String firebaseConfig;

    @PostConstruct
    public void init() {
        try {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(
                            GoogleCredentials
                                    .fromStream(new ClassPathResource(firebaseConfig).getInputStream())
                    )
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Bean
    public PushNotificationService pushNotificationService(ClientTokenRegistry clientTokenRegistry) {
        return new PushNotificationService(clientTokenRegistry);
    }
}