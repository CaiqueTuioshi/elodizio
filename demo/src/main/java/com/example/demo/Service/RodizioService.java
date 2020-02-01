package com.example.demo.Service;

import com.example.demo.DTO.DuplaDTO;
import com.example.demo.DTO.MembroDTO;
import com.example.demo.RodizioUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
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

    public List<DuplaDTO> buscarDuplas() throws IOException {
        return this.duplaService.buscarDuplas();
    }

    public List<String> iniciarDuplas(DuplaDTO dupla) throws IOException {
        return this.duplaService.iniciarDuplas(dupla);
    }

    public List<String> salvar(String novoMembro) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(MEMBROS, true));

        int maxIdMembro = this.membroService.lerArquivoDeMembros().stream()
                .mapToInt(membro -> Integer.valueOf(membro.split("\\|")[0]))
                .max()
                .orElse(0);

        String lineSeparator = maxIdMembro == 0 ? "" : System.lineSeparator();

        bufferedWriter.append(
                lineSeparator
                        .concat(Integer.toString(maxIdMembro + 1))
                        .concat("|")
                        .concat(novoMembro));
        bufferedWriter.close();

        return this.membroService.lerArquivoDeMembros();
    }

    public List<String> remover(Integer idMembro) throws IOException {
        List<String> membros = this.membroService.lerArquivoDeMembros();
        membros.removeIf(membro -> idMembro.equals(Integer.valueOf(membro.split("\\|")[0])));

        RodizioUtils.resetarArquivo(MEMBROS);

        membros.forEach(membro -> {
            try {
                this.salvar(membro.split("\\|")[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return this.membroService.lerArquivoDeMembros();
    }

    public List<String> construirDuplas() throws IOException {
        return this.duplaService.construirDuplas();
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
