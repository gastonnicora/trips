package com.gastonnicora.trips.mappers;

import com.gastonnicora.trips.dtos.entities.WorkerDTO;
import com.gastonnicora.trips.dtos.response.worker.WorkerCompany;
import com.gastonnicora.trips.dtos.response.worker.WorkerUser;
import com.gastonnicora.trips.dtos.response.worker.WorkersByCompany;
import com.gastonnicora.trips.dtos.response.worker.WorkersByUser;
import com.gastonnicora.trips.entities.Worker;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Componente encargado de convertir entidades {@link Worker} en los diferentes
 * objetos DTO utilizados para representar la información de los trabajadores.
 *
 * <p>
 * Proporciona métodos para transformar un trabajador individual, una lista de
 * trabajadores y agrupaciones de trabajadores asociadas a usuarios o empresas.
 * </p>
 */
@Component
public class WorkerMapper {

    private final UserMapper userMapper;
    private final CompanyMapper companyMapper;

    public WorkerMapper(UserMapper userMapper, CompanyMapper companyMapper) {
        this.userMapper = userMapper;
        this.companyMapper = companyMapper;
    }

    /**
     * Convierte una entidad {@link Worker} en un {@link WorkerUser}.
     *
     * <p>
     * La conversión incluye los datos de identificación, usuario, roles y estado
     * del trabajador.
     * </p>
     *
     * @param worker Entidad de trabajador que se desea convertir.
     *
     * @return DTO de trabajador asociado a un usuario.
     */
    private WorkerUser toCompanyDTO(Worker worker) {
        return new WorkerUser(
                worker.getUuid(),
                userMapper.toDTO(worker.getUser()),
                worker.getRoles(),
                worker.isActive());
    }

    /**
     * Convierte una entidad {@link Worker} en un {@link WorkerDTO}.
     *
     * <p>
     * La conversión incluye los datos de identificación, usuario, empresa, roles,
     * estado y fechas de creación y actualización.
     * </p>
     *
     * @param worker Entidad de trabajador que se desea convertir.
     *
     * @return DTO de trabajador correspondiente a la entidad proporcionada.
     */
    public WorkerDTO toDTO(Worker worker) {
        return new com.gastonnicora.trips.dtos.entities.WorkerDTO(
                worker.getUuid(),
                userMapper.toDTO(worker.getUser()),
                companyMapper.toDTO(worker.getCompany()),
                worker.getRoles(),
                worker.isActive(),
                worker.getCreatedAt(),
                worker.getUpdatedAt());
    }

    /**
     * Convierte una lista de entidades {@link Worker} en una lista de
     * {@link WorkerDTO}.
     *
     * @param workers Lista de entidades de trabajadores que se desea convertir.
     *
     * @return Lista de DTOs de trabajadores correspondientes a las entidades proporcionadas.
     */
    public List<WorkerDTO> toDTOList(List<Worker> workers) {
        return workers.stream()
                .map(this::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Convierte una entidad {@link Worker} en un {@link WorkerCompany}.
     *
     * <p>
     * La conversión incluye los datos de identificación, empresa, roles y estado
     * del trabajador.
     * </p>
     *
     * @param worker Entidad de trabajador que se desea convertir.
     *
     * @return DTO de trabajador asociado a una empresa.
     */
    private WorkerCompany toUserDTO(Worker worker) {
        return new WorkerCompany(
                worker.getUuid(),
                companyMapper.toDTO(worker.getCompany()),
                worker.getRoles(),
                worker.isActive());
    }

    /**
     * Convierte una lista de entidades {@link Worker} en un
     * {@link WorkersByCompany}.
     *
     * <p>
     * El DTO resultante contiene la empresa asociada al primer trabajador de la
     * lista y los trabajadores correspondientes a dicha empresa.
     * </p>
     *
     * @param worker Lista de entidades de trabajadores.
     *
     * @return DTO que agrupa los trabajadores por empresa.
     */
    public WorkersByCompany toWorkersByCompanyDTO(List<Worker> worker) {
        return new WorkersByCompany(
                companyMapper.toDTO(worker.get(0).getCompany()),
                worker.stream()
                        .map(this::toCompanyDTO)
                        .toList());
    }

    /**
     * Convierte una lista de entidades {@link Worker} en un
     * {@link WorkersByUser}.
     *
     * <p>
     * El DTO resultante contiene el usuario asociado al primer trabajador de la
     * lista y los trabajadores correspondientes a dicho usuario.
     * </p>
     *
     * @param worker Lista de entidades de trabajadores.
     *
     * @return DTO que agrupa los trabajadores por usuario.
     */
    public WorkersByUser toWorkersByUserDTO(List<Worker> worker) {
        return new WorkersByUser(
                userMapper.toDTO(worker.get(0).getUser()),
                worker.stream()
                        .map(this::toUserDTO)
                        .toList());
    }

}