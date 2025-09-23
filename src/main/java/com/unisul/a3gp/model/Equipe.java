package com.unisul.a3gp.model;

import com.unisul.a3gp.util.Util;

import java.util.*;
import java.util.stream.Collectors;

public class Equipe {

    private final UUID id;
    private String nome;
    private String descricao;
    private List<Usuario> membros = new ArrayList<>();

    // IDs temporários usados apenas durante a reidratação via CSV
    List<UUID> pendingMembrosIds = new ArrayList<>();

    /** Construtor padrão */
    public Equipe() {
        this.id = UUID.randomUUID();
    }

    /** Construtor essencial */
    public Equipe(String nome, String descricao) {
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.descricao = descricao;
    }

    /** Construtor privado (reidratação de CSV) */
    private Equipe(UUID id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public List<Usuario> getMembros() { return membros; }
    public void addMembro(Usuario usuario) { membros.add(usuario); }
    public void removeMembro(Usuario usuario) { membros.remove(usuario); }

    public List<UUID> getPendingMembrosIds() {
        return pendingMembrosIds;
    }
    public void clearPendingMembrosIds() {
        pendingMembrosIds.clear();
    }

    /** Serializa equipe em CSV (incluindo membros por ID) */
    public String toCsv() {
        String membrosIds = membros.stream()
                .map(u -> u.getId().toString())
                .collect(Collectors.joining(","));

        return String.join(";",
                id.toString(),
                Util.esc(nome),
                Util.esc(descricao),
                membrosIds
        );
    }

    /** Reconstrói equipe a partir de CSV (membros resolvidos depois) */
    public static Equipe fromCsv(String line) {
        String[] p = line.split(";", -1);
        if (p.length < 4) throw new IllegalArgumentException("Linha inválida para Equipe: " + line);

        Equipe e = new Equipe(
                UUID.fromString(p[0]),
                Util.des(p[1]),
                Util.des(p[2])
        );

        if (!p[3].isEmpty()) {
            e.pendingMembrosIds = Arrays.stream(p[3].split(","))
                    .map(UUID::fromString)
                    .collect(Collectors.toList());
        }

        return e;
    }

    @Override
    public String toString() {
        return "Equipe{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", membros=" + membros.size() +
                '}';
    }
}