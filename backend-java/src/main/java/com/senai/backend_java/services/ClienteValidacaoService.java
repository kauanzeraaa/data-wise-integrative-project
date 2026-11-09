package com.senai.backend_java.services;

import java.util.HashSet; // estrutura para identificar repetidos
import java.util.List; // usado para receber lista de clientes
import java.util.Set; // interface para conjunto sem repetição

import org.springframework.stereotype.Service; // marca a classe como serviço do Spring

import com.senai.backend_java.dto.ClienteDTO;

@Service // o Spring cria esta classe automaticamente
public class ClienteValidacaoService { // classe de regras complementares
    public List<ClienteDTO> validarEPreparar(List<ClienteDTO> clientes) { // recebe lista do controller
        Set<String> codigos = new HashSet<>(); // guarda códigos já encontrados
        for (ClienteDTO cliente : clientes) { // percorre cada cliente recebido
            String codigo = cliente.codigoCliente().trim(); // remove espaços do código
            if (!codigos.add(codigo)) { // se já existe, é repetido
                throw new IllegalArgumentException("Código repetido na planilha: " + codigo); // erro claro
            }
        }

        return clientes.stream() // transforma a lista em fluxo de processamento
                .map(this::padronizar) // aplica padronização em cada cliente
                .toList(); // volta para lista
    }

    private ClienteDTO padronizar(ClienteDTO c) { // método privado para limpar uma linha
        return new ClienteDTO( // cria um novo DTO já preparado
                c.codigoCliente().trim(), // remove espaços do código
                c.nomeCliente().trim(), // remove espaços do nome
                formatarNome(c.consultor()), // deixa consultor mais legível
                padronizarSegmento(c.segmento()), // normaliza o segmento
                c.nivelCliente().trim().toUpperCase(), // nível sempre em maiúsculo
                c.faturamentoAnual(), // mantém valor numérico validado
                c.servicosContratados().trim(), // remove espaços dos serviços
                c.dataContratacao(), // mantém data validada
                c.cidade() == null ? "" : c.cidade().trim(), // cidade opcional
                c.uf() == null ? "" : c.uf().trim().toUpperCase() // UF opcional em maiúsculo
        );
    }

    private String padronizarSegmento(String segmento) { // recebe segmento da planilha
        String s = segmento.trim().toLowerCase(); // remove espaços e deixa minúsculo
        if (s.equals("ind.") || s.equals("industria") || s.equals("indústria")) { // variações de indústria
            return "Indústria"; // retorna padrão único
        }
        if (s.equals("comercio") || s.equals("comércio")) { // variações de comércio
            return "Comércio"; // retorna padrão único
        }
        if (s.equals("servicos") || s.equals("serviços")) { // variações de serviços
            return "Serviços"; // retorna padrão único
        }
        return segmento.trim(); // se não conhece, mantém texto sem espaços extras
    }

    private String formatarNome(String nome) { // recebe nome do consultor
        String texto = nome.trim().toLowerCase(); // remove espaços e deixa minúsculo
        return texto.substring(0, 1).toUpperCase() + texto.substring(1); // primeira letra maiúscula
    }
}
