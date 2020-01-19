package com.example.demo.DTO;

import lombok.Data;

@Data
public class MembroDTO {

    private Long id;
    private String nome;

    public static MembroDTO from(String membro) {
        MembroDTO membroDTO = new MembroDTO();
        membroDTO.setId(Long.valueOf(membro.split("\\|")[0]));
        membroDTO.setNome(membro.split("\\|")[1]);

        return membroDTO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}