package com.unisul.a3gp.repository;

import com.unisul.a3gp.model.Projeto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProjetoRepository {

    private static final String FILE = "projetos.data";

    /** Lê todos os projetos do arquivo. */
    public static List<Projeto> loadAll() {
        List<Projeto> projetos = new ArrayList<>();
        Path path = Paths.get(FILE);
        if (!Files.exists(path)) return projetos;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    projetos.add(Projeto.fromCsv(line));
                } catch (Exception e) {
                    System.err.println("Linha inválida ignorada em " + FILE + ": " + line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro lendo " + FILE, e);
        }
        return projetos;
    }

    /** Salva todos os projetos no arquivo (sobrescreve). */
    public static void saveAll(List<Projeto> projetos) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(FILE))) {
            for (Projeto p : projetos) {
                bw.write(p.toCsv());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro salvando " + FILE, e);
        }
    }
}