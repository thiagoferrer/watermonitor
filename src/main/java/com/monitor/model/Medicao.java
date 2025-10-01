package com.monitor.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;


@Entity(name = "MEDICAO")
public class Medicao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Localização é obrigatória")
    private String localizacao;

    @Positive(message = "Consumo deve ser um valor positivo")
    private Double consumoLitros;

    @NotNull(message = "Data da medição é obrigatória")
    private LocalDate dataMedicao;

    @NotBlank(message = "Campo alerta é obrigatório")
    private String alerta;

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
}