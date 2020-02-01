package com.example.demo.Service;

import com.example.demo.DTO.DuplaDTO;
import com.example.demo.RodizioUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DuplaService {
    private final String LOG_DUPLAS = System.getProperty("user.dir").concat("/src/main/java/com/example/demo/File/log_duplas.txt");
    private final String PIPE = "\\|";
    private final String MEMBRO_LOCKADO = "*";

    private final MembroService membroService;

    public DuplaService(MembroService membroService) {
        this.membroService = membroService;
    }

    public List<DuplaDTO> buscarDuplas() throws IOException {
        List<String> membros = this.membroService.lerArquivoDeMembros();

        return this.lerArquivoDeDuplas().stream().map(dupla -> DuplaDTO.from(dupla, membros)).collect(Collectors.toList());
    }

    public List<String> iniciarDuplas(DuplaDTO dupla) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(LOG_DUPLAS, true));

        String lineSeparator = this.lerArquivoDeDuplas().size() == 0 ? "" : System.lineSeparator();

        List<String> membros = this.membroService.lerArquivoDeMembros();

        String membro1 = membros.stream().filter(membro -> membro.split(PIPE)[1].equals(dupla.getMembro1())).findFirst().get().split(PIPE)[0];
        String membro2 = membros.stream().filter(membro -> membro.split(PIPE)[1].equals(dupla.getMembro2())).findFirst().get().split(PIPE)[0];

        bufferedWriter.append(lineSeparator.concat(membro1).concat("|").concat(membro2));
        bufferedWriter.close();

        return this.lerArquivoDeDuplas();
    }

    private List<String> lerArquivoDeDuplas() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(LOG_DUPLAS));
        List<String> contentFile = new ArrayList<>();
        String s;

        while ((s = bufferedReader.readLine()) != null) {
            contentFile.add(s);
        }

        bufferedReader.close();

        return contentFile;
    }

    public List<String> construirDuplas() throws IOException {
        List<String> membrosLockados = this.membroService.lerArquivoDeMembros().stream()
                .filter(membro -> membro.split("\\|").length > 2 && membro.split("\\|")[2].equals(MEMBRO_LOCKADO)).collect(Collectors.toList());

        List<String> membrosRotativos = this.membroService.lerArquivoDeMembros().stream()
                .filter(membro -> !membrosLockados.contains(membro)).collect(Collectors.toList());

        List<String> novasDuplas = new ArrayList<>();

        return this.validarDuplas(membrosLockados, membrosRotativos, novasDuplas);
    }

    private List<String> validarDuplas(List<String> membrosLockados, List<String> membrosRotativos, List<String> novasDuplas) throws IOException {
        List<String> duplas = this.lerArquivoDeDuplas();

        while (membrosLockados.size() + membrosRotativos.size() > 1) {
            String idMembroLockadoSorteado = membrosLockados.isEmpty() ? this.membroService.getIdMembroRotativo(membrosRotativos) : this.membroService.getIdMembroLockado(membrosLockados);
            String idMembroRotativoSorteado = membrosRotativos.isEmpty() ? this.membroService.getIdMembroRotativo(membrosLockados) : this.membroService.getIdMembroRotativo(membrosRotativos);

            boolean duplaJaMontada = duplas.stream().anyMatch(dupla ->
                    dupla.contains(idMembroLockadoSorteado) && dupla.contains(idMembroRotativoSorteado)
            );

            if (duplaJaMontada && membrosLockados.size() + membrosRotativos.size() == 2) {
                this.construirDuplas();
            }

            if (duplaJaMontada) {
                this.validarDuplas(membrosLockados, membrosRotativos, novasDuplas);
            }

            novasDuplas.add(idMembroLockadoSorteado.concat("|").concat(idMembroRotativoSorteado));

            if (membrosLockados.isEmpty()) {
                membrosRotativos.removeIf(membro -> membro.split("\\|")[0].equals(idMembroLockadoSorteado));
            } else {

                membrosLockados.removeIf(membro -> membro.split("\\|")[0].equals(idMembroLockadoSorteado));
            }

            if (membrosRotativos.isEmpty()) {
                membrosLockados.removeIf(membro -> membro.split("\\|")[0].equals(idMembroRotativoSorteado));
            } else {

                membrosRotativos.removeIf(membro -> membro.split("\\|")[0].equals(idMembroRotativoSorteado));
            }
        }

        if (!membrosLockados.isEmpty()) {
            novasDuplas.add(membrosLockados.get(0).split("\\|")[0].concat("|"));
            membrosLockados.clear();
        }

        if (!membrosRotativos.isEmpty()) {
            novasDuplas.add(membrosRotativos.get(0).split("\\|")[0].concat("|"));
            membrosRotativos.clear();
        }

        RodizioUtils.resetarArquivo(LOG_DUPLAS);

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(LOG_DUPLAS, true));
        bufferedWriter.append(String.join(System.lineSeparator(), novasDuplas));
        bufferedWriter.close();

        return this.lerArquivoDeDuplas();
    }
}
