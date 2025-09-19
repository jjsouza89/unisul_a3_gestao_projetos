package com.unisul.eventos.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final String ALGORITHM = "SHA-256"; // simples e seguro para começar
    private static final int SALT_BYTES = 16;

    /** Gera um salt aleatório em Base64 */
    public static String gerarSalt() {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /** Gera o hash de uma senha usando o salt informado */
    public static String hashSenha(String senha, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            md.update(Base64.getDecoder().decode(salt)); // mistura o salt
            byte[] hashed = md.digest(senha.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash de senha", e);
        }
    }

    /** Verifica se a senha em claro bate com o hash armazenado */
    public static boolean verificarSenha(String senhaEmClaro, String salt, String hashArmazenado) {
        String hashTentativa = hashSenha(senhaEmClaro, salt);
        return hashTentativa.equals(hashArmazenado);
    }
}
