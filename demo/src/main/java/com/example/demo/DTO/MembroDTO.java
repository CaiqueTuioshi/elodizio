package com.example.demo.DTO;

import lombok.Data;

@Data
public class MembroDTO {

    private String id;
    private String nome;
    private Boolean lockado = Boolean.FALSE;

    public static MembroDTO from(String membro) {
        MembroDTO membroDTO = new MembroDTO();
        membroDTO.setId(membro.split("\\|")[0]);
        membroDTO.setNome(membro.split("\\|")[1]);
        membroDTO.setLockado(membro.split("\\|").length > 2);

        return membroDTO;
    }
}
