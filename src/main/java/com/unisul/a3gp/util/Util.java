package com.unisul.eventos.util;

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

}
