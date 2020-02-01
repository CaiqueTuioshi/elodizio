package com.example.demo;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public final class RodizioUtils {

    public static void resetarArquivo(String arquivo) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(arquivo);
        printWriter.close();
    }
}
