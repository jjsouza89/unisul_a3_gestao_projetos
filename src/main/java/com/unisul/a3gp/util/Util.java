package com.unisul.a3gp.util;
import com.unisul.a3gp.model.Usuario;

import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class Util {

    // Versão genérica: serve para qualquer tipo
    public static <T> T nvl(T value, T defaultValue) {
        return (value == null) ? defaultValue : value;
    }

    // Sobrecarga específica para String (mais comum)
    public static String nvl(String s) {
        return nvl(s, "");
    }

    // Escapar Strings (evita problemas no CSV, por exemplo)
    public static String esc(String s) {
        return (s == null) ? "" : s.replace(";", ",");
    }

    // Reverter escape (se precisar no futuro)
    public static String des(String s) {
        return s; // aqui dá pra expandir depois
    }

    /** Converte string vazia ou null em null */
    public static String vazioParaNull(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }

    public static String readUnique(Scanner sc, String label, List<Usuario> usuarios,
                                    Function<Usuario, String> getter, String campo) {
        while (true) {
            System.out.print(label);
            String valor = sc.nextLine().trim();

            boolean exists = usuarios.stream()
                    .anyMatch(u -> valor.equalsIgnoreCase(getter.apply(u)));

            if (exists) {
                System.out.printf("❌ Já existe um usuário com este %s: %s. Informe outro.%n", campo, valor);
            } else {
                return valor;
            }
        }
    }

}
