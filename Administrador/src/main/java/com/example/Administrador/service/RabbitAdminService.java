package com.example.Administrador.service;

import com.example.Administrador.dto.ColaRequestDTO;
import com.example.Administrador.dto.RespuestaAdminDTO;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.net.URI;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


@Service

public class RabbitAdminService {
    @Value("${spring.rabbitmq.host:rabbitmq}")
    private String rabbitmqHost;

    private static final Logger logger = LoggerFactory.getLogger(RabbitAdminService.class);
    private final RabbitAdmin rabbitAdmin;
    

    public RabbitAdminService(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    /**
     * Crea una nueva cola en RabbitMQ con las especificaciones del DTO.
     */
    public RespuestaAdminDTO crearCola(ColaRequestDTO dto) {
        logger.info("Solicitud para crear cola: {} (Durable: {}, Exclusiva: {}, AutoDelete: {})",
                dto.getNombre(), dto.isDurable(), dto.isExclusiva(), dto.isAutoDelete());

        Queue nuevaCola = new Queue(
                dto.getNombre(),
                dto.isDurable(),
                dto.isExclusiva(),
                dto.isAutoDelete()
        );

        String nombreDeclarado = rabbitAdmin.declareQueue(nuevaCola);

        if (nombreDeclarado != null) {
            logger.info("Cola '{}' declarada exitosamente en el broker RabbitMQ", dto.getNombre());
            return new RespuestaAdminDTO(true, "Cola creada exitosamente en RabbitMQ", dto.getNombre());
        } else {
            logger.error("No se pudo declarar la cola '{}' en el broker RabbitMQ", dto.getNombre());
            return new RespuestaAdminDTO(false, "No fue posible registrar la cola en el broker", dto.getNombre());
        }
    }

    /**
     * Elimina una cola existente en RabbitMQ por su nombre.
     */
    public RespuestaAdminDTO eliminarCola(String nombreCola) {
        logger.info("Solicitud para eliminar la cola: {}", nombreCola);

        boolean eliminada = rabbitAdmin.deleteQueue(nombreCola);

        if (eliminada) {
            logger.info("Cola '{}' eliminada con éxito del broker RabbitMQ", nombreCola);
            return new RespuestaAdminDTO(true, "Cola eliminada exitosamente", nombreCola);
        } else {
            logger.warn("La cola '{}' no existía o no pudo ser eliminada", nombreCola);
            return new RespuestaAdminDTO(false, "La cola no existe o no pudo ser eliminada", nombreCola);
        }
    }

    /**
     * Consulta las propiedades y estado de una cola en RabbitMQ.
     */
    public Map<String, Object> consultarCola(String nombreCola) {
        logger.info("Consultando información de la cola: {}", nombreCola);

        Properties propiedades = rabbitAdmin.getQueueProperties(nombreCola);
        Map<String, Object> resultado = new HashMap<>();

        if (propiedades != null) {
            resultado.put("existe", true);
            resultado.put("nombre", nombreCola);
            resultado.put("mensajesListos", propiedades.get(RabbitAdmin.QUEUE_MESSAGE_COUNT));
            resultado.put("consumidoresActivos", propiedades.get(RabbitAdmin.QUEUE_CONSUMER_COUNT));
        } else {
            resultado.put("existe", false);
            resultado.put("nombre", nombreCola);
            resultado.put("mensaje", "La cola no se encuentra en el broker");
        }

        return resultado;
    }
    public List<Map<String, Object>> listarTodasLasColas() {
        logger.info("Solicitando el listado de todas las colas al broker RabbitMQ");
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor("guest", "guest"));
            
            // Usamos la variable inyectada para formar la URL dinámica
            String url = "http://" + rabbitmqHost + ":15672/api/queues";
            
            List<Map<String, Object>> colas = restTemplate.getForObject(url, List.class);
            logger.info("Se encontraron {} colas en el broker", colas != null ? colas.size() : 0);
            return colas;
            
        } catch (Exception e) {
            logger.error("Error al obtener el listado de colas desde la API HTTP: {}", e.getMessage());
            return Collections.emptyList();
        }
    }





    public List<Map<String, Object>> leerMensajesDeCola(String nombreCola, int cantidadMensajes) {
        logger.info("Leyendo {} mensajes de la cola {} sin consumirlos", cantidadMensajes, nombreCola);
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor("guest", "guest"));

            String urlString = "http://" + rabbitmqHost + ":15672/api/queues/%2F/" + nombreCola + "/get";
            URI uri = URI.create(urlString); // Convertimos el String a URI para que Spring no lo re-codifique

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("count", cantidadMensajes);
            requestBody.put("ackmode", "ack_requeue_true"); 
            requestBody.put("encoding", "auto");
            requestBody.put("truncate", 50000);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity <Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<List> response = restTemplate.exchange(uri, HttpMethod.POST, requestEntity, List.class);


            if (response.getBody() != null) {
                List<Map<String, Object>> mensajesFormateados = new ArrayList<>();
                
                for (Object item : response.getBody()) {
                    Map<String, Object> mensajeRaw = (Map<String, Object>) item;
                    Map<String, Object> mensajeLimpio = new HashMap<>();
                    
                    // Extraemos las rutas
                    mensajeLimpio.put("routing_key", mensajeRaw.get("routing_key"));
                    mensajeLimpio.put("exchange", mensajeRaw.get("exchange"));
                    
                    // Extraemos el payload tal cual como viene (normalmente un String JSON)
                    Object payload = mensajeRaw.get("payload");
                    mensajeLimpio.put("contenido", payload);
                    
                    mensajesFormateados.add(mensajeLimpio);
                }
                return mensajesFormateados;
            }
            return Collections.emptyList();

        } catch (Exception e) {
            logger.error("Error al leer mensajes de la cola {}: {}", nombreCola, e.getMessage());
            return Collections.emptyList();
        }
    }
    

}



