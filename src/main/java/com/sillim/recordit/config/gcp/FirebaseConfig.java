package com.sillim.recordit.config.gcp;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.ResourceUtils;

@Configuration
@Profile("!test")
public class FirebaseConfig {

	@Value("${firebase.credentials.location}")
	private String keyfilePath;

	@PostConstruct
	public void initialize() throws IOException {
		InputStream keyFile = ResourceUtils.getURL(keyfilePath).openStream();
		FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(keyFile))
				.build();

		List<FirebaseApp> apps = FirebaseApp.getApps();
		if (apps == null || apps.isEmpty()) {
			FirebaseApp.initializeApp(options);
		}
	}
}
