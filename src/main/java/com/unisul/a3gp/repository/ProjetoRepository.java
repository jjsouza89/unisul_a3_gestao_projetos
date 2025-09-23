package com.unisul.a3gp.repository;

import com.unisul.a3gp.model.Projeto;
import com.unisul.a3gp.model.Equipe;
import com.unisul.a3gp.model.Usuario;

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

    /** Resolve vínculos de gerente e equipe a partir dos IDs pendentes. */
    public static void resolverVinculos(List<Projeto> projetos, List<Usuario> usuarios, List<Equipe> equipes) {
        for (Projeto p : projetos) {
            if (p.getPendingGerenteId() != null) {
                usuarios.stream()
                        .filter(u -> u.getId().equals(p.getPendingGerenteId()))
                        .findFirst()
                        .ifPresent(p::setGerenteResponsavel);
            }
            if (p.getPendingEquipeId() != null) {
                equipes.stream()
                        .filter(e -> e.getId().equals(p.getPendingEquipeId()))
                        .findFirst()
                        .ifPresent(p::setEquipe);
            }
        }
    }
}