package com.gastonnicora.trips.mappers;

import java.util.List;

import org.springframework.stereotype.Component;

import com.gastonnicora.trips.dtos.entities.WorkerDTO;
import com.gastonnicora.trips.dtos.response.worker.WorkerCompany;
import com.gastonnicora.trips.dtos.response.worker.WorkerUser;
import com.gastonnicora.trips.dtos.response.worker.WorkersByCompany;
import com.gastonnicora.trips.dtos.response.worker.WorkersByUser;
import com.gastonnicora.trips.entities.Worker;

/**
 * Mapper que convierte entidades {@link Worker} a {@link WorkerDTO}.
 * <p>
 * Se utiliza para exponer datos de trabajador de manera segura en la API, sin
 * incluir información sensible como la contraseña.
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
     * Convierte un {@link Worker} en {@link WorkerDTO}.
     *
     * @param worker Entidad de trabajador
     * @return DTO de trabajador correspondiente
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
     * Convierte una lista de {@link Worker} en una lista de {@link WorkerDTO}.
     *
     * @param workers Lista de entidades de trabajador
     * @return Lista de DTOs de trabajador correspondientes
     */
    public List<WorkerDTO> toDTOList(List<Worker> workers) {
        return workers.stream()
                .map(this::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Convierte un {@link Worker} en {@link WorkerUser}.
     *
     * @param worker Entidad de trabajador
     * @return DTO de trabajador correspondiente
     */
    private WorkerUser toCompanyDTO(Worker worker) {
        return new WorkerUser(
                worker.getUuid(),
                userMapper.toDTO(worker.getUser()),
                worker.getRoles(),
                worker.isActive());
    }

    /**
     * Convierte una lista de {@link Worker} en {@link WorkersByCompany}.
     *
     * @param worker Lista de entidades de trabajador
     * @return DTO de trabajador correspondiente
     */
    public WorkersByCompany toWorkersByCompanyDTO(List<Worker> worker) {
        return new WorkersByCompany(
                companyMapper.toDTO(worker.get(0).getCompany()),
                worker.stream()
                        .map(this::toCompanyDTO)
                        .toList());
    }

    /**
     * Convierte un {@link Worker} en {@link WorkerCompany}.
     *
     * @param worker Entidad de trabajador
     * @return DTO de trabajador correspondiente
     */
    private WorkerCompany toUserDTO(Worker worker) {
        return new WorkerCompany(
                worker.getUuid(),
                companyMapper.toDTO(worker.getCompany()),
                worker.getRoles(),
                worker.isActive());
    }

    /**
     * Convierte una lista de {@link Worker} en {@link WorkersByUser}.
     *
     * @param worker Lista de entidades de trabajador
     * @return DTO de trabajador correspondiente
     */
    public WorkersByUser toWorkersByUserDTO(List<Worker> worker) {
        return new WorkersByUser(
                userMapper.toDTO(worker.get(0).getUser()),
                worker.stream()
                        .map(this::toUserDTO)
                        .toList());
    }

}
