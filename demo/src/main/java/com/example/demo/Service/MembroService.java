package com.example.demo.Service;

import com.example.demo.DTO.MembroDTO;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MembroService {
    
    private final String MEMBROS = System.getProperty("user.dir").concat("/src/main/java/com/example/demo/File/membros.txt");
    private final String PIPE = "\\|";

    public List<String> lerArquivoDeMembros() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(MEMBROS));
        List<String> contentFile = new ArrayList<>();
        String s;

        while ((s = bufferedReader.readLine()) != null) {
            contentFile.add(s);
        }

        bufferedReader.close();

        return contentFile;
    }

    public List<MembroDTO> buscarMembrosDTO() throws IOException {
        return this.lerArquivoDeMembros().stream().map(MembroDTO::from).collect(Collectors.toList());
    }

    private String sortearMembro() throws IOException {
        int membro = 0;
        while (membro == 0) {
            membro = (int) (this.lerArquivoDeMembros().size() * Math.random()) + 1;
        }

        return String.valueOf(membro);
    }

    public String getIdMembroLockado(List<String> membrosLockados) throws IOException {
        if (membrosLockados.size() > 0) {
            List<String> idMembrosLockados = membrosLockados.stream().map(membro -> membro.split(PIPE)[0]).collect(Collectors.toList());

            String idMembroLockadoSorteado = "";
            while (!idMembrosLockados.contains(idMembroLockadoSorteado)) {
                idMembroLockadoSorteado = this.sortearMembro();
            }

            return idMembroLockadoSorteado;
        }

        return "";
    }

    public String getIdMembroRotativo(List<String> membrosRotativos) throws IOException {
        if (membrosRotativos.size() > 0) {
            List<String> idMembrosRotativos = membrosRotativos.stream().map(membro -> membro.split(PIPE)[0]).collect(Collectors.toList());

            String idMembroRotativoSorteado = "";
            while (!idMembrosRotativos.contains(idMembroRotativoSorteado)) {
                idMembroRotativoSorteado = this.sortearMembro();
            }

            return idMembroRotativoSorteado;
        }

        return "";
    }
}
