package com.gastonnicora.trips.services;

import static com.gastonnicora.trips.utils.SecurityUtils.getCurrentUserUuid;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.request.company.CompanyCreate;
import com.gastonnicora.trips.dtos.response.company.AddressResponse;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.mappers.CompanyMapper;
import com.gastonnicora.trips.repositories.CompanyRepository;

/**
 * Servicio de gestión de empresas.
 * <p>
 * Este servicio maneja todas las operaciones relacionadas con la gestión de
 * empresas,
 * como la creación, actualización, eliminación, y obtención de empresas.
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

    /**
     * Constructor que inicializa los servicios necesarios para la gestión de
     * empresas.
     * 
     * @param userService       Servicio de gestión de usuarios.
     * @param companyRepository Repositorio de empresas utilizado para acceder a
     *                          la base de datos.
     * @param companyMapper     Mapper para convertir entidades {@link Company} a
     *                          DTOs {@link CompanyDTO}.
     * @param geocodingService  Servicio para obtener direcciones a partir de
     *                          coordenadas.
     */
    public CompanyService(UserService userService, CompanyRepository companyRepository, CompanyMapper companyMapper,
            GeocodingService geocodingService) {
        this.userService = userService;
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.geocodingService = geocodingService;
    }

    /**
     * Crea una nueva empresa.
     * <p>
     * Este método realiza lo siguiente:
     * </p>
     * <ul>
     * <li>Obtiene el usuario actual mediante {@link UserService}.</li>
     * <li>Obtiene la dirección a partir de las coordenadas proporcionadas mediante
     * {@link GeocodingService}.</li>
     * <li>Crea una nueva instancia de {@link Company} con los datos proporcionados
     * </li>
     * <li>Guarda la nueva empresa en la base de datos</li>
     * <li>Convierte la nueva empresa en {@link CompanyDTO} utilizando
     * {@link CompanyMapper}.</li>
     * </ul>
     * 
     * @param companyCreate {@link CompanyCreate} con los datos de la nueva
     *                      empresa.
     * @return {@link CompanyDTO} Datos de la nueva empresa creada.
     * @see UserService #getUser(java.util.UUID)
     * @see GeocodingService #obtenerDireccion(double, double)
     * @see CompanyMapper #toDTO(Company)
     * @see CompanyRepository #save(Company)
     * @throws BadRequestException Si la dirección no es válida.
     */
    public CompanyDTO createCompany(CompanyCreate companyCreate) {
        User currentUser = userService.getUser(getCurrentUserUuid());
        AddressResponse addressR = geocodingService.obtenerDireccion(companyCreate.getLatitude(),
                companyCreate.getLongitude());
        if (addressR.displayName() == null) {
            throw new BadRequestException("Dirección inválida");
        }
        String address = addressR.displayName();

        Company company = new Company(companyCreate.getName(), currentUser, address, companyCreate.getLatitude(),
                companyCreate.getLongitude(),
                companyCreate.getEmail(), companyCreate.getPhone());
        return companyMapper.toDTO(companyRepository.save(company));
    }

    /**
     * Obtiene los detalles de una empresa por su UUID.
     * <p>
     * Este método realiza lo siguiente:
     * </p>
     * <ul>
     * <li>Busca la empresa en la base de datos mediante {@link CompanyRepository}.</li>
     * <li>Si no se encuentra, lanza una excepción {@link BadRequestException} con
     * mensaje descriptivo.</li>
     * <li>Convierte la empresa en un DTO con sus datos utilizando {@link CompanyMapper}.</li>
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
        Company company = companyRepository.findByUuid(uuid).orElseThrow(() -> new BadRequestException("Empresa no encontrada"));
        return companyMapper.toDTO(company);
    }


    public List<CompanyDTO> getCompanies() {
        List<Company> companies = companyRepository.findAll();
        return companyMapper.toDTOList(companies);
    }

}
