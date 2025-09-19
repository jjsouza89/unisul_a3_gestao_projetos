package com.unisul.eventos.app;

import com.unisul.eventos.model.CategoriaEvento;
import com.unisul.eventos.model.Evento;
import com.unisul.eventos.model.Usuario;
import com.unisul.eventos.model.Participacao;
import com.unisul.eventos.repository.EventoRepository;
import com.unisul.eventos.repository.UsuarioRepository;
import com.unisul.eventos.repository.ParticipacaoRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    // ─────────────── ESTADO EM MEMÓRIA ───────────────
    private static final List<Usuario> usuarios = new ArrayList<>();
    private static final List<Evento> eventos = new ArrayList<>();
    private static final List<Participacao> participacoes = new ArrayList<>();


    // Formato de data/hora para o console
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        usuarios.addAll(UsuarioRepository.loadAll());
        eventos.addAll(EventoRepository.loadAll());
        participacoes.addAll(ParticipacaoRepository.load());

        loopMenu();

        UsuarioRepository.saveAll(usuarios);
        EventoRepository.saveAll(eventos);
        ParticipacaoRepository.save(participacoes);

        System.out.println("Até mais!");
    }

    // ─────────────── MENU PRINCIPAL ───────────────
    private static void loopMenu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n=== Sistema de Eventos (Console) ===");
            System.out.println("1) Cadastrar usuário");
            System.out.println("2) Cadastrar evento");
            System.out.println("3) Listar eventos");
            System.out.println("4) Listar usuários");
            System.out.println("5) Confirmar participação em evento");
            System.out.println("6) Cancelar participação");
            System.out.println("7) Listar eventos do usuario");
            System.out.println("8) Listar participantes do evento");

            System.out.println("9) Sair");
            System.out.print("Opção: ");
            String op = sc.nextLine().trim();

            switch (op) {
                case "1":
                    cadastrarUsuario(sc);
                    break;
                case "2":
                    cadastrarEvento(sc);
                    break;
                case "3":
                    listarEventos();
                    break;
                case "4":
                    listarUsuarios();
                    break;
                case "5":
                    confirmarParticipacao(sc);
                    break;
                case "6":
                    cancelarParticipacao(sc);
                    break;
                case "7":
                    listarParticipacoes(sc);
                    break;
                case "8":
                    listarUsuariosEvento(sc);
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

    private static void cadastrarEvento(Scanner sc) {
        System.out.println("\n>>> Cadastro de Evento");
        String nome = readLine(sc, "Nome: ");
        String endereco = readLine(sc, "Endereço: ");

        CategoriaEvento categoria = escolherCategoria(sc);

        LocalDateTime horario = readDateTime(sc,
                "Horário (formato yyyy-MM-dd HH:mm, ex: 2025-09-20 20:00): ");

        String duracaoStr = readLine(sc, "Duração (minutos) [vazio = 120]: ");
        int duracaoMin = duracaoStr.isBlank() ? 120 : Integer.parseInt(duracaoStr);

        String descricao = readLine(sc, "Descrição: ");

        Evento e = new Evento(nome, endereco, categoria, horario, duracaoMin, descricao);
        eventos.add(e);

        System.out.println("Evento cadastrado! ID: " + e.getId());
    }

    private static void listarEventos() {
        System.out.println("\n>>> Eventos cadastrados");
        if (eventos.isEmpty()) {
            System.out.println("(nenhum evento ainda)");
            return;
        }

        eventos.sort(Comparator.comparing(Evento::getHorario));

        for (Evento e : eventos) {
            String status;
            if (e.isOngoing()) status = "AGORA";
            else if (e.isPast()) status = "PASSADO";
            else status = "FUTURO";

            System.out.printf("%s [%s]\n", status, e.toString());
        }
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

    private static void listarUsuariosEvento(Scanner sc) {
        System.out.println("\n>>> Consultar usuários do evento");
        String nomeEvento = readLine(sc, "Digite o nome do evento: ");

        Evento evento = eventos.stream()
                .filter(e -> e.getNome().equalsIgnoreCase(nomeEvento))
                .findFirst()
                .orElse(null);

        if (evento == null) {
            System.out.println("Evento não encontrado.");
            return;
        }

        System.out.println("Lista de usuários do evento: " + evento.getNome());

        // Filtrar participações do evento
        List<Participacao> lista = new ArrayList<>();
        for (Participacao p : participacoes) {
            if (p.getEventoId().equals(evento.getId())) {
                lista.add(p);
            }
        }

        if (lista.isEmpty()) {
            System.out.println("(nenhuma participação encontrada)");
            return;
        }

        // Mostrar dados do usuário + id da participação
        for (Participacao p : lista) {
            Usuario participante = usuarios.stream()
                    .filter(u -> u.getId().equals(p.getUsuarioId()))
                    .findFirst()
                    .orElse(null);

            String usuarioInfo = (participante != null)
                    ? participante.getNome() + " (" + participante.getEmail() + ")"
                    : "Usuário não encontrado";

            System.out.printf("- Participação %s | %s | Confirmado em: %s%n",
                    p.getId(),
                    usuarioInfo,
                    p.getDataConfirmacao().format(DTF));
        }
    }

    private static void listarParticipacoes(Scanner sc) {
        System.out.println("\n>>> Consultar participações de usuário");
        String email = readLine(sc, "Digite o email do usuário: ");

        // Buscar usuário pelo e-mail
        Usuario usuario = usuarios.stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);

        if (usuario == null) {
            System.out.println("Usuário não encontrado.");
            return;
        }

        System.out.println("Participações de " + usuario.getNome() + " (" + usuario.getEmail() + "):");

        // Filtrar participações só desse usuário
        List<Participacao> minhas = new ArrayList<>();
        for (Participacao p : participacoes) {
            if (p.getUsuarioId().equals(usuario.getId())) {
                minhas.add(p);
            }
        }

        if (minhas.isEmpty()) {
            System.out.println("(nenhuma participação encontrada)");
            return;
        }

        // Resolver nomes dos eventos
        for (Participacao p : minhas) {
            Evento evento = eventos.stream()
                    .filter(e -> e.getId().equals(p.getEventoId()))
                    .findFirst()
                    .orElse(null);

            String eventoNome = (evento != null) ? evento.getNome() : "Evento não encontrado";
            System.out.printf("- %s | Evento: %s | Confirmado em: %s\n",
                    p.getId(),
                    eventoNome,
                    p.getDataConfirmacao());
        }
    }

    private static void confirmarParticipacao(Scanner sc) {
        if (usuarios.isEmpty() || eventos.isEmpty()) {
            System.out.println("⚠️ É necessário ter usuários e eventos cadastrados.");
            return;
        }

        listarUsuarios();
        String email = readLine(sc, "Digite o email do usuário: ");
        Optional<Usuario> usuarioOpt = usuarios.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();

        if (usuarioOpt.isEmpty()) {
            System.out.println("Usuário não encontrado.");
            return;
        }

        listarEventos();
        String eid = readLine(sc, "Digite o ID do evento: ");
        Optional<Evento> eventoOpt = eventos.stream()
                .filter(e -> e.getId().toString().equals(eid))
                .findFirst();

        if (eventoOpt.isEmpty()) {
            System.out.println("Evento não encontrado.");
            return;
        }

        Participacao p = new Participacao(usuarioOpt.get().getId(), eventoOpt.get().getId());
        participacoes.add(p);
        ParticipacaoRepository.save(participacoes);

        System.out.println("✅ Participação confirmada!");
    }

    private static void cancelarParticipacao(Scanner sc) {
        if (participacoes.isEmpty()) {
            System.out.println("⚠️ Nenhuma participação registrada.");
            return;
        }

        listarParticipacoes(sc);
        String pid = readLine(sc, "Digite o ID da participação para cancelar: ");

        Optional<Participacao> partOpt = participacoes.stream()
                .filter(p -> p.getId().toString().equals(pid))
                .findFirst();

        if (partOpt.isEmpty()) {
            System.out.println("Participação não encontrada.");
            return;
        }

        participacoes.remove(partOpt.get());
        ParticipacaoRepository.save(participacoes);

        System.out.println("❌ Participação cancelada.");
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

    private static CategoriaEvento escolherCategoria(Scanner sc) {
        System.out.println("Categorias:");
        CategoriaEvento[] values = CategoriaEvento.values();
        for (int i = 0; i < values.length; i++) {
            System.out.printf("  %d) %s\n", i + 1, values[i].name());
        }
        while (true) {
            String s = readLine(sc, "Escolha (1-" + values.length + "): ");
            try {
                int idx = Integer.parseInt(s);
                if (idx >= 1 && idx <= values.length) {
                    return values[idx - 1];
                }
            } catch (NumberFormatException ignored) {}
            System.out.println("Opção inválida.");
        }
    }

}