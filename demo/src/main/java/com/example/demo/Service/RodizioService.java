package com.example.demo.Service;

import com.example.demo.DTO.DuplaDTO;
import com.example.demo.DTO.MembroDTO;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RodizioService {

    private final String MEMBROS = System.getProperty("user.dir").concat("/src/main/java/com/example/demo/File/membros.txt");
    private final String LOG_DUPLAS = System.getProperty("user.dir").concat("/src/main/java/com/example/demo/File/log_duplas.txt");

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

    public List<String> iniciarDuplas(DuplaDTO dupla) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(LOG_DUPLAS, true));

        String lineSeparator = this.lerArquivoDeDuplas().size() == 0 ? "" : System.lineSeparator();

        String membro1 = this.lerArquivoDeMembros().stream()
                .filter(membro -> membro.split("\\|")[1].equals(dupla.getMembro1())).findFirst().get()
                .split("\\|")[0];

        String membro2 = this.lerArquivoDeMembros().stream()
                .filter(membro -> membro.split("\\|")[1].equals(dupla.getMembro2())).findFirst().get()
                .split("\\|")[0];

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

    private String sortearMembro(List<String> membros) {
        int membro = 0;
        while (membro == 0) {
            membro = ((int) (membros.size() * Math.random()));
        }

        return String.valueOf(membro);
    }

    private void resetarArquivo(String arquivo) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(arquivo);
        printWriter.close();
    }

    private void validarDuplas(List<String> membros, List<String> novasDuplas) throws IOException {
        List<String> duplas = this.lerArquivoDeDuplas();

        while (!membros.isEmpty()) {
            String membro1 = this.sortearMembro(membros);
            String membro2 = this.sortearMembro(membros);

            boolean duplaJaMontada = duplas.stream().anyMatch(dupla ->
                    dupla.contains(membro1) && dupla.contains(membro2)
            );

            if (duplaJaMontada && membros.size() == 2) {
                this.construirDuplas();
            }

            if (duplaJaMontada) {
                this.validarDuplas(membros, novasDuplas);
            }

            String lineSeparator = novasDuplas.size() == 0 ? "" : System.lineSeparator();

            novasDuplas.add(membro1.concat("|").concat(membro2));
            membros.removeIf(membro -> membro.split("\\|")[0].equals(membro1));
            membros.removeIf(membro -> membro.split("\\|")[0].equals(membro2));
        }

        this.resetarArquivo(LOG_DUPLAS);

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(LOG_DUPLAS, true));
        bufferedWriter.append(String.join(System.lineSeparator(), novasDuplas));
    }

    public List<String> construirDuplas() throws IOException {
        List<String> membros = this.lerArquivoDeMembros();
        List<String> novasDuplas = new ArrayList<>();

        this.validarDuplas(membros, novasDuplas);

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
