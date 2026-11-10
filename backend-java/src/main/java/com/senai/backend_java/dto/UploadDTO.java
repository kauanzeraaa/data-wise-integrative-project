package com.senai.backend_java.dto;

import java.util.List;

public final class UploadDTO {
    private UploadDTO() {
        // Construtor privado para evitar instanciação
    }

    public record EnvioValidadasRequest(String arquivoNome, Integer descartadas, List<String> erros) {}

    public record LinhaEnvio(Integer numeroLinha, String codigoCliente, String nomeCliente, String consultor,
        String segmento, String nivelCliente, String faturamentoAnual, String servicosContratados, String dataContratacao,
        String cidade, String uf
    ){}
}
