package com.sillim.recordit.config.gcp;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class GCSConfig {

	@Bean
	public Storage storage() throws IOException {
		return StorageOptions.getDefaultInstance().getService();
	}
}
