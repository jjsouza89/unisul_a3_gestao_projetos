package com.unisul.a3gp.model;

import com.unisul.a3gp.util.Util;

import java.time.LocalDate;
import java.util.UUID;

public class Projeto {

    private final UUID id;

    private String nome;
    private String descricao;
    private LocalDate dataInicio;
    private LocalDate dataFimPrevista;
    private StatusProjeto status;
    private Usuario gerenteResponsavel;
    private Equipe equipe;

    // IDs temporários usados apenas durante a reidratação via CSV
    private UUID pendingGerenteId;
    private UUID pendingEquipeId;

    /** Construtor padrão (útil para frameworks/serialização) */
    public Projeto() {
        this.id = UUID.randomUUID();
    }

    /** Construtor essencial (novo projeto) */
    public Projeto(String nome, String descricao, LocalDate dataInicio,
                   LocalDate dataFimPrevista, StatusProjeto status,
                   Usuario gerenteResponsavel, Equipe equipe) {
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFimPrevista = dataFimPrevista;
        this.status = status;
        this.gerenteResponsavel = gerenteResponsavel;
        this.equipe = equipe;
    }

    /** Construtor privado (reidratação a partir de CSV) */
    private Projeto(UUID id, String nome, String descricao, LocalDate dataInicio, LocalDate dataFimPrevista,
                    StatusProjeto status, Usuario gerenteResponsavel, Equipe equipe) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFimPrevista = dataFimPrevista;
        this.status = status;
        this.gerenteResponsavel = gerenteResponsavel;
        this.equipe = equipe;
    }

    // Getters
    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFimPrevista() { return dataFimPrevista; }
    public StatusProjeto getStatus() { return status; }
    public Usuario getGerenteResponsavel() { return gerenteResponsavel; }
    public Equipe getEquipe() { return equipe; }

    // Getters dos IDs pendentes (usados pela Main para resolver vínculos)
    public UUID getPendingGerenteId() { return pendingGerenteId; }
    public UUID getPendingEquipeId() { return pendingEquipeId; }

    // Setters
    public void setNome(String nome) { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }
    public void setDataFimPrevista(LocalDate dataFimPrevista) { this.dataFimPrevista = dataFimPrevista; }
    public void setStatus(StatusProjeto status) { this.status = status; }
    public void setGerenteResponsavel(Usuario gerenteResponsavel) { this.gerenteResponsavel = gerenteResponsavel; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }

    /** Serializa o projeto em CSV */
    public String toCsv() {
        return String.join(";",
                id.toString(),
                Util.esc(nome),
                Util.esc(descricao),
                dataInicio != null ? dataInicio.toString() : "",
                dataFimPrevista != null ? dataFimPrevista.toString() : "",
                status != null ? status.name() : "",
                gerenteResponsavel != null ? gerenteResponsavel.getId().toString() : "",
                equipe != null ? equipe.getId().toString() : ""
        );
    }

    /** Reconstrói projeto a partir de CSV (gerente/equipe resolvidos depois) */
    public static Projeto fromCsv(String line) {
        String[] p = line.split(";", -1);
        if (p.length < 8) throw new IllegalArgumentException("Linha inválida para Projeto: " + line);

        Projeto projeto = new Projeto(
                UUID.fromString(p[0]),
                Util.des(p[1]),
                Util.des(p[2]),
                p[3].isEmpty() ? null : LocalDate.parse(p[3]),
                p[4].isEmpty() ? null : LocalDate.parse(p[4]),
                p[5].isEmpty() ? null : StatusProjeto.valueOf(p[5]),
                null, // gerente resolvido depois
                null  // equipe resolvida depois
        );

        if (!p[6].isEmpty()) {
            projeto.pendingGerenteId = UUID.fromString(p[6]);
        }
        if (!p[7].isEmpty()) {
            projeto.pendingEquipeId = UUID.fromString(p[7]);
        }

        return projeto;
    }

    @Override
    public String toString() {
        return "Projeto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", dataInicio=" + dataInicio +
                ", dataFimPrevista=" + dataFimPrevista +
                ", status=" + status +
                ", gerenteResponsavel=" + (gerenteResponsavel != null ? gerenteResponsavel.getNome() : "(sem gerente)") +
                ", equipe=" + (equipe != null ? equipe.getNome() : "(sem equipe)") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Projeto)) return false;
        Projeto projeto = (Projeto) o;
        return id.equals(projeto.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}