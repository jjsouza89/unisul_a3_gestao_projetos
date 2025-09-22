package com.unisul.a3gp.model;

import com.unisul.a3gp.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Equipe {

    private final UUID id;
    private String nome;
    private String descricao;
    private List<Usuario> membros = new ArrayList<>();

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

    /** Serializa equipe em CSV (sem os membros, que podem ser salvos em arquivo separado ou por IDs) */
    public String toCsv() {
        return String.join(";",
                id.toString(),
                Util.esc(nome),
                Util.esc(descricao)
        );
    }

    /** Reconstrói equipe a partir de CSV */
    public static Equipe fromCsv(String line) {
        String[] p = line.split(";", -1);
        if (p.length < 3) throw new IllegalArgumentException("Linha inválida para Equipe: " + line);

        return new Equipe(
                UUID.fromString(p[0]),
                Util.des(p[1]),
                Util.des(p[2])
        );
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