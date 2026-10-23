package com.gastonnicora.trips.services;

import com.gastonnicora.trips.dtos.entities.WorkerDTO;
import com.gastonnicora.trips.dtos.response.worker.WorkersByCompany;
import com.gastonnicora.trips.dtos.response.worker.WorkersByUser;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.entities.Worker;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.exceptions.ForbiddenException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.WorkerMapper;
import com.gastonnicora.trips.repositories.WorkerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Servicio encargado de gestionar las relaciones entre usuarios y empresas
 * mediante la entidad {@link Worker}.
 *
 * <p>
 * Permite crear, consultar, actualizar y desactivar trabajadores, así como
 * asignar y modificar sus roles dentro de una empresa.
 * </p>
 *
 * @author Gastón Nicora
 * @version 1.0
 * @since 2026-06-03
 */
@Service
public class WorkerService {

    private final WorkerMapper WorkerMapper;
    private final WorkerRepository workerRepository;

    /**
     * Crea una instancia del servicio de trabajadores.
     *
     * @param workerRepository Repositorio de trabajadores utilizado para acceder
     *                         a la base de datos ({@link WorkerRepository}).
     * @param workerMapper     Mapper utilizado para convertir entidades
     *                         {@link Worker} en {@link WorkerDTO} ({@link WorkerMapper}).
     */
    public WorkerService(WorkerRepository workerRepository, WorkerMapper workerMapper) {
        this.workerRepository = workerRepository;
        this.WorkerMapper = workerMapper;
    }

    /**
     * Crea un nuevo trabajador asociado a un usuario y una empresa.
     *
     * <p>
     * Antes de crear la relación, valida los roles proporcionados y verifica que
     * no exista un trabajador activo asociado al mismo usuario y empresa.
     * </p>
     *
     * @param user    Usuario({@link User}) que se asociará como trabajador.
     * @param company Empresa({@link Company}) a la que se asociará el trabajador.
     * @param roles   Conjunto de {@link RoleCompany} que tendrá el trabajador.
     *
     * @return {@link WorkerDTO} correspondiente al trabajador creado.
     *
     * @throws ConflictException Si ya existe un trabajador activo asociado al
     *                           usuario y la empresa indicados.
     * @see WorkerService#verifyRole(Set)
     * @see WorkerRepository#findByUserUuidAndCompanyUuidAndActiveTrueAndUserEnabledTrueAndCompanyActiveTrue(UUID, UUID)
     * @see WorkerMapper#toDTO(Worker)
     */
    public WorkerDTO createWorker(User user, Company company, Set<RoleCompany> roles) {
        this.verifyRole(roles);
        if (workerRepository
                .findByUserUuidAndCompanyUuidAndActiveTrueAndUserEnabledTrueAndCompanyActiveTrue(
                        user.getUuid(),
                        company.getUuid()
                                                                                                )
                .isPresent()) {
            throw new ConflictException("Ya existe el trabajador");
        }
        return WorkerMapper.toDTO(workerRepository.save(new Worker(user, company, roles)));
    }

    /**
     * Crea un nuevo trabajador con el rol {@link RoleCompany#OWNER}.
     *
     * <p>
     * La creación solo se realiza si la empresa no posee trabajadores activos.
     * </p>
     *
     * @param user    Usuario({@link User}) que se asociará como propietario.
     * @param company Empresa({@link Company}) a la que se asociará el trabajador.
     *
     * @return {@link WorkerDTO} correspondiente al trabajador creado.
     *
     * @throws ConflictException Si la empresa ya posee trabajadores activos.
     * @see WorkerRepository#findAllByCompanyUuidAndCompanyActiveTrueAndUserEnabledTrueAndActiveTrue(UUID)
     * @see WorkerMapper#toDTO(Worker)
     */
    public WorkerDTO createWorkerOwner(User user, Company company) {
        if (!workerRepository.findAllByCompanyUuidAndCompanyActiveTrueAndUserEnabledTrueAndActiveTrue(company.getUuid())
                .isEmpty()) {
            throw new ConflictException("Ya existe el trabajador");
        }
        return WorkerMapper.toDTO(workerRepository.save(new Worker(user, company, Set.of(RoleCompany.OWNER))));
    }

    /**
     * Desactiva la relación entre un usuario y una empresa.
     *
     * @param user    UUID del usuario asociado al trabajador.
     * @param company UUID de la empresa asociada al trabajador.
     *
     * @see WorkerService#getWorker(UUID, UUID)
     * @see WorkerService#verifyNotOwner(Worker)
     */
    public void deleteWorker(UUID user, UUID company) {
        Worker worker = this.getWorker(user, company);
        this.verifyNotOwner(worker);
        worker.setActive(false);
        workerRepository.save(worker);
    }

    /**
     * Obtiene las empresas en las que un usuario posee el rol
     * {@link RoleCompany#OWNER}.
     *
     * <p>
     * Busca las relaciones activas del usuario que contienen el rol de
     * propietario y devuelve las empresas asociadas.
     * </p>
     *
     * @param owner UUID del usuario propietario.
     *
     * @return Lista de {@link Company} asociadas al usuario como propietario.
     *
     * @throws NotFoundException Si el usuario no posee empresas como propietario.
     * @see WorkerRepository#findAllByUserUuidAndUserEnabledTrueAndRolesContainsAndActiveTrue(UUID, RoleCompany)
     */
    public List<Company> getCompaniesByOwner(UUID owner) {
        List<Worker> workers = workerRepository.findAllByUserUuidAndUserEnabledTrueAndRolesContainsAndActiveTrue(
                owner,
                RoleCompany.OWNER
                                                                                                                );
        if (workers.isEmpty()) {
            throw new NotFoundException("Empresas no encontradas");
        }
        return workers.stream()
                .map(Worker::getCompany)
                .toList();
    }

    /**
     * Obtiene un trabajador activo mediante el UUID del usuario y de la empresa.
     *
     * @param user    UUID del usuario asociado al trabajador.
     * @param company UUID de la empresa asociada al trabajador.
     *
     * @return {@link Worker} correspondiente a la relación encontrada.
     *
     * @throws NotFoundException Si no existe una relación activa entre el
     *                           usuario y la empresa indicados.
     * @see WorkerRepository#findByUserUuidAndCompanyUuidAndActiveTrueAndUserEnabledTrueAndCompanyActiveTrue(UUID, UUID)
     */
    private Worker getWorker(UUID user, UUID company) {
        return workerRepository
                .findByUserUuidAndCompanyUuidAndActiveTrueAndUserEnabledTrueAndCompanyActiveTrue(user, company)
                .orElseThrow(() -> new NotFoundException("Relación entre usuario y empresa no encontrada"));
    }

    /**
     * Obtiene un trabajador mediante el UUID del usuario y de la empresa.
     *
     * @param user    UUID del usuario asociado al trabajador.
     * @param company UUID de la empresa asociada al trabajador.
     *
     * @return {@link WorkerDTO} correspondiente al trabajador encontrado.
     *
     * @see WorkerService#getWorker(UUID, UUID)
     * @see WorkerMapper#toDTO(Worker)
     */
    public WorkerDTO getWorkerByUserAndCompany(UUID user, UUID company) {
        Worker worker = this.getWorker(user, company);
        return WorkerMapper.toDTO(worker);
    }

    /**
     * Obtiene todos los trabajadores activos de una empresa.
     *
     * @param company UUID de la empresa cuyos trabajadores se desean obtener.
     *
     * @return {@link WorkersByCompany} con los datos de la empresa y sus
     * trabajadores activos.
     *
     * @see WorkerRepository#findAllByCompanyUuidAndCompanyActiveTrueAndUserEnabledTrueAndActiveTrue(UUID)
     * @see WorkerMapper#toWorkersByCompanyDTO(List)
     */
    public WorkersByCompany getWorkersByCompany(UUID company) {
        List<Worker> workers = workerRepository.findAllByCompanyUuidAndCompanyActiveTrueAndUserEnabledTrueAndActiveTrue(
                company);
        return WorkerMapper.toWorkersByCompanyDTO(workers);
    }

    /**
     * Obtiene todos los trabajadores activos asociados a un usuario.
     *
     * <p>
     * Si el usuario no posee trabajadores activos, se lanza una excepción.
     * </p>
     *
     * @param user UUID del usuario cuyos trabajadores se desean obtener.
     *
     * @return {@link WorkersByUser} con los datos del usuario y sus trabajadores
     * activos.
     *
     * @throws NotFoundException Si el usuario no posee trabajadores activos.
     * @see WorkerRepository#findAllByUserUuidAndUserEnabledTrueAndCompanyActiveTrueAndActiveTrue(UUID)
     * @see WorkerMapper#toWorkersByUserDTO(List)
     */
    public WorkersByUser getWorkersByUser(UUID user) {
        List<Worker> workers = workerRepository.findAllByUserUuidAndUserEnabledTrueAndCompanyActiveTrueAndActiveTrue(
                user);
        if (workers == null || workers.isEmpty()) {
            throw new NotFoundException("El usuario no tiene trabajos");
        }
        return WorkerMapper.toWorkersByUserDTO(workers);
    }

    /**
     * Actualiza los roles de un trabajador.
     *
     * <p>
     * Antes de actualizar la relación, valida los roles proporcionados y verifica
     * que el trabajador exista y se encuentre activo.
     * </p>
     *
     * @param user    UUID del usuario asociado al trabajador.
     * @param company UUID de la empresa asociada al trabajador.
     * @param roles   Conjunto de {@link RoleCompany} que tendrá el trabajador.
     *
     * @return {@link WorkerDTO} correspondiente al trabajador actualizado.
     *
     * @see WorkerService#verifyRole(Set)
     * @see WorkerService#verifyNotOwner(Worker)
     * @see WorkerService#getWorker(UUID, UUID)
     * @see WorkerMapper#toDTO(Worker)
     */
    public WorkerDTO updateWorker(UUID user, UUID company, Set<RoleCompany> roles) {
        this.verifyRole(roles);
        Worker worker = this.getWorker(user, company);
        this.verifyNotOwner(worker);
        worker.setRoles(roles);
        worker = workerRepository.save(worker);
        return WorkerMapper.toDTO(worker);
    }

    /**
     * Verifica que el trabajador no sea el propietario de la empresa. El rol
     * OWNER solo se asigna al crear la empresa y no puede otorgarse después.
     *
     * @param worker ({@link Worker}) relación trabajador/empresa.
     *
     * @throws ForbiddenException Si el trabajador posee el rol OWNER.
     */
    private void verifyNotOwner(Worker worker) {
        if (worker.hasRole(RoleCompany.OWNER)) {
            throw new ForbiddenException("No se puede modificar ni eliminar al propietario de la empresa");
        }
    }

    /**
     * Valida el conjunto de roles asignado a un trabajador.
     *
     * <p>
     * Verifica que se haya proporcionado al menos un rol y que no se intente
     * asignar el rol {@link RoleCompany#OWNER} mediante este método.
     * </p>
     *
     * @param roles Conjunto de {@link RoleCompany} que se desea validar.
     *
     * @throws BadRequestException Si los roles son nulos, están vacíos o
     *                             contienen el rol {@link RoleCompany#OWNER}.
     *
     */
    private void verifyRole(Set<RoleCompany> roles) {
        if (roles == null || roles.isEmpty()) {
            throw new BadRequestException("Se debe asignar al menos un rol al trabajador");
        }

        if (roles.contains(RoleCompany.OWNER)) {
            throw new BadRequestException("No se puede asignar el rol de OWNER a un trabajador");
        }
    }

}