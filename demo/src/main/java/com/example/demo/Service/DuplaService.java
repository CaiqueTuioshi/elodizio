package com.example.demo.Service;

import com.example.demo.DTO.DuplaDTO;
import com.example.demo.RodizioUtils;
import org.apache.commons.lang3.StringUtils;
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
    private final String MEMBROS = System.getProperty("user.dir").concat("/src/main/java/com/example/demo/File/membros.txt");

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
        List<String> membrosAtualizados = new ArrayList<>();
        this.validarDuplas(membrosLockados, membrosRotativos, novasDuplas, membrosAtualizados);

        return this.lerArquivoDeDuplas();
    }

    private void validarDuplas(List<String> membrosLockados, List<String> membrosRotativos, List<String> novasDuplas, List<String> membrosAtualizados) throws IOException {
        List<String> duplas = this.lerArquivoDeDuplas();

        while (membrosLockados.size() + membrosRotativos.size() > 1) {
            String idMembroLockadoSorteado = membrosLockados.isEmpty() ? this.membroService.getIdMembroRotativo(membrosRotativos) : this.membroService.getIdMembroLockado(membrosLockados);
            String idMembroRotativoSorteado = membrosRotativos.isEmpty() ? "" : this.membroService.getIdMembroRotativo(membrosRotativos);

            boolean duplaJaMontada = duplas.stream()
                    .anyMatch(dupla -> dupla.equals(String.format("%s|%s", idMembroLockadoSorteado, idMembroRotativoSorteado))
                            || dupla.equals(String.format("%s|%s", idMembroRotativoSorteado, idMembroLockadoSorteado)));

            if (duplaJaMontada && membrosLockados.size() + membrosRotativos.size() == 2) {
                this.construirDuplas();
                return;
            }

            if (duplaJaMontada) {
                this.validarDuplas(membrosLockados, membrosRotativos, novasDuplas, membrosAtualizados);
                return;
            }

            if (StringUtils.isNotEmpty(idMembroRotativoSorteado)) {
                String membroRotativo = this.membroService.lerArquivoDeMembros().stream()
                        .filter(membro -> membro.split(PIPE)[0].equals(idMembroRotativoSorteado)).findFirst().get();
                membrosAtualizados.add(membroRotativo.concat("|*"));

                String membroLockado = this.membroService.lerArquivoDeMembros().stream()
                        .filter(m -> m.split(PIPE)[0].equals(idMembroLockadoSorteado)).findFirst().get();
                membrosAtualizados.add(membroLockado.split(PIPE)[0].concat("|").concat(membroLockado.split(PIPE)[1]));

                novasDuplas.add(idMembroRotativoSorteado.concat("|").concat(idMembroLockadoSorteado));
            } else {
                String membroLockado = this.membroService.lerArquivoDeMembros().stream()
                        .filter(membro -> membro.split(PIPE)[0].equals(idMembroLockadoSorteado)).findFirst().get();
                membrosAtualizados.add(membroLockado.split(PIPE)[0].concat("|").concat(membroLockado.split(PIPE)[1]).concat("|*"));

                novasDuplas.add(idMembroLockadoSorteado.concat("|").concat(idMembroRotativoSorteado));
            }


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
            membrosAtualizados.add(membrosLockados.get(0).split("\\|")[0].concat("|").concat(membrosLockados.get(0).split("\\|")[1]).concat("|*"));
            membrosLockados.clear();
        }

        if (!membrosRotativos.isEmpty()) {
            novasDuplas.add(membrosRotativos.get(0).split("\\|")[0].concat("|"));
            membrosAtualizados.add(membrosRotativos.get(0).split("\\|")[0].concat("|").concat(membrosRotativos.get(0).split("\\|")[1]).concat("|*"));
            membrosRotativos.clear();
        }

        RodizioUtils.resetarArquivo(LOG_DUPLAS);

        BufferedWriter bufferedWriterLog = new BufferedWriter(new FileWriter(LOG_DUPLAS, true));
        bufferedWriterLog.append(String.join(System.lineSeparator(), novasDuplas));
        bufferedWriterLog.close();

        RodizioUtils.resetarArquivo(MEMBROS);

        membrosAtualizados.forEach(membro -> {
            BufferedWriter bufferedWriter = null;
            try {
                bufferedWriter = new BufferedWriter(new FileWriter(MEMBROS, true));

                int maxIdMembro = this.membroService.lerArquivoDeMembros().stream()
                        .mapToInt(m -> Integer.valueOf(m.split("\\|")[0]))
                        .max()
                        .orElse(0);

                String lineSeparator = maxIdMembro == 0 ? "" : System.lineSeparator();

                bufferedWriter.append(
                        lineSeparator
                                .concat(Integer.toString(maxIdMembro + 1))
                                .concat("|")
                                .concat(membro.split("\\|")[1])
                                .concat(membro.split("\\|").length > 2 ? "|*" : ""));
                bufferedWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }
}
