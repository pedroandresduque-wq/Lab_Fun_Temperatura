package servicios;

import entidades.CambioTemperatura;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CalculoTemperaturaServicio {

    private record TemperaturaExtrema(String ciudad, double valor) {}

    public static Map<String, Double> calcularTemperaturas(
            List<CambioTemperatura> cambiosTemperaturas,
            List<String> ciudades,
            LocalDate desde,
            LocalDate hasta) {

        var temperaturasPorCiudad = ciudades.stream()
            .map(ciudad -> new AbstractMap.SimpleEntry<>(
                ciudad,
                CambioTemperaturaServicio.filtrar(ciudad, desde, hasta, cambiosTemperaturas).stream()
                    .map(CambioTemperatura::getCambio)
                    .collect(Collectors.toList())
            ))
            .filter(entry -> !entry.getValue().isEmpty())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> new TemperaturaExtrema[]{
                    new TemperaturaExtrema(
                        entry.getKey(),
                        CambioTemperaturaServicio.getMaximo(entry.getValue())
                    ),
                    new TemperaturaExtrema(
                        entry.getKey(),
                        CambioTemperaturaServicio.getMinimo(entry.getValue())
                    )
                }
            ));

        if (temperaturasPorCiudad.isEmpty()) {
            return Collections.emptyMap();
        }

        var maxGlobal = temperaturasPorCiudad.values().stream()
            .map(extremos -> extremos[0])
            .max(Comparator.comparingDouble(TemperaturaExtrema::valor))
            .map(ext -> new AbstractMap.SimpleEntry<>(
                "Máxima Global (" + ext.ciudad() + ")",
                ext.valor()
            ))
            .orElseThrow();

        var minGlobal = temperaturasPorCiudad.values().stream()
            .map(extremos -> extremos[1])
            .min(Comparator.comparingDouble(TemperaturaExtrema::valor))
            .map(ext -> new AbstractMap.SimpleEntry<>(
                "Mínima Global (" + ext.ciudad() + ")",
                ext.valor()
            ))
            .orElseThrow();

        return Map.of(
            maxGlobal.getKey(), maxGlobal.getValue(),
            minGlobal.getKey(), minGlobal.getValue()
        );
    }
}