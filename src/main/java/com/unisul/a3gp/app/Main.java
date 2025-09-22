//1. Cadastro de Usuários
//a) Nome completo, CPF, e-mail, cargo, login, senha.
//b) Cada usuário deve ter um
// perfil: administrador, gerente ou colaborador.
//2. Cadastro de Projetos
//c) Nome do projeto, descrição, data de início, data de término prevista, status (planejado, em andamento, concluído, cancelado).
//d) Cada projeto deve ter um gerente responsável.
//3. Cadastro de Equipes
//e) Nome da equipe, descrição, membros (usuários vinculados).
//f) Uma equipe pode atuar em vários projetos.

package com.unisul.a3gp.app;

import com.unisul.a3gp.model.Usuario;
import com.unisul.a3gp.model.Equipe;
import com.unisul.a3gp.model.Projeto;
import com.unisul.a3gp.model.Perfil;
import com.unisul.a3gp.model.StatusProjeto;
import com.unisul.a3gp.util.Util;
import com.unisul.a3gp.repository.UsuarioRepository;
import com.unisul.a3gp.repository.EquipeRepository;
import com.unisul.a3gp.repository.ProjetoRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    // ─────────────── ESTADO EM MEMÓRIA ───────────────
    private static final List<Usuario> usuarios = new ArrayList<>();
    private static final List<Equipe> equipes = new ArrayList<>();
    private static final List<Projeto> projetos = new ArrayList<>();

    // Formato de data/hora para o console
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        // Load inicial
        usuarios.addAll(UsuarioRepository.loadAll());
        equipes.addAll(EquipeRepository.loadAll());
        projetos.addAll(ProjetoRepository.loadAll());

        loopMenu();

        // Save final
        UsuarioRepository.saveAll(usuarios);
        EquipeRepository.saveAll(equipes);
        ProjetoRepository.saveAll(projetos);

        System.out.println("Até mais!");
    }

    // ─────────────── MENU PRINCIPAL ───────────────
    private static void loopMenu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Sistema de Gestão de Projetos (Console) ===");
            System.out.println("1) Cadastrar Usuário");
            System.out.println("2) Cadastrar Projeto");
            System.out.println("3) Cadastrar Equipe");
            System.out.println("4) Listar Usuários");
            System.out.println("5) Listar Projetos");
            System.out.println("6) Listar Equipes");
            System.out.println("7) Atribuir Gerente de Projetos");
            System.out.println("8) Listar Projetos do Gerente");

            System.out.println("9) Sair");
            System.out.print("Opção: ");
            String op = sc.nextLine().trim();

            switch (op) {
                case "1":
                    cadastrarUsuario(sc);
                    break;
                case "2":
                    cadastrarProjeto(sc);
                    break;
                case "3":
                    cadastrarEquipe(sc);
                    break;
                case "4":
                    listarUsuarios();
                    break;
                case "5":
                    listarProjetos();
                    break;
                case "6":
                    listarEquipes();
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
        String nome = readLine(sc, "Nome completo: ");
        String cpf = Util.readUnique(sc, "CPF: ", usuarios, Usuario::getCpf, "CPF");
        String email = Util.readUnique(sc, "E-mail: ", usuarios, Usuario::getEmail, "E-mail");
        String login = Util.readUnique(sc, "Login: ", usuarios, Usuario::getLogin, "Login");
        String senha = readLine(sc, "Senha: ");
        String telefone = readLine(sc, "Telefone: ");
        String cargo = readLine(sc, "Cargo: ");
        System.out.println("Perfil (1=Administrador, 2=Gerente, 3=Colaborador): ");
        String opcao = sc.nextLine().trim();
        Perfil perfil;
        switch (opcao) {
            case "1": perfil = Perfil.ADMINISTRADOR; break;
            case "2": perfil = Perfil.GERENTE; break;
            default: perfil = Perfil.COLABORADOR; break;
        }

        Usuario u = new Usuario(nome, cpf, email, telefone, cargo, login, perfil);
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
            System.out.printf("- %s | CPF: %s | Email: %s | Cargo: %s | Login: %s | Perfil: %s | Cadastrado em: %s%n",
                    u.getNome(),
                    u.getCpf(),
                    u.getEmail(),
                    u.getCargo(),
                    u.getLogin(),
                    u.getPerfil(),
                    u.getDataCadastro()
            );
        }
    }

    // ─────────────── FLUXOS DE EQUIPE ───────────────

    private static void cadastrarEquipe(Scanner sc) {
        System.out.println("\n>>> Cadastro de Equipe");

        String nome = readLine(sc, "Nome da equipe: ");
        String descricao = readLine(sc, "Descrição: ");

        Equipe equipe = new Equipe(nome, descricao);

        // Permitir adicionar membros
        while (true) {
            System.out.println("Deseja adicionar um membro à equipe? (s/n)");
            String resp = sc.nextLine().trim().toLowerCase();
            if (resp.equals("s")) {
                if (usuarios.isEmpty()) {
                    System.out.println("❌ Não há usuários cadastrados para adicionar.");
                    break;
                }
                // Listar usuários disponíveis
                for (int i = 0; i < usuarios.size(); i++) {
                    System.out.printf("%d) %s (%s)%n", i + 1, usuarios.get(i).getNome(), usuarios.get(i).getEmail());
                }
                System.out.print("Escolha o número do usuário: ");
                try {
                    int idx = Integer.parseInt(sc.nextLine().trim()) - 1;
                    if (idx >= 0 && idx < usuarios.size()) {
                        equipe.addMembro(usuarios.get(idx));
                        System.out.println("✅ Membro adicionado.");
                    } else {
                        System.out.println("Opção inválida.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida.");
                }
            } else {
                break;
            }
        }

        equipes.add(equipe);
        System.out.println("✅ Equipe cadastrada com sucesso! ID: " + equipe.getId());
    }

    private static void listarEquipes() {
        System.out.println("\n>>> Equipes cadastradas");
        if (equipes.isEmpty()) {
            System.out.println("(nenhuma equipe cadastrada ainda)");
            return;
        }

        for (Equipe e : equipes) {
            System.out.printf("- %s | %s | Membros: %d%n",
                    e.getNome(),
                    e.getDescricao(),
                    e.getMembros().size()
            );
        }
    }

// ─────────────── FLUXOS DE PROJETO ───────────────

    private static void cadastrarProjeto(Scanner sc) {
        System.out.println("\n>>> Cadastro de Projeto");

        String nome = readLine(sc, "Nome do projeto: ");
        String descricao = readLine(sc, "Descrição: ");
        LocalDate dataInicio = readDate(sc, "Data de início (yyyy-MM-dd): ");
        LocalDate dataFimPrevista = readDate(sc, "Data de término prevista (yyyy-MM-dd): ");

        // Status do projeto
        System.out.println("Status do projeto:");
        System.out.println("1) Planejado");
        System.out.println("2) Em andamento");
        System.out.println("3) Concluído");
        System.out.println("4) Cancelado");
        String statusOp = sc.nextLine().trim();
        StatusProjeto status;
        switch (statusOp) {
            case "1": status = StatusProjeto.PLANEJADO; break;
            case "2": status = StatusProjeto.EM_ANDAMENTO; break;
            case "3": status = StatusProjeto.CONCLUIDO; break;
            case "4": status = StatusProjeto.CANCELADO; break;
            default: status = StatusProjeto.PLANEJADO; break;
        }

        // Selecionar gerente responsável
        List<Usuario> gerentes = usuarios.stream()
                .filter(u -> u.getPerfil() == Perfil.GERENTE)
                .toList();

        if (gerentes.isEmpty()) {
            System.out.println("❌ Nenhum gerente cadastrado. Cadastre um gerente primeiro.");
            return;
        }
        System.out.println("Selecione o gerente responsável:");
        for (int i = 0; i < gerentes.size(); i++) {
            System.out.printf("%d) %s (%s)%n", i + 1, gerentes.get(i).getNome(), gerentes.get(i).getEmail());
        }
        int gerenteIdx = Integer.parseInt(sc.nextLine().trim()) - 1;
        Usuario gerente = gerentes.get(gerenteIdx);

        // Selecionar equipe vinculada
        if (equipes.isEmpty()) {
            System.out.println("❌ Nenhuma equipe cadastrada. Cadastre uma equipe primeiro.");
            return;
        }
        System.out.println("Selecione a equipe vinculada:");
        for (int i = 0; i < equipes.size(); i++) {
            System.out.printf("%d) %s (%s)%n", i + 1, equipes.get(i).getNome(), equipes.get(i).getDescricao());
        }
        int equipeIdx = Integer.parseInt(sc.nextLine().trim()) - 1;
        Equipe equipe = equipes.get(equipeIdx);

        // Criar projeto
        Projeto p = new Projeto(nome, descricao, dataInicio, dataFimPrevista, status, gerente, equipe);
        projetos.add(p);

        System.out.println("✅ Projeto cadastrado com sucesso! ID: " + p.getId());
    }

    private static void listarProjetos() {
        System.out.println("\n>>> Projetos cadastrados");
        if (projetos.isEmpty()) {
            System.out.println("(nenhum projeto cadastrado ainda)");
            return;
        }

        for (Projeto p : projetos) {
            System.out.printf("- %s | %s | Início: %s | Fim previsto: %s | Status: %s | Gerente: %s | Equipe: %s%n",
                    p.getNome(),
                    p.getDescricao(),
                    p.getDataInicio(),
                    p.getDataFimPrevista(),
                    p.getStatus(),
                    p.getGerenteResponsavel() != null ? p.getGerenteResponsavel().getNome() : "(sem gerente)",
                    p.getEquipe() != null ? p.getEquipe().getNome() : "(sem equipe)"
            );
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

    private static LocalDate readDate(Scanner sc, String label) {
        while (true) {
            String s = readLine(sc, label);
            try {
                return LocalDate.parse(s);
            } catch (Exception e) {
                System.out.println("Data inválida. Use o formato yyyy-MM-dd");
            }
        }
    }



}