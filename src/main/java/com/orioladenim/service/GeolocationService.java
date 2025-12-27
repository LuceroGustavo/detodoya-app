package com.orioladenim.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Service
public class GeolocationService {

    private final WebClient webClient;

    @Autowired
    public GeolocationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://ip-api.com")
                .build();
    }

    /**
     * Obtiene la ubicación geográfica basada en la IP
     */
    public String getLocationFromIP(String ipAddress) {
        try {
            // Log para debugging
            System.out.println("🔍 Geolocalización - IP recibida: " + ipAddress);
            
            // IPs locales o de desarrollo
            if (isLocalIP(ipAddress)) {
                System.out.println("📍 IP local detectada, usando fallback");
                // Para pruebas: puedes descomentar la siguiente línea para forzar la consulta
                // return getLocationFromPublicAPI(ipAddress);
                return "Prueba Local (IP: " + ipAddress + ")";
            }
            
            System.out.println("🌍 Consultando API de geolocalización para IP: " + ipAddress);

            // Llamada a la API de geolocalización
            Map<String, Object> response = webClient
                    .get()
                    .uri("/json/{ip}?fields=status,country,regionName,city", ipAddress)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(5))
                    .onErrorReturn(Map.of("status", "fail"))
                    .block();

            if (response != null && "success".equals(response.get("status"))) {
                String country = (String) response.get("country");
                String region = (String) response.get("regionName");
                String city = (String) response.get("city");
                
                return buildLocationString(country, region, city);
            }
        } catch (Exception e) {
            // Log del error si es necesario
            System.err.println("Error obteniendo geolocalización para IP: " + ipAddress + " - " + e.getMessage());
        }
        
        return "Ubicación no disponible";
    }

    /**
     * Verifica si la IP es local o de desarrollo
     */
    private boolean isLocalIP(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return true;
        }
        
        // IPv4 locales
        if (ipAddress.startsWith("127.") || 
            ipAddress.startsWith("192.168.") || 
            ipAddress.startsWith("10.") ||
            ipAddress.startsWith("172.16.") ||
            ipAddress.startsWith("172.17.") ||
            ipAddress.startsWith("172.18.") ||
            ipAddress.startsWith("172.19.") ||
            ipAddress.startsWith("172.20.") ||
            ipAddress.startsWith("172.21.") ||
            ipAddress.startsWith("172.22.") ||
            ipAddress.startsWith("172.23.") ||
            ipAddress.startsWith("172.24.") ||
            ipAddress.startsWith("172.25.") ||
            ipAddress.startsWith("172.26.") ||
            ipAddress.startsWith("172.27.") ||
            ipAddress.startsWith("172.28.") ||
            ipAddress.startsWith("172.29.") ||
            ipAddress.startsWith("172.30.") ||
            ipAddress.startsWith("172.31.")) {
            return true;
        }
        
        // IPv6 locales
        if (ipAddress.equals("0:0:0:0:0:0:0:1") ||
            ipAddress.equals("::1") ||
            ipAddress.startsWith("::ffff:127.") ||
            ipAddress.startsWith("::ffff:192.168.") ||
            ipAddress.startsWith("::ffff:10.")) {
            return true;
        }
        
        return false;
    }

    /**
     * Construye la cadena de ubicación
     */
    private String buildLocationString(String country, String region, String city) {
        StringBuilder location = new StringBuilder();
        
        if (city != null && !city.isEmpty()) {
            location.append(city);
        }
        
        if (region != null && !region.isEmpty() && !region.equals(city)) {
            if (location.length() > 0) {
                location.append(", ");
            }
            location.append(region);
        }
        
        if (country != null && !country.isEmpty()) {
            if (location.length() > 0) {
                location.append(", ");
            }
            location.append(country);
        }
        
        return location.length() > 0 ? location.toString() : "Ubicación no disponible";
    }
}
