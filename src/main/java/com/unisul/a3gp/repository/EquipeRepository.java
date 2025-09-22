package com.unisul.a3gp.repository;

import com.unisul.a3gp.model.Equipe;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class EquipeRepository {

    private static final String FILE = "equipes.data";

    /** Lê todas as equipes do arquivo. */
    public static List<Equipe> loadAll() {
        List<Equipe> equipes = new ArrayList<>();
        Path path = Paths.get(FILE);
        if (!Files.exists(path)) return equipes;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    equipes.add(Equipe.fromCsv(line));
                } catch (Exception e) {
                    System.err.println("Linha inválida ignorada em " + FILE + ": " + line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro lendo " + FILE, e);
        }
        return equipes;
    }

    /** Salva todas as equipes no arquivo (sobrescreve). */
    public static void saveAll(List<Equipe> equipes) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(FILE))) {
            for (Equipe e : equipes) {
                bw.write(e.toCsv());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro salvando " + FILE, e);
        }
    }
}