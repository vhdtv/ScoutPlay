package com.scoutplay.ScoutPlay.enums;

public enum TipoContaEnum {
    OLHEIRO(1, "OLHEIRO"),
    ATLETA(2, "ATLETA"),
    RESPONSAVEL(3, "RESPONSAVEL"),
    REPRESENTANTE_CLUBE(4, "REPRESENTANTE_CLUBE");

    private final int id;
    private final String nome;

    TipoContaEnum(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
}