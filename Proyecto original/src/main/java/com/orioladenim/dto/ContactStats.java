package com.orioladenim.dto;

public class ContactStats {
    private long total;
    private long noLeidas;
    private long noRespondidas;
    private long hoy;
    private long estaSemana;
    private long esteMes;

    public ContactStats() {}

    public ContactStats(long total, long noLeidas, long noRespondidas, long hoy, long estaSemana, long esteMes) {
        this.total = total;
        this.noLeidas = noLeidas;
        this.noRespondidas = noRespondidas;
        this.hoy = hoy;
        this.estaSemana = estaSemana;
        this.esteMes = esteMes;
    }

    // Getters y Setters
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public long getNoLeidas() { return noLeidas; }
    public void setNoLeidas(long noLeidas) { this.noLeidas = noLeidas; }

    public long getNoRespondidas() { return noRespondidas; }
    public void setNoRespondidas(long noRespondidas) { this.noRespondidas = noRespondidas; }

    public long getHoy() { return hoy; }
    public void setHoy(long hoy) { this.hoy = hoy; }

    public long getEstaSemana() { return estaSemana; }
    public void setEstaSemana(long estaSemana) { this.estaSemana = estaSemana; }

    public long getEsteMes() { return esteMes; }
    public void setEsteMes(long esteMes) { this.esteMes = esteMes; }
}
