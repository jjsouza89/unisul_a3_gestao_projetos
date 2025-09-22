package com.unisul.a3gp.repository;

import com.unisul.a3gp.model.Usuario;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {

    private static final String FILE = "usuarios.data";

    /** Lê todos os usuários do arquivo. */
    public static List<Usuario> loadAll() {
        List<Usuario> usuarios = new ArrayList<>();
        Path path = Paths.get(FILE);
        if (!Files.exists(path)) return usuarios;

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    usuarios.add(Usuario.fromCsv(line));
                } catch (Exception e) {
                    System.err.println("Linha inválida ignorada em " + FILE + ": " + line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro lendo " + FILE, e);
        }
        return usuarios;
    }

    /** Salva todos os usuários no arquivo (sobrescreve). */
    public static void saveAll(List<Usuario> usuarios) {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(FILE))) {
            for (Usuario u : usuarios) {
                bw.write(u.toCsv());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro salvando " + FILE, e);
        }
    }
}