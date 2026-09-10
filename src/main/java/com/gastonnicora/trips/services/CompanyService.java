package com.gastonnicora.trips.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.gastonnicora.trips.dtos.entities.CompanyDTO;
import com.gastonnicora.trips.dtos.request.company.CompanyCreate;
import com.gastonnicora.trips.dtos.response.ListResponse;
import com.gastonnicora.trips.dtos.response.company.AddressResponse;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.CompanyMapper;
import com.gastonnicora.trips.repositories.CompanyRepository;

import jakarta.transaction.Transactional;

/**
 * Servicio encargado de gestionar las operaciones relacionadas con empresas.
 *
 * <p>
 * Proporciona operaciones para crear, consultar, actualizar y desactivar
 * empresas, además de transformar entidades de empresa en respuestas de tipo
 * {@link CompanyDTO}.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-05-20
 */
@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final GeocodingService geocodingService;

    /**
     * Crea una instancia del servicio encargado de gestionar empresas.
     *
     * @param companyRepository Repositorio utilizado para persistir y consultar
     *        empresas.
     * @param companyMapper Mapper utilizado para convertir entidades de empresa
     *        en DTOs.
     * @param geocodingService Servicio utilizado para obtener direcciones a
     *        partir de coordenadas geográficas.
     */
    public CompanyService(CompanyRepository companyRepository, CompanyMapper companyMapper,
            GeocodingService geocodingService) {
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.geocodingService = geocodingService;
    }

    /**
     * Crea una nueva empresa a partir de los datos proporcionados.
     *
     * <p>
     * Obtiene la dirección correspondiente a las coordenadas geográficas
     * proporcionadas y, si la dirección es válida, persiste la nueva empresa.
     * </p>
     *
     * @param companyCreate Datos necesarios para crear la empresa.
     * @return {@link CompanyDTO} correspondiente a la empresa creada.
     * @throws BadRequestException Si no se puede obtener una dirección válida.
     * @see GeocodingService#obtenerDireccion(double, double)
     * @see CompanyRepository#save(Company)
     * @see CompanyMapper#toDTO(Company)
     */
    public CompanyDTO createCompany(CompanyCreate companyCreate) {
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
        return companyMapper.toDTO(company);
    }

    /**
     * Obtiene una empresa mediante su identificador único.
     *
     * @param uuid Identificador único de la empresa.
     * @return {@link CompanyDTO} correspondiente a la empresa encontrada.
     * @throws NotFoundException Si no existe una empresa con el identificador
     *         indicado.
     * @see CompanyRepository#findByUuid(UUID)
     * @see CompanyMapper#toDTO(Company)
     */
    public CompanyDTO getCompany(UUID uuid) {
        Company company = this.getCompanyEntity(uuid);
        return companyMapper.toDTO(company);
    }

    /**
     * Obtiene la entidad de una empresa mediante su identificador único.
     *
     * <p>
     * Este método se utiliza internamente por otros servicios que necesitan
     * trabajar directamente con la entidad {@link Company}.
     * </p>
     *
     * @param uuid Identificador único de la empresa.
     * @return Entidad de la empresa encontrada.
     * @throws NotFoundException Si no existe una empresa con el identificador
     *         indicado.
     */
    public Company getCompanyEntity(UUID uuid) {
        return companyRepository.findByUuidAndActiveTrue(uuid)
                .orElseThrow(() -> new NotFoundException("Empresa no encontrada"));
    }

    /**
     * Convierte una lista de entidades de empresa en una respuesta de tipo
     * {@link ListResponse}.
     *
     * @param companies Lista de empresas que se desea convertir.
     * @return {@link ListResponse} que contiene los DTOs de las empresas.
     */
    public ListResponse<CompanyDTO> toListResponse(List<Company> companies) {
        return new ListResponse<>(companyMapper.toDTOList(companies));
    }

    /**
     * Actualiza los datos de una empresa.
     *
     * <p>
     * Obtiene la dirección correspondiente a las nuevas coordenadas
     * geográficas y persiste los cambios realizados sobre la empresa.
     * </p>
     *
     * @param uuid Identificador único de la empresa que se desea actualizar.
     * @param companyCreate Nuevos datos de la empresa.
     * @return {@link CompanyDTO} correspondiente a la empresa actualizada.
     * @throws NotFoundException Si no existe una empresa con el identificador
     *         indicado.
     * @throws BadRequestException Si no se puede obtener una dirección válida.
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
     * Desactiva una empresa mediante su identificador único.
     *
     * <p>
     * La empresa no se elimina físicamente de la base de datos, sino que se
     * marca como inactiva.
     * </p>
     *
     * @param uuid Identificador único de la empresa que se desea desactivar.
     * @throws NotFoundException Si no existe una empresa con el identificador
     *         indicado.
     */
    @Transactional
    public void deleteCompany(UUID uuid) {
        Company company = this.getCompanyEntity(uuid);
        company.setActive(false);
        companyRepository.save(company);
    }

}