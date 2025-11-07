package entidades;

import java.time.LocalDate;

public final class CambioTemperatura {
    private final String ciudad;
    private final LocalDate fecha;
    private final double cambio;
    
    public CambioTemperatura(String ciudad, LocalDate fecha, double cambio) {
        this.ciudad = ciudad;
        this.fecha = fecha.plusDays(0); // defensive copy
        this.cambio = cambio;
    }

    public String getCiudad() {
        return ciudad;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public double getCambio() {
        return cambio;
    }

}
