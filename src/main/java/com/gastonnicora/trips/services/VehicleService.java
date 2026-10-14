package com.gastonnicora.trips.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.gastonnicora.trips.dtos.entities.VehicleDTO;
import com.gastonnicora.trips.dtos.request.vehicle.VehicleCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Vehicle;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.exceptions.ForbiddenException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.VehicleMapper;
import com.gastonnicora.trips.repositories.VehicleRepository;

import jakarta.transaction.Transactional;

/**
 * Servicio encargado de gestionar los vehículos asociados a empresas.
 *
 * <p>
 * Permite crear, consultar y desactivar vehículos, así como validar su
 * existencia, estado y asociación con la empresa correspondiente.
 * </p>
 *
 * @author Gastón
 * @version 1.0
 * @since 2026-09-07
 */
@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    /**
     * Crea una instancia del servicio de vehículos.
     *
     * @param vehicleRepository Repositorio utilizado para gestionar los
     *                          vehículos ({@link VehicleRepository}).
     * @param vehicleMapper     Mapper utilizado para convertir entidades
     *                          {@link Vehicle} en {@link VehicleDTO} ({@link VehicleMapper}).
     */
    public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    /**
     * Busca un vehículo activo mediante su UUID.
     *
     * @param vehicleUuid UUID del vehículo que se desea obtener.
     * @return {@link Vehicle} correspondiente al vehículo encontrado.
     * @throws NotFoundException Si el vehículo no existe o se encuentra
     *                           inactivo.
     * @see VehicleRepository#findByUuidAndActiveTrue(UUID)
     */
    public Vehicle findByUuid(UUID vehicleUuid) {
        return vehicleRepository.findByUuidAndActiveTrue(vehicleUuid)
                .orElseThrow(() -> new NotFoundException("Vehículo no encontrado"));
    }

    /**
     * Crea un nuevo vehículo asociado a una empresa.
     *
     * <p>
     * Antes de crear el vehículo, verifica que no exista otro vehículo activo
     * con la misma patente asociado a la empresa.
     * </p>
     *
     * @param company       Empresa({@link Company}) a la que se asociará el vehículo.
     * @param vehicleCreate Datos del vehículo que se desea crear ({@link VehicleCreate}).
     * @return {@link VehicleDTO} correspondiente al vehículo creado.
     * @throws ConflictException Si ya existe un vehículo activo con la misma
     *                           patente para la empresa.
     * @see VehicleRepository#findByCompanyUuidAndPlateAndActiveTrue(UUID, String)
     * @see VehicleMapper#toDTO(Vehicle)
     */
    public VehicleDTO createVehicle(Company company, VehicleCreate vehicleCreate) {
        if (vehicleRepository
                .findByCompanyUuidAndPlateAndActiveTrue(company.getUuid(), vehicleCreate.getPlate())
                .isPresent()) {
            throw new ConflictException("Ya existe un vehículo activo con esa patente");
        }

        Vehicle vehicle = vehicleRepository.save(
                new Vehicle(company, vehicleCreate.getPlate(), vehicleCreate.getModel(), vehicleCreate.getCapacity()));
        return vehicleMapper.toDTO(vehicle);
    }

    /**
     * Desactiva un vehículo existente asociado a una empresa.
     *
     * <p>
     * Verifica que el vehículo exista y que pertenezca a la empresa indicada
     * antes de marcarlo como inactivo.
     * </p>
     *
     * @param companyUuid UUID de la empresa a la que debe pertenecer el vehículo.
     * @param vehicleUuid UUID del vehículo que se desea desactivar.
     * @throws ForbiddenException Si el vehículo no pertenece a la empresa
     *                            indicada.
     * @see VehicleService#findByUuid(UUID)
     */
    @Transactional
    public void deleteVehicle(UUID companyUuid, UUID vehicleUuid) {
        Vehicle existingVehicle = findByUuid(vehicleUuid);
        if (!companyUuid.equals(existingVehicle.getCompany().getUuid())) {
            throw new ForbiddenException("Acceso denegado");
        }
        existingVehicle.setActive(false);
        vehicleRepository.save(existingVehicle);
    }

    /**
     * Obtiene un vehículo activo mediante su UUID.
     *
     * @param vehicleUuid UUID del vehículo que se desea obtener.
     * @param companyUuid UUID de la compañía propietaria del vehiculo.
     * @return {@link VehicleDTO} correspondiente al vehículo encontrado.
     * @throws ForbiddenException Si el vehículo no pertenece a la compañía.
     * @see VehicleService#findByUuid(UUID)
     * @see VehicleMapper#toDTO(Vehicle)
     */
    public VehicleDTO getVehicle(UUID vehicleUuid, UUID companyUuid) {
        Vehicle vehicle = findByUuid(vehicleUuid);
        if (!vehicle.getCompany().getUuid().equals(companyUuid)) {
            throw new ForbiddenException("El vehiculo no existe");
        }
        return vehicleMapper.toDTO(vehicle);
    }

}