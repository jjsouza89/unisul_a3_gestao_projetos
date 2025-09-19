package com.unisul.eventos.model;

import com.unisul.eventos.security.PasswordUtil;
import com.unisul.eventos.util.Util;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Usuario {

    private final UUID id;

    private String nome;
    private String email;
    private String telefone;

    private final LocalDateTime dataCadastro;

    private String senhaHash;
    private String senhaSalt;

    /** Construtor padrão (útil para frameworks/serialização) */
    public Usuario() {
        this.id = UUID.randomUUID();
        this.dataCadastro = LocalDateTime.now();
    }

    /** Construtor essencial (novo usuário) */
    public Usuario(String nome, String email, String telefone) {
        this.id = UUID.randomUUID();
        this.dataCadastro = LocalDateTime.now();
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    /** Construtor privado (reidratação a partir de CSV) */
    private Usuario(UUID id, String nome, String email, String telefone,
                    LocalDateTime dataCadastro, String senhaHash, String senhaSalt) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.dataCadastro = dataCadastro;
        this.senhaHash = senhaHash;
        this.senhaSalt = senhaSalt;
    }

    /** Define/atualiza a senha do usuário (gera SALT e guarda apenas o HASH). */
    public void definirSenha(String senhaEmClaro) {
        String salt = PasswordUtil.gerarSalt();
        String hash = PasswordUtil.hashSenha(senhaEmClaro, salt);
        this.senhaSalt = salt;
        this.senhaHash = hash;
    }

    /** Valida uma senha informada contra o hash persistido. */
    public boolean validarSenha(String senhaEmClaro) {
        if (this.senhaHash == null || this.senhaSalt == null) return false;
        String hashTentativa = PasswordUtil.hashSenha(senhaEmClaro, this.senhaSalt);
        return Objects.equals(this.senhaHash, hashTentativa);
    }

    public UUID getId() { return id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public LocalDateTime getDataCadastro() { return dataCadastro; }

    // Sem getters públicos para senhaHash/salt (exposição perigosa).
    // Apenas package-private para persistência.
    String getSenhaHash() { return senhaHash; }
    String getSenhaSalt() { return senhaSalt; }

    /** Serializa o usuário em uma linha CSV */
    public String toCsv() {
        return String.join(";",
                id.toString(),
                Util.esc(nome),
                Util.esc(email),
                Util.esc(telefone),
                dataCadastro.toString(),
                Util.nvl(senhaHash, ""),
                Util.nvl(senhaSalt, "")
        );
    }

    /** Reconstrói usuário a partir de uma linha CSV */
    public static Usuario fromCsv(String line) {
        String[] p = line.split(";", -1); // mantém vazios
        if (p.length < 7) throw new IllegalArgumentException("Linha inválida para Usuario: " + line);

        return new Usuario(
                UUID.fromString(p[0]),
                Util.des(p[1]),
                Util.des(p[2]),
                Util.des(p[3]),
                LocalDateTime.parse(p[4]),
                Util.vazioParaNull(p[5]),
                Util.vazioParaNull(p[6])
        );
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", dataCadastro=" + dataCadastro +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return id.equals(usuario.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}