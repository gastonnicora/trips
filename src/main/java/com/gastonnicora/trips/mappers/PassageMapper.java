package com.gastonnicora.trips.mappers;

import com.gastonnicora.trips.dtos.entities.PassageDTO;
import com.gastonnicora.trips.entities.Passage;
import com.gastonnicora.trips.entities.Trip;
import com.gastonnicora.trips.entities.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper que convierte entidades {@link Passage} en sus DTOs correspondientes.
 *
 * <p>
 * Delega la conversión del viaje y del usuario en {@link TripMapper} y
 * {@link UserMapper}, para mantener la misma representación que en el resto de la
 * API. Los datos del tramo (precio, coordenadas y auditoría) se copian directamente.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-10-23
 */
@Component
public class PassageMapper {

    private final UserMapper userMapper;
    private final TripMapper tripMapper;

    /**
     * Construye el mapper con los mappers de las entidades relacionadas.
     *
     * @param userMapper mapper de usuarios ({@link UserMapper}).
     * @param tripMapper mapper de viajes ({@link TripMapper}).
     */
    public PassageMapper(UserMapper userMapper, TripMapper tripMapper) {
        this.userMapper = userMapper;
        this.tripMapper = tripMapper;
    }

    /**
     * Convierte una entidad {@link Passage} en un {@link PassageDTO}.
     *
     * <p>
     * El viaje y el usuario se convierten con sus propios mappers. Acceder a estas
     * relaciones puede cargar datos adicionales de la base, porque son de carga
     * perezosa.
     * </p>
     *
     * @param passage entidad de pasaje que se desea convertir.
     *
     * @return DTO del pasaje con sus datos de viaje y usuario convertidos.
     *
     * @see TripMapper#toDTO(Trip)
     * @see UserMapper#toDTO(User)
     */
    public PassageDTO toDTO(Passage passage) {
        return new PassageDTO(
                passage.getUuid(),
                tripMapper.toDTO(passage.getTrip()),
                userMapper.toDTO(passage.getUser()),
                passage.getPrice(),
                passage.getFromLatitude(),
                passage.getFromLongitude(),
                passage.getToLatitude(),
                passage.getToLongitude(),
                passage.getCreatedAt(),
                passage.getUpdatedAt(),
                passage.isActive()
        );
    }

    /**
     * Convierte una lista de entidades {@link Passage} en una lista de
     * {@link PassageDTO}.
     *
     * @param passages lista de entidades de pasajes que se desea convertir.
     *
     * @return lista de DTOs de pasajes en el mismo orden que la entrada.
     */
    public List<PassageDTO> toDTOList(List<Passage> passages) {
        return passages.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
