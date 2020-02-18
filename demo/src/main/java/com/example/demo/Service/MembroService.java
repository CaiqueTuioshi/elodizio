package com.example.demo.Service;

import com.example.demo.DTO.MembroDTO;
import com.example.demo.RodizioUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
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
        return this.lerArquivoDeMembros().stream()
                .map(MembroDTO::from)
                .sorted(Comparator.comparing(MembroDTO::getNome))
                .collect(Collectors.toList());
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

    public List<MembroDTO> salvar(String novoMembro) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(MEMBROS, true));

        int maxIdMembro = this.lerArquivoDeMembros().stream()
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

        return this.buscarMembrosDTO();
    }

    public void removerMembro(String id) throws IOException {
        List<String> membros = this.lerArquivoDeMembros();

        String membroEncontrado = membros.stream().filter(membro -> membro.split(PIPE)[0].equals(id)).findFirst().get();
        int indexToLock = membros.indexOf(membroEncontrado);

        List<String> novaListaMembros = new ArrayList<>();
        novaListaMembros.addAll(membros.subList(0, indexToLock));
        novaListaMembros.addAll(membros.subList(indexToLock + 1, membros.size()));

        RodizioUtils.resetarArquivo(MEMBROS);

        novaListaMembros.forEach(this::atualizarESalvar);
    }

    public List<MembroDTO> lockarDeslockarMembro(String id) throws IOException {
        List<String> membros = this.lerArquivoDeMembros();

        String membroEncontrado = membros.stream().filter(membro -> membro.split(PIPE)[0].equals(id)).findFirst().get();
        int indexToLock = membros.indexOf(membroEncontrado);

        if (membroEncontrado.split(PIPE).length > 2) {
            membroEncontrado = membroEncontrado.split(PIPE)[0].concat("|").concat(membroEncontrado.split(PIPE)[1]);
        } else if (membroEncontrado.split(PIPE).length == 2) {
            membroEncontrado = membroEncontrado.split(PIPE)[0].concat("|").concat(membroEncontrado.split(PIPE)[1]).concat("|").concat("*");
        }

        List<String> novaListaMembros = new ArrayList<>();
        novaListaMembros.addAll(membros.subList(0, indexToLock));
        novaListaMembros.add(membroEncontrado);
        novaListaMembros.addAll(membros.subList(indexToLock + 1, membros.size()));

        RodizioUtils.resetarArquivo(MEMBROS);

        novaListaMembros.forEach(this::atualizarESalvar);

        return this.buscarMembrosDTO();
    }

    private void atualizarESalvar(String membro) {
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(MEMBROS, true));

            int maxIdMembro = this.lerArquivoDeMembros().stream()
                    .mapToInt(m -> Integer.valueOf(m.split("\\|")[0]))
                    .max()
                    .orElse(0);

            String lineSeparator = maxIdMembro == 0 ? "" : System.lineSeparator();

            bufferedWriter.append(
                    lineSeparator
//                            .concat(Integer.toString(maxIdMembro + 1))
                            .concat(membro.split("\\|")[0])
                            .concat("|")
                            .concat(membro.split("\\|")[1])
                            .concat(membro.split("\\|").length > 2 ? "|*" : ""));
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
