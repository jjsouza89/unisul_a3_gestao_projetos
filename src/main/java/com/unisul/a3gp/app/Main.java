package com.unisul.a3gp.app;

import com.unisul.a3gp.model.Usuario;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    // ─────────────── ESTADO EM MEMÓRIA ───────────────
    private static final List<Usuario> usuarios = new ArrayList<>();

    // Formato de data/hora para o console
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        loopMenu();

        System.out.println("Até mais!");
    }

    // ─────────────── MENU PRINCIPAL ───────────────
    private static void loopMenu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Sistema de Gestão de Projetos (Console) ===");
            System.out.println("1) Cadastrar usuário");
            System.out.println("2) Cadastrar Projeto");
            System.out.println("3) Listar Usuários");
            System.out.println("4) Listar Projetos");
            System.out.println("5) Atribuir Gerente de Projetos");
            System.out.println("6) Listar Projetos do Gerente");

            System.out.println("9) Sair");
            System.out.print("Opção: ");
            String op = sc.nextLine().trim();

            switch (op) {
                case "1":
                    cadastrarUsuario(sc);
                    break;
                case "2":
                    cadastrarUsuario(sc);
                    break;
                case "3":
                    listarUsuarios();
                    break;
                case "9":
                    return;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    // ─────────────── FLUXOS ───────────────

    private static void cadastrarUsuario(Scanner sc) {
        System.out.println("\n>>> Cadastro de Usuário");
        String nome = readLine(sc, "Nome: ");
        String email = readLine(sc, "Email: ");
        String telefone = readLine(sc, "Telefone: ");
        String senha = readLine(sc, "Senha: ");

        Usuario u = new Usuario(nome, email, telefone);
        u.definirSenha(senha);
        usuarios.add(u);

        System.out.println("Usuário cadastrado com sucesso! ID: " + u.getId());
    }

    private static void listarUsuarios() {
        System.out.println("\n>>> Usuários cadastrados");
        if (usuarios.isEmpty()) {
            System.out.println("(nenhum usuário cadastrado ainda)");
            return;
        }

        for (Usuario u : usuarios) {
            System.out.printf("- %s | %s | %s (cadastrado em %s)\n",
                u.getNome(),
                u.getEmail(),
                u.getTelefone(),
                u.getDataCadastro());
            }
    }

    // ─────────────── HELPERS ───────────────

    private static String readLine(Scanner sc, String label) {
        System.out.print(label);
        return sc.nextLine().trim();
    }

    private static LocalDateTime readDateTime(Scanner sc, String label) {
        while (true) {
            String s = readLine(sc, label);
            try {
                return LocalDateTime.parse(s, DTF);
            } catch (Exception e) {
                System.out.println("Data/hora inválida. Use o formato yyyy-MM-dd HH:mm");
            }
        }
    }

}