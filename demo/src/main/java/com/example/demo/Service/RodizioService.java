package com.example.demo.Service;

import com.example.demo.DTO.DuplaDTO;
import com.example.demo.DTO.MembroDTO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class RodizioService {

    private final String MEMBROS = System.getProperty("user.dir").concat("/src/main/java/com/example/demo/File/membros.txt");

    private final MembroService membroService;
    private final DuplaService duplaService;

    public RodizioService(MembroService membroService, DuplaService duplaService) {
        this.membroService = membroService;
        this.duplaService = duplaService;
    }

    public List<MembroDTO> buscarMembrosDTO() throws IOException {
        return this.membroService.buscarMembrosDTO();
    }

    public List<DuplaDTO> buscarDuplasDTO() throws IOException {
        return this.duplaService.buscarDuplas();
    }

    public List<DuplaDTO> iniciarDuplas(DuplaDTO dupla) throws IOException {
        return this.duplaService.iniciarDuplas(dupla);
    }

    public List<MembroDTO> salvar(String novoMembro) throws IOException {
        return this.membroService.salvar(novoMembro);
    }

    public void removerMembro(String id) throws IOException {
        this.membroService.removerMembro(id);
    }

    public List<DuplaDTO> construirDuplas() throws IOException {
        return this.duplaService.construirDuplas();
    }

    public List<MembroDTO> lockarDeslockarMembro(String id) throws IOException {
        return this.membroService.lockarDeslockarMembro(id);
    }
}


//1|Antônio
//2|Akina
//3|Caíque
//4|Elton
//5|Jun
//6|Luiz
//7|Passoca
//8|Serjão
//9|Viviane
