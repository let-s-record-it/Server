package com.sillim.recordit.support.gcp;

import com.google.cloud.storage.Storage;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestGCSConfig {

	@Bean
	public Storage storage() {
		return Mockito.mock(Storage.class);
	}
}
