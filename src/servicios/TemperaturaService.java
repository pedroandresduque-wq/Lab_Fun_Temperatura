package servicios;

import entidades.CambioTemperatura;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class TemperaturaService {
    
    public record TemperaturaDatos(
        double valor,
        LocalDate fecha,
        String ciudad
    ) {}

    public record EstadisticasTemperatura(
        double promedio,
        double desviacionEstandar,
        double maximo,
        double minimo,
        double moda,
        double mediana
    ) {}

    public record TemperaturasExtremas(
        TemperaturaDatos maxima,
        TemperaturaDatos minima
    ) {}

    public static Optional<TemperaturasExtremas> obtenerTemperaturasExtremas(
            List<CambioTemperatura> datos,
            LocalDate desde,
            LocalDate hasta,
            String ciudad) {
        
        var datosFiltrados = datos.stream()
            .filter(ct -> ct.getCiudad().equals(ciudad))
            .filter(ct -> !ct.getFecha().isBefore(desde))
            .filter(ct -> !ct.getFecha().isAfter(hasta))
            .collect(Collectors.toList());

        if (datosFiltrados.isEmpty()) {
            return Optional.empty();
        }

        var maxima = datosFiltrados.stream()
            .max(Comparator.comparingDouble(CambioTemperatura::getCambio))
            .map(ct -> new TemperaturaDatos(ct.getCambio(), ct.getFecha(), ct.getCiudad()))
            .orElseThrow();

        var minima = datosFiltrados.stream()
            .min(Comparator.comparingDouble(CambioTemperatura::getCambio))
            .map(ct -> new TemperaturaDatos(ct.getCambio(), ct.getFecha(), ct.getCiudad()))
            .orElseThrow();

        return Optional.of(new TemperaturasExtremas(maxima, minima));
    }

    public static Optional<EstadisticasTemperatura> calcularEstadisticas(
            List<CambioTemperatura> datos,
            LocalDate desde,
            LocalDate hasta,
            String ciudad) {

        var cambios = datos.stream()
            .filter(ct -> ct.getCiudad().equals(ciudad))
            .filter(ct -> !ct.getFecha().isBefore(desde))
            .filter(ct -> !ct.getFecha().isAfter(hasta))
            .map(CambioTemperatura::getCambio)
            .collect(Collectors.toList());

        if (cambios.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new EstadisticasTemperatura(
            CambioTemperaturaServicio.getPromedio(cambios),
            CambioTemperaturaServicio.getDesviacionEstandar(cambios),
            CambioTemperaturaServicio.getMaximo(cambios),
            CambioTemperaturaServicio.getMinimo(cambios),
            CambioTemperaturaServicio.getModa(cambios),
            CambioTemperaturaServicio.getMediana(cambios)
        ));
    }

    public static Map<LocalDate, Double> obtenerDatosGrafica(
            List<CambioTemperatura> datos,
            LocalDate desde,
            LocalDate hasta,
            String ciudad) {
        
        return datos.stream()
            .filter(ct -> ct.getCiudad().equals(ciudad))
            .filter(ct -> !ct.getFecha().isBefore(desde))
            .filter(ct -> !ct.getFecha().isAfter(hasta))
            .collect(Collectors.toMap(
                CambioTemperatura::getFecha,
                CambioTemperatura::getCambio,
                (v1, v2) -> v1,
                TreeMap::new
            ));
    }

    public static List<CambioTemperatura> cargarDatos(String rutaArchivo) {
        return List.copyOf(CambioTemperaturaServicio.getDatos(rutaArchivo));
    }

    public static List<String> obtenerCiudades(List<CambioTemperatura> datos) {
        return datos.stream()
            .map(CambioTemperatura::getCiudad)
            .distinct()
            .sorted()
            .collect(Collectors.toUnmodifiableList());
    }
}