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

    public List<DuplaDTO> iniciarDuplas(DuplaDTO dupla) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(LOG_DUPLAS, true));

        String lineSeparator = this.lerArquivoDeDuplas().size() == 0 ? "" : System.lineSeparator();

        List<String> membros = this.membroService.lerArquivoDeMembros();

        String membro1 = membros.stream().filter(membro -> membro.split(PIPE)[0].equals(dupla.getMembro1())).findFirst().get().split(PIPE)[0];
        String membro2 = StringUtils.isNotEmpty(dupla.getMembro2())
                ? membros.stream().filter(membro -> membro.split(PIPE)[0].equals(dupla.getMembro2())).findFirst().get().split(PIPE)[0]
                : "";

        bufferedWriter.append(lineSeparator.concat(membro1).concat("|").concat(membro2));
        bufferedWriter.close();

        return buscarDuplas();
    }

    public List<String> lerArquivoDeDuplas() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(LOG_DUPLAS));
        List<String> contentFile = new ArrayList<>();
        String s;

        while ((s = bufferedReader.readLine()) != null) {
            contentFile.add(s);
        }

        bufferedReader.close();

        return contentFile;
    }

    public List<DuplaDTO> construirDuplas() throws IOException {
        List<String> membrosLockados = this.membroService.lerArquivoDeMembros().stream()
                .filter(membro -> membro.split(PIPE).length > 2 && membro.split(PIPE)[2].equals(MEMBRO_LOCKADO)).collect(Collectors.toList());

        List<String> membrosRotativos = this.membroService.lerArquivoDeMembros().stream()
                .filter(membro -> !membrosLockados.contains(membro)).collect(Collectors.toList());

        List<String> novasDuplas = new ArrayList<>();
        List<String> membrosAtualizados = new ArrayList<>();
         this.validarDuplas(membrosLockados, membrosRotativos, novasDuplas, membrosAtualizados);

        return this.buscarDuplas();
    }

    private void validarDuplas(List<String> membrosLockados, List<String> membrosRotativos, List<String> novasDuplas, List<String> membrosAtualizados) throws IOException {
        List<String> duplas = this.lerArquivoDeDuplas();
        List<String> membros = this.membroService.lerArquivoDeMembros();

        List<String> newListMembros = new ArrayList<>();
        newListMembros.addAll(membrosAtualizados.isEmpty() ? membros : membrosAtualizados);

        while (membrosLockados.size() + membrosRotativos.size() > 1) {
            String idMembroLockadoSorteado = membrosLockados.isEmpty() ? this.membroService.getIdMembroRotativo(membrosRotativos) : this.membroService.getIdMembroLockado(membrosLockados);
            String idMembroRotativoSorteado = "";

            while ((!membrosRotativos.isEmpty() && StringUtils.isEmpty(idMembroRotativoSorteado)) || idMembroLockadoSorteado.equals(idMembroRotativoSorteado)) {
                idMembroRotativoSorteado = membrosRotativos.isEmpty() ? "" : this.membroService.getIdMembroRotativo(membrosRotativos);
            }

            String finalIdMembroRotativoSorteado = idMembroRotativoSorteado;
            boolean duplaJaMontada = duplas.stream()
                    .anyMatch(dupla -> (StringUtils.isNotEmpty(idMembroLockadoSorteado) && StringUtils.isNotEmpty(finalIdMembroRotativoSorteado))
                            && (dupla.equals(String.format("%s|%s", idMembroLockadoSorteado, finalIdMembroRotativoSorteado))
                            || dupla.equals(String.format("%s|%s", finalIdMembroRotativoSorteado, idMembroLockadoSorteado))));

            if (duplaJaMontada && membrosLockados.size() + membrosRotativos.size() == 2) {
                this.construirDuplas();
                return;
            }

            if (duplaJaMontada) {
                this.validarDuplas(membrosLockados, membrosRotativos, novasDuplas, membrosAtualizados);
                return;
            }

            if (StringUtils.isNotEmpty(idMembroRotativoSorteado)) {
                String membroRotativo = newListMembros.stream().filter(membro -> membro.split(PIPE)[0].equals(finalIdMembroRotativoSorteado)).findFirst().get();
                int indexmembroRotativo = newListMembros.indexOf(membroRotativo);

                if (!membrosAtualizados.isEmpty()) {
                    membrosAtualizados.clear();
                }
                membrosAtualizados.addAll(newListMembros.subList(0, indexmembroRotativo));
                membrosAtualizados.add(membroRotativo.concat("|*"));
                membrosAtualizados.addAll(newListMembros.subList(indexmembroRotativo + 1, newListMembros.size()));
                newListMembros.clear();
                newListMembros.addAll(membrosAtualizados);

                String membroLockado = newListMembros.stream().filter(m -> m.split(PIPE)[0].equals(idMembroLockadoSorteado)).findFirst().get();
                int indexMembroLockado = newListMembros.indexOf(membroLockado);

                membrosAtualizados.clear();
                membrosAtualizados.addAll(newListMembros.subList(0, indexMembroLockado));
                membrosAtualizados.add(membroLockado.split(PIPE)[0].concat("|").concat(membroLockado.split(PIPE)[1]));
                membrosAtualizados.addAll(newListMembros.subList(indexMembroLockado + 1, newListMembros.size()));
                newListMembros.clear();
                newListMembros.addAll(membrosAtualizados);

                novasDuplas.add(idMembroRotativoSorteado.concat("|").concat(idMembroLockadoSorteado));
            } else {
                String membroLockado = newListMembros.stream()
                        .filter(membro -> membro.split(PIPE)[0].equals(idMembroLockadoSorteado)).findFirst().get();

                int indexMembroLockado = newListMembros.indexOf(membroLockado);

                membrosAtualizados.clear();
                membrosAtualizados.addAll(newListMembros.subList(0, indexMembroLockado));
                membrosAtualizados.add(membroLockado.split(PIPE)[0].concat("|").concat(membroLockado.split(PIPE)[1]).concat("|*"));
                membrosAtualizados.addAll(newListMembros.subList(indexMembroLockado + 1, newListMembros.size()));
                newListMembros.clear();
                newListMembros.addAll(membrosAtualizados);

                novasDuplas.add(idMembroLockadoSorteado.concat("|").concat(idMembroRotativoSorteado));
            }


            if (membrosLockados.isEmpty()) {
                membrosRotativos.removeIf(membro -> membro.split(PIPE)[0].equals(idMembroLockadoSorteado));
            } else {

                membrosLockados.removeIf(membro -> membro.split(PIPE)[0].equals(idMembroLockadoSorteado));
            }

            if (membrosRotativos.isEmpty()) {
                membrosLockados.removeIf(membro -> membro.split(PIPE)[0].equals(finalIdMembroRotativoSorteado));
            } else {

                membrosRotativos.removeIf(membro -> membro.split(PIPE)[0].equals(finalIdMembroRotativoSorteado));
            }
        }

        if (!membrosLockados.isEmpty()) {
            String membroLockado = newListMembros.stream().filter(membro -> membro.split(PIPE)[0].equals(membrosLockados.get(0).split(PIPE)[0])).findFirst().get();
            int indexMembroLockado = newListMembros.indexOf(membroLockado);

            membrosAtualizados.clear();
            membrosAtualizados.addAll(newListMembros.subList(0, indexMembroLockado));
            membrosAtualizados.add(membrosLockados.get(0).split(PIPE)[0].concat("|").concat(membrosLockados.get(0).split(PIPE)[1]).concat("|*"));
            membrosAtualizados.addAll(newListMembros.subList(indexMembroLockado + 1, newListMembros.size()));
            newListMembros.clear();
            newListMembros.addAll(membrosAtualizados);

            novasDuplas.add(membrosLockados.get(0).split(PIPE)[0].concat("|"));
            membrosLockados.clear();
        }

        if (!membrosRotativos.isEmpty()) {
            String membroRotativos = newListMembros.stream().filter(membro -> membro.split(PIPE)[0].equals(membrosRotativos.get(0).split(PIPE)[0])).findFirst().get();
            int indexMembroRotativos = newListMembros.indexOf(membroRotativos);

            membrosAtualizados.clear();
            membrosAtualizados.addAll(newListMembros.subList(0, indexMembroRotativos));
            membrosAtualizados.add(membrosRotativos.get(0).split(PIPE)[0].concat("|").concat(membrosRotativos.get(0).split(PIPE)[1]).concat("|*"));
            membrosAtualizados.addAll(newListMembros.subList(indexMembroRotativos + 1, newListMembros.size()));
            newListMembros.clear();
            newListMembros.addAll(membrosAtualizados);

            novasDuplas.add(membrosRotativos.get(0).split(PIPE)[0].concat("|"));
            membrosRotativos.clear();
        }

        this.escreverDuplas(novasDuplas);

        RodizioUtils.resetarArquivo(MEMBROS);

        membrosAtualizados.forEach(this.membroService::atualizarESalvar);
    }

    public void escreverDuplas(List<String> novaListaDuplas) throws IOException {
        RodizioUtils.resetarArquivo(LOG_DUPLAS);

        BufferedWriter bufferedWriterLog = new BufferedWriter(new FileWriter(LOG_DUPLAS, true));
        bufferedWriterLog.append(String.join(System.lineSeparator(), novaListaDuplas));
        bufferedWriterLog.close();
    }
}
