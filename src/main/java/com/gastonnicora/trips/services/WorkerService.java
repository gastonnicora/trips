package com.gastonnicora.trips.services;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.gastonnicora.trips.dtos.entities.WorkerDTO;
import com.gastonnicora.trips.dtos.response.worker.WorkersByCompany;
import com.gastonnicora.trips.dtos.response.worker.WorkersByUser;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.mappers.WorkerMapper;
import com.gastonnicora.trips.repositories.WorkerRepository;

import jakarta.transaction.Transactional;

/**
 * Servicio para gestionar la entidad {@link Worker}.
 * 
 * <p>
 * Este servicio maneja todas las operaciones relacionadas con la gestión de
 * trabajadores, como la creación, actualización, eliminación y obtención de
 * workers.
 * Ademas permite el cambio y asignación de roles a los trabajadores.
 * </p>
 * 
 * 
 * @author Gastón
 * @version 1.0
 * @since 2026-06-03
 */
@Service
public class WorkerService {
    private final WorkerRepository workerRepository;
    private final WorkerMapper WorkerMapper;

    /**
     * Constructor del servicio WorkerService.
     * 
     * @param workerRepository Repositorio de workers utilizado para acceder a la
     *                         base de datos.
     * @param WorkerMapper     Mapper para convertir entidades {@link Worker} a
     *                         {@link WorkerDTO}.
     */
    public WorkerService(WorkerRepository workerRepository, WorkerMapper WorkerMapper) {
        this.workerRepository = workerRepository;
        this.WorkerMapper = WorkerMapper;
    }

    /**
     * Crea un nuevo worker.
     * 
     * @param user    {@link User} del worker.
     * @param company {@link Company} de la empresa.
     * @param roles   Set de {@link RoleCompany} del worker.
     * @return {@link WorkerDTO} del worker creado.
     */
    public WorkerDTO createWorker(User user, Company company, Set<RoleCompany> roles) {
        return WorkerMapper.toDTO(workerRepository.save(new Worker(user, company, roles)));
    }

    /**
     * Obtiene un worker por su UUID.
     * 
     * @param user    UUID del usuario.
     * @param company UUID de la empresa.
     * @return {@link WorkerDTO} del worker.
     */
    public WorkerDTO getWorkerByUserAndCompany(UUID user, UUID company) {
        Worker worker = workerRepository.findByUserUuidAndCompanyUuid(user, company).orElse(null);
        return WorkerMapper.toDTO(worker);
    }

    /**
     * Obtiene todos los workers de una empresa.
     * 
     * @param company UUID de la empresa.
     * @return {@link WorkersByCompany} con los datos de la empresa y todos sus
     *         trabajadores.
     */
    public WorkersByCompany getWorkersByCompany(UUID company) {
        List<Worker> workers = workerRepository.findAllByCompanyUuid(company);
        return WorkerMapper.toWorkersByCompanyDTO(workers);
    }

    /**
     * Obtiene todos los workers de un usuario.
     * 
     * @param user UUID del usuario.
     * @return {@link WorkersByUser} con los datos del usuario y todos sus trabajos.
     */
    public WorkersByUser getWorkersByUser(UUID user) {
        List<Worker> workers = workerRepository.findAllByUserUuid(user);
        return WorkerMapper.toWorkersByUserDTO(workers);
    }

    /**
     * Elimina un worker.
     * 
     * @param user    UUID del usuario.
     * @param company UUID de la empresa.
     */
    @Transactional
    public void deleteWorker(UUID user, UUID company) {
        Worker worker = this.getWorker(user, company);
        if (worker != null) {
            worker.setActive(false);
            workerRepository.save(worker);
        }
    }

    /**
     * Actualiza los roles de un worker.
     * 
     * @param user    UUID del usuario.
     * @param company UUID de la empresa.
     * @param roles   Set de {@link RoleCompany} del worker.
     */
    @Transactional
    public void updateWorker(UUID user, UUID company, Set<RoleCompany> roles) {
        Worker worker = this.getWorker(user, company);
        if (worker != null) {
            worker.setRoles(roles);
            workerRepository.save(worker);
        }
    }

    /**
     * Obtiene un worker .
     * 
     * @param user    UUID del usuario.
     * @param company UUID de la empresa.
     * @return {@link Worker} del worker.
     */
    private Worker getWorker(UUID user, UUID company) {
        return workerRepository.findByUserUuidAndCompanyUuid(user, company).orElse(null);
    }

    /**
     * Obtiene todos los workers de un usuario con el rol owner.
     * 
     * @param owner UUID del usuario.
     * @return Lista de {@link Worker} del worker.
     */
    public List<Worker> getWorkersByOwner(UUID owner) {
        return workerRepository.findAllByUserUuidAndRolesContains(owner, RoleCompany.OWNER);
    }

}
