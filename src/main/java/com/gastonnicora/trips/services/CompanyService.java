package com.gastonnicora.trips.services;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.gastonnicora.trips.dtos.entities.BusDTO;
import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.entities.WorkerDTO;
import com.gastonnicora.trips.dtos.request.bus.BusCreate;
import com.gastonnicora.trips.dtos.request.company.CompanyCreate;
import com.gastonnicora.trips.dtos.response.ListResponse;
import com.gastonnicora.trips.dtos.response.company.AddressResponse;
import com.gastonnicora.trips.dtos.response.worker.WorkersByCompany;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.CompanyMapper;
import com.gastonnicora.trips.repositories.CompanyRepository;
import com.gastonnicora.trips.utils.SecurityUtils;
import static com.gastonnicora.trips.utils.SecurityUtils.getCurrentUserUuid;

import jakarta.transaction.Transactional;

/**
 * Servicio de gestión de empresas.
 * <p>
 * Este servicio maneja todas las operaciones relacionadas con la gestión de
 * empresas, como la creación, actualización, eliminación, y obtención de
 * empresas.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-20
 */
@Service
public class CompanyService {

    private final UserService userService;
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final GeocodingService geocodingService;
    private final WorkerService workerService;
    private final BusService busService;

    /**
     * Constructor que inicializa los servicios necesarios para la gestión de
     * empresas.
     *
     * @param userService Servicio de gestión de usuarios.
     * @param companyRepository Repositorio de empresas utilizado para acceder a
     * la base de datos.
     * @param companyMapper Mapper para convertir entidades {@link Company} a
     * DTOs {@link CompanyDTO}.
     * @param geocodingService Servicio para obtener direcciones a partir de
     * coordenadas.
     * @param workerService Servicio de gestión de trabajadores.
     */
    public CompanyService(UserService userService, CompanyRepository companyRepository, CompanyMapper companyMapper,
            GeocodingService geocodingService, WorkerService workerService, BusService busService) {
        this.userService = userService;
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.geocodingService = geocodingService;
        this.workerService = workerService;
        this.busService = busService;
    }

    /**
     * Crea una nueva empresa.
     * <p>
     * Este método realiza lo siguiente:
     * </p>
     * <ul>
     * <li>Obtiene el usuario actual mediante {@link UserService}.</li>
     * <li>Obtiene la dirección a partir de las coordenadas proporcionadas
     * mediante {@link GeocodingService}.</li>
     * <li>Crea una nueva instancia de {@link Company} con los datos
     * proporcionados
     * </li>
     * <li>Crea un nuevo trabajador con el rol de dueño.</li>
     * <li>Guarda la nueva empresa en la base de datos</li>
     * <li>Convierte la nueva empresa en {@link CompanyDTO} utilizando
     * {@link CompanyMapper}.</li>
     * </ul>
     *
     * @param companyCreate {@link CompanyCreate} con los datos de la nueva
     * empresa.
     * @return {@link CompanyDTO} Datos de la nueva empresa creada.
     * @throws BadRequestException Si la dirección no es válida.
     * @see UserService #getUser(java.util.UUID)
     * @see GeocodingService #obtenerDireccion(double, double)
     * @see WorkerService #createWorker(User, Company, Set)
     * @see CompanyMapper #toDTO(Company)
     * @see CompanyRepository #save(Company)
     */
    public CompanyDTO createCompany(CompanyCreate companyCreate) {
        User currentUser = userService.getUser(getCurrentUserUuid());
        AddressResponse addressR = geocodingService.obtenerDireccion(companyCreate.getLatitude(),
                companyCreate.getLongitude());
        if (addressR.displayName() == null) {
            throw new BadRequestException("Dirección inválida");
        }
        String address = addressR.displayName();

        Company company = new Company(companyCreate.getName(), address, companyCreate.getLatitude(),
                companyCreate.getLongitude(),
                companyCreate.getEmail(), companyCreate.getPhone());
        company = companyRepository.save(company);
        workerService.createWorker(currentUser, company, Set.of(RoleCompany.OWNER));
        return companyMapper.toDTO(company);
    }

    /**
     * Obtiene los detalles de una empresa por su UUID.
     * <p>
     * Este método realiza lo siguiente:
     * </p>
     * <ul>
     * <li>Busca la empresa en la base de datos mediante
     * {@link CompanyRepository}.</li>
     * <li>Si no se encuentra, lanza una excepción {@link BadRequestException}
     * con mensaje descriptivo.</li>
     * <li>Convierte la empresa en un DTO con sus datos utilizando
     * {@link CompanyMapper}.</li>
     * </ul>
     *
     * @param uuid UUID de la empresa que se quiere obtener.
     * @return {@link CompanyDTO} Datos de la empresa con el UUID especificado.
     * @throws BadRequestException Si la empresa no existe.
     * @see CompanyRepository #findByUuid(UUID)
     * @see CompanyMapper #toDTO(Company)
     * @see BadRequestException
     */
    public CompanyDTO getCompany(UUID uuid) {
        Company company = this.getCompanyEntity(uuid);
        return companyMapper.toDTO(company);
    }

    public Company getCompanyEntity(UUID uuid) {
        return companyRepository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Empresa no encontrada"));
    }

    /**
     * Obtiene todas las empresas del usuario.
     *
     * @param uuid UUID del usuario.
     * @return Lista de empresas del usuario.
     * @see CompanyRepository #findAllByOwner_Uuid(UUID)
     */
    public ListResponse<CompanyDTO> getCompaniesByUser(UUID uuid) {
        return getCompaniesByOwner(uuid);
    }

    /**
     * Obtiene todas las empresas del usuario actual.
     * <p>
     * Este método realiza lo siguiente:
     * </p>
     * <ul>
     * <li>Obtiene el UUID del usuario actual mediante
     * {@link SecurityUtils}.</li>
     * <li>Llama al método {@link #getCompaniesByOwner(UUID)} para obtener las
     * empresas del usuario.</li>
     * </ul>
     *
     * @return Lista de empresas del usuario actual.
     *
     * @see ListResponse
     * @see SecurityUtils #getCurrentUserUuid()
     * @see #getCompaniesByOwner(UUID)
     */
    public ListResponse<CompanyDTO> getCompaniesByCurrentUser() {
        return getCompaniesByOwner(getCurrentUserUuid());
    }

    /**
     * Actualiza los detalles de una empresa por su UUID.
     * <p>
     * Este método realiza lo siguiente:
     * </p>
     * <ul>
     * <li>Busca la empresa en la base de datos mediante
     * {@link CompanyRepository}.</li>
     * <li>Si no se encuentra, lanza una excepción {@link NotFoundException} con
     * mensaje descriptivo.</li>
     * <li>Si el usuario no es el dueño de la empresa, lanza una excepción
     * {@link BadRequestException} con mensaje descriptivo.</li>
     * <li>Convierte las coordenadas en una dirección a partir de
     * {@link GeocodingService}.</li>
     * <li>Si la dirección no es válida, lanza una excepción
     * {@link BadRequestException} con mensaje descriptivo.</li>
     * <li>Actualiza los campos de la empresa con los datos proporcionados.</li>
     * <li>Guarda la empresa actualizada en la base de datos.</li>
     * <li>Convierte la empresa en {@link CompanyDTO} utilizando
     * {@link CompanyMapper}.</li>
     * </ul>
     *
     * @param uuid UUID de la empresa que se quiere actualizar.
     * @param companyCreate {@link CompanyCreate} con los nuevos datos de la
     * empresa.
     * @return {@link CompanyDTO} Datos de la empresa actualizada.
     * @throws NotFoundException Si la empresa no existe.
     * @throws BadRequestException Si el usuario no es el dueño de la empresa.
     * @throws BadRequestException Si la dirección no es válida.
     * @see CompanyMapper #toDTO(Company)
     * @see CompanyRepository #findByUuid(UUID)
     */
    @Transactional
    public CompanyDTO updateCompany(UUID uuid, CompanyCreate companyCreate) {
        Company company = this.getCompanyEntity(uuid);
        AddressResponse addressR = geocodingService.obtenerDireccion(companyCreate.getLatitude(),
                companyCreate.getLongitude());
        if (addressR.displayName() == null) {
            throw new BadRequestException("Dirección inválida");
        }
        String address = addressR.displayName();
        company.setName(companyCreate.getName());
        company.setEmail(companyCreate.getEmail());
        company.setPhone(companyCreate.getPhone());
        company.setLatitude(companyCreate.getLatitude());
        company.setLongitude(companyCreate.getLongitude());
        company.setAddress(address);
        return companyMapper.toDTO(companyRepository.save(company));
    }

    /**
     * Elimina una empresa por su UUID.
     * <p>
     * Este método realiza lo siguiente:
     * </p>
     * <ul>
     * <li>Busca la empresa en la base de datos mediante su UUID</li>
     * <li>Si la empresa no existe, lanza una excepción
     * {@link NotFoundException} con mensaje descriptivo.</li>
     * <li>Corrobora que la empresa sea propiedad del usuario actual</li>
     * <li>Si no es el dueño lanza una excepción {@link BadRequestException} con
     * mensaje descriptivo.</li>
     * <li>Desactiva la empresa en la base de datos.</li>
     * </ul>
     *
     * @param uuid UUID de la empresa que se quiere eliminar.
     * @throws NotFoundException con mensaje descriptivo.
     * @throws BadRequestException con mensaje descriptivo.
     * @see CompanyRepository #findByUuid(UUID)
     */
    @Transactional
    public void deleteCompany(UUID uuid) {
        Company company = this.getCompanyEntity(uuid);
        company.setActive(false);
        companyRepository.save(company);
    }

    /**
     * Obtiene todas las empresas del usuario por su UUID.
     * <p>
     * Este método realiza lo siguiente:
     * </p>
     * <ul>
     * <li>Le pide a {@link WorkerService} todas las empresas en la que el
     * usuario es dueño.</li>
     * <li>Convierte las empresas en una lista de DTOs con sus datos utilizando
     * {@link CompanyMapper}.</li>
     * </ul>
     *
     * @return Lista de empresas del usuario.
     * @see CompanyRepository #findAllByOwner_Uuid(UUID)
     * @see CompanyMapper #toDTOList(List)
     * @see ListResponse
     * @see WorkerService #getWorkersByOwner(UUID)
     */
    private ListResponse<CompanyDTO> getCompaniesByOwner(UUID uuid) {
        List<Company> companies = workerService.getWorkersByOwner(uuid).stream()
                .map(worker -> worker.getCompany())
                .toList();
        return new ListResponse<CompanyDTO>(companyMapper.toDTOList(companies));
    }

    /**
     * Obtiene todos los trabajadores de una empresa.
     *
     * @param companyUuid UUID de la empresa.
     * @return {@link WorkersByCompany} con los datos de la empresa y todos sus
     * trabajadores.
     */
    public WorkersByCompany getWorkersByCompany(UUID companyUuid) {
        this.getCompany(companyUuid);
        return workerService.getWorkersByCompany(companyUuid);
    }

    /**
     * Obtiene la relación entre un usuario y una empresa.
     *
     * @param userUuid UUID del usuario.
     * @return {@link WorkerDTO} con los datos del usuario y la empresa.
     */
    public WorkerDTO getWorker(UUID userUuid, UUID companyUuid) {
        this.getCompany(companyUuid);
        userService.getUser(userUuid); 
        return workerService.getWorkerByUserAndCompany(userUuid, companyUuid);
    }

        /**
         * Crea un nuevo trabajador en una empresa.
         *
         * @param userUuid UUID del usuario que se quiere agregar como trabajador.
         * @param companyUuid UUID de la empresa a la que se quiere agregar el
         * trabajador.
         * @param roles Set de {@link RoleCompany} que se le asignarán al trabajador.
         * @return {@link WorkerDTO} Datos del trabajador creado.
         * @throws BadRequestException Si no se asigna ningún rol, o si se intenta
         * asignar el rol de OWNER.
         * @throws ConflictException Si el usuario ya es trabajador de la empresa.
         */
        public WorkerDTO createWorker(UUID userUuid, UUID companyUuid, Set<RoleCompany> roles) {
            Company company = this.getCompanyEntity(companyUuid);
            User user = userService.getUser(userUuid);

            if (workerService.getWorkersByCompany(companyUuid).getWorkers().stream()
                    .anyMatch(worker -> worker.getUser().getUuid().equals(userUuid))) {
                throw new ConflictException("El usuario ya es trabajador de la empresa");
            }

            if (roles == null || roles.isEmpty()) {
                throw new BadRequestException("Se debe asignar al menos un rol al trabajador");
            }

            if (roles.contains(RoleCompany.OWNER)) {
                throw new BadRequestException("No se puede asignar el rol de OWNER a un trabajador");
            }

            return workerService.createWorker(user, company, roles);
        }

    /**
     * Elimina un trabajador de una empresa.
     *
     * @param userUuid UUID del usuario que se quiere eliminar como trabajador.
     * @param companyUuid UUID de la empresa de la que se quiere eliminar el
     * trabajador.
     */
    public void deleteWorker(UUID userUuid, UUID companyUuid) {
        this.getCompanyEntity(companyUuid);
        userService.getUser(userUuid);
        workerService.deleteWorker(userUuid, companyUuid);
    }

    /**
     * Actualiza los roles de un trabajador en una empresa.
     *
     * @param userUuid UUID del usuario que se quiere actualizar como trabajador.
     * @param companyUuid UUID de la empresa en la que se quiere actualizar el
     * trabajador.
     * @param roles Set de {@link RoleCompany} que se le asignarán al trabajador.
     * @return {@link WorkerDTO} Datos del trabajador actualizado.
     * @throws BadRequestException Si no se asigna ningún rol, o si se intenta
     * asignar el rol de OWNER.
     */
    public WorkerDTO updateWorker(UUID userUuid, UUID companyUuid, Set<RoleCompany> roles) {
        this.getCompanyEntity(companyUuid);
        userService.getUser(userUuid);
        if (roles == null || roles.isEmpty()) {
            throw new BadRequestException("Se debe asignar al menos un rol al trabajador");
        }
        if (roles.contains(RoleCompany.OWNER)) {
            throw new BadRequestException("No se puede asignar el rol de OWNER a un trabajador");
        }

        return workerService.updateWorker(userUuid, companyUuid, roles);
    }

    /**
     * Crea un nuevo bus asociado a una empresa.
     *
     * @param companyUuid UUID de la empresa a la que se asociará el bus.
     * @param busCreate DTO que contiene los datos del bus a crear.
     * @return DTO del bus creado.
     * @throws ConflictException si ya existe un bus con la misma placa para la
     * empresa.
     */
    public BusDTO createBus(UUID companyUuid, BusCreate busCreate) {
        Company company = this.getCompanyEntity(companyUuid);
        return busService.createBus(company, busCreate);
    }
}
