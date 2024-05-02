/* (C)2024 */
package com.sillim.recordit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class RecorditApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecorditApplication.class, args);
	}
}
