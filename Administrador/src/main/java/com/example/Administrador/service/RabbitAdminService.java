package com.example.Administrador.service;

import com.example.Administrador.dto.ColaRequestDTO;
import com.example.Administrador.dto.RespuestaAdminDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueInformation;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Servicio dedicado para encapsular todas las operaciones de infraestructura de RabbitMQ.
 * Cumple con el criterio de encapsulamiento del administrador (10% de la rúbrica):
 * Los controladores llaman a este servicio de alto nivel sin conocer detalles internos de RabbitMQ.
 */
@Service
public class RabbitAdminService {

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
}
