package com.example.demo.Service;

import com.example.demo.DTO.DuplaDTO;
import com.example.demo.DTO.MembroDTO;
import com.example.demo.DTO.NovoMembroDTO;
import com.example.demo.RodizioUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class RodizioService {

    private final String MEMBROS = System.getProperty("user.dir").concat("/src/main/java/com/example/demo/File/membros.txt");
    private final String LOG_DUPLAS = System.getProperty("user.dir").concat("/src/main/java/com/example/demo/File/log_duplas.txt");

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

    public List<MembroDTO> salvar(NovoMembroDTO novoMembro) throws IOException {
        return this.membroService.salvar(novoMembro.getMembro());
    }

    public void removerMembro(String id) throws IOException {
        this.membroService.removerMembro(id);

        List<String> duplas = this.duplaService.lerArquivoDeDuplas();

        String duplaEncontrada = duplas.stream()
                .filter(membro -> membro.contains(id))
                .findFirst().get();
        int indexFound = duplas.indexOf(duplaEncontrada);

        String duplaAtualizada =  duplaEncontrada.replace(id, "").replace("|", "");

//        List<String> novaListaDuplas = new ArrayList<>();
//        novaListaDuplas.addAll(duplas.subList(0, indexFound));
//        novaListaDuplas.add(duplaAtualizada);
//        novaListaDuplas.addAll(duplas.subList(indexFound + 1, duplas.size()));

        duplas.remove(indexFound);
        duplas.add(duplaAtualizada.concat("|"));

        this.duplaService.escreverDuplas(duplas);
    }

    public List<DuplaDTO> construirDuplas() throws IOException {
        return this.duplaService.construirDuplas();
    }

    public List<MembroDTO> lockarDeslockarMembro(String id) throws IOException {
        return this.membroService.lockarDeslockarMembro(id);
    }

    public void zerarMembros() throws IOException {
        RodizioUtils.resetarArquivo(MEMBROS);;
    }

    public void zerarDuplas() throws IOException {
        RodizioUtils.resetarArquivo(LOG_DUPLAS);;
    }
}


//1|Akina
//2|Antônio
//3|Caíque
//4|Elton
//5|Jun
//6|Luiz
//7|Passoca
//8|Serjão
//9|Viviane
