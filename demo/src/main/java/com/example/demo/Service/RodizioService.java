package com.example.demo.Service;

import com.example.demo.DTO.DuplaDTO;
import com.example.demo.DTO.MembroDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RodizioService {

    private final String MEMBROS = System.getProperty("user.dir").concat("/src/main/java/com/example/demo/File/membros.txt");
    private final String LOG_DUPLAS = System.getProperty("user.dir").concat("/src/main/java/com/example/demo/File/log_duplas.txt");
    private final String PIPE = "\\|";
    private final String MEMBRO_LOCKADO = "*";

    private List<String> lerArquivoDeMembros() throws IOException {
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

    public List<DuplaDTO> buscarDuplas() throws IOException {
        List<String> membros = this.lerArquivoDeMembros();
        
        return this.lerArquivoDeDuplas().stream().map(dupla -> DuplaDTO.from(dupla, membros)).collect(Collectors.toList());
    }

    public List<String> iniciarDuplas(DuplaDTO dupla) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(LOG_DUPLAS, true));

        String lineSeparator = this.lerArquivoDeDuplas().size() == 0 ? "" : System.lineSeparator();

        String membro1 = this.lerArquivoDeMembros().stream()
                .filter(membro -> membro.split(PIPE)[1].equals(dupla.getMembro1())).findFirst().get()
                .split(PIPE)[0];

        String membro2 = this.lerArquivoDeMembros().stream()
                .filter(membro -> membro.split(PIPE)[1].equals(dupla.getMembro2())).findFirst().get()
                .split(PIPE)[0];

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

    private String sortearMembro() throws IOException {
        int membro = 0;
        while (membro == 0) {
            membro = ((int) (this.lerArquivoDeMembros().size() * Math.random()));
        }

        return String.valueOf(membro);
    }

    private void resetarArquivo(String arquivo) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(arquivo);
        printWriter.close();
    }

    private String sortearMembroFixo(List<String> membrosLockados) throws IOException {
        List<String> idMembrosLockados = membrosLockados.stream().map(membro -> membro.split(PIPE)[0]).collect(Collectors.toList());

        String sorteado = "";
        while(!idMembrosLockados.contains(sorteado)){
            sorteado = this.sortearMembro();
        }

        return sorteado;
    }

    private String sortearMembroRotativo(List<String> membrosRotativos) throws IOException {
        List<String> idMembrosRotativos = membrosRotativos.stream().map(membro -> membro.split(PIPE)[0]).collect(Collectors.toList());

        String sorteado = "";
        while(!idMembrosRotativos.contains(sorteado)){
            sorteado = this.sortearMembro();
        }

        return sorteado;
    }

    private String getIdMembroLockadoAndRemove(List<String> membrosLockados) throws IOException {
        if (membrosLockados.size() > 0) {
            String idMembroLockadoSorteado = this.sortearMembroFixo(membrosLockados);

            return idMembroLockadoSorteado;
        }

        return "";
    }

    private String getIdMembroRotativoAndRemove(List<String> membrosRotativos) throws IOException {
        if (membrosRotativos.size() > 0) {
            String idMembroRotativoSorteado = this.sortearMembroRotativo(membrosRotativos);

            return idMembroRotativoSorteado;
        }

        return "";
    }

    private void validarDuplas(List<String> membrosLockados, List<String> membrosRotativos, List<String> novasDuplas) throws IOException {
        List<String> duplas = this.lerArquivoDeDuplas();

        while (membrosLockados.size() + membrosRotativos.size() > 1) {
            String idMembroLockadoSorteado = this.getIdMembroLockadoAndRemove(membrosLockados);
            String idMembroRotativoSorteado = this.getIdMembroRotativoAndRemove(membrosRotativos);

            if (StringUtils.isEmpty(idMembroLockadoSorteado)) {
                if (membrosRotativos.size() > 1) {
                    idMembroLockadoSorteado = this.getIdMembroRotativoAndRemove(membrosRotativos);
                }
            }

            String finalIdMembroLockadoSorteado = idMembroLockadoSorteado;
            boolean duplaJaMontada = duplas.stream().anyMatch(dupla ->
                    dupla.contains(finalIdMembroLockadoSorteado) && dupla.contains(idMembroRotativoSorteado)
            );

            if (duplaJaMontada && membrosLockados.size() + membrosRotativos.size() == 2) {
                this.construirDuplas();
            }

            if (duplaJaMontada) {
                this.validarDuplas(membrosLockados, membrosRotativos, novasDuplas);
            }

            novasDuplas.add(finalIdMembroLockadoSorteado.concat("|").concat(idMembroRotativoSorteado));
            membrosLockados.removeIf(membro -> membro.split("\\|")[0].equals(finalIdMembroLockadoSorteado));
            membrosRotativos.removeIf(membro -> membro.split("\\|")[0].equals(idMembroRotativoSorteado));
        }

        if (!membrosLockados.isEmpty()){

            novasDuplas.add(membrosLockados.get(0).split("\\|")[0].concat("|"));
        }

        if (!membrosRotativos.isEmpty()){
            novasDuplas.add(membrosRotativos.get(0).split("\\|")[0].concat("|"));
        }

        this.resetarArquivo(LOG_DUPLAS);

//        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(LOG_DUPLAS, true));
//        bufferedWriter.append(String.join(System.lineSeparator(), novasDuplas));
    }

    public List<String> construirDuplas() throws IOException {
        List<String> membrosLockados = this.lerArquivoDeMembros().stream()
                .filter(membro -> membro.split("\\|").length > 2 && membro.split("\\|")[2].equals(MEMBRO_LOCKADO)).collect(Collectors.toList());

        List<String> membrosRotativos = this.lerArquivoDeMembros().stream()
                .filter(membro -> !membrosLockados.contains(membro)).collect(Collectors.toList());

        List<String> novasDuplas = new ArrayList<>();

        this.validarDuplas(membrosLockados, membrosRotativos, novasDuplas);

        return this.lerArquivoDeDuplas();
    }

    public List<String> salvar(String novoMembro) throws IOException {
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

        return lerArquivoDeMembros();
    }

    public List<String> remover(Integer idMembro) throws IOException {
        List<String> membros = this.lerArquivoDeMembros();
        membros.removeIf(membro -> idMembro.equals(Integer.valueOf(membro.split("\\|")[0])));

        this.resetarArquivo(MEMBROS);

        membros.forEach(membro -> {
            try {
                this.salvar(membro.split("\\|")[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return lerArquivoDeMembros();
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
