package com.monitor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;


@Entity
@Table(name = "medicoes")
public class Medicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Localização é obrigatória")
    @Column(nullable = false)
    private String localizacao;

    @Positive(message = "Consumo deve ser positivo")
    @Column(name = "consumo_litros", nullable = false)
    private Double consumoLitros;

    @PastOrPresent(message = "Data não pode ser futura")
    @Column(name = "data_medicao", nullable = false)
    private LocalDate dataMedicao;

    @NotBlank(message = "Campo alerta é obrigatório")
    @Column(nullable = false)
    private String alerta;

    // ✅ CONSTRUTOR PADRÃO (OBRIGATÓRIO)
    public Medicao() {
    }

    // ✅ CONSTRUTOR COMPLETO
    public Medicao(Long id, String localizacao, Double consumoLitros, LocalDate dataMedicao, String alerta) {
        this.id = id;
        this.localizacao = localizacao;
        this.consumoLitros = consumoLitros;
        this.dataMedicao = dataMedicao;
        this.alerta = alerta;
    }

    // ✅ GETTERS E SETTERS
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public Double getConsumoLitros() {
        return consumoLitros;
    }

    public void setConsumoLitros(Double consumoLitros) {
        this.consumoLitros = consumoLitros;
    }

    public LocalDate getDataMedicao() {
        return dataMedicao;
    }

    public void setDataMedicao(LocalDate dataMedicao) {
        this.dataMedicao = dataMedicao;
    }

    public String getAlerta() {
        return alerta;
    }

    public void setAlerta(String alerta) {
        this.alerta = alerta;
    }

    // ✅ TO STRING (OPCIONAL MAS ÚTIL)
    @Override
    public String toString() {
        return "Medicao{" +
                "id=" + id +
                ", localizacao='" + localizacao + '\'' +
                ", consumoLitros=" + consumoLitros +
                ", dataMedicao=" + dataMedicao +
                ", alerta='" + alerta + '\'' +
                '}';
    }
}