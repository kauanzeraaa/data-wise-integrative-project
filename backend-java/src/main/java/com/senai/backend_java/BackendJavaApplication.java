package com.senai.backend_java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Ponto de entrada da API.
 * {@code @ConfigurationPropertiesScan} registra records como AppProperties (prefixo "app").
 * Sem ele, AppProperties não vira bean e UploadService/PythonEtlService não sobem.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class BackendJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendJavaApplication.class, args);
	}

}
