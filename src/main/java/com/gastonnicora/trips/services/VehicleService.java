package com.gastonnicora.trips.services;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.gastonnicora.trips.dtos.entities.VehicleDTO;
import com.gastonnicora.trips.dtos.request.vehicle.VehicleCreate;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.entities.Vehicle;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.VehicleMapper;
import com.gastonnicora.trips.repositories.VehicleRepository;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleMapper vehicleMapper;

    public VehicleService(VehicleRepository vehicleRepository, VehicleMapper vehicleMapper) {
        this.vehicleRepository = vehicleRepository;
        this.vehicleMapper = vehicleMapper;
    }

    public Vehicle findByUuid(UUID vehicleUuid) {

        Vehicle vehicle = vehicleRepository.findByUuid(vehicleUuid).orElseThrow(() -> new NotFoundException("Vehicle no encontrado"));
        if (!vehicle.isActive()) {
            throw new NotFoundException("Vehicle no encontrado");
        }
        return vehicle;
    }

    /**
     * Crea un nuevo vehículo asociado a una empresa.
     *
     * @param company Empresa a la que se asociará el vehículo.
     * @param vehicleCreate DTO que contiene los datos del vehículo a crear.
     * @return DTO del vehículos creado.
     * @throws ConflictException si ya existe un vehículo con la misma patente para la
     * empresa.
     */
    public VehicleDTO createVehicle(Company company, VehicleCreate vehicleCreate) {
        Optional<Vehicle> existingVehicle = vehicleRepository.findByCompanyUuidAndPlate(company.getUuid(), vehicleCreate.getPlate());
        if (existingVehicle.isPresent()) {
            throw new ConflictException("Ya existe un vehículo con la misma patente para esta empresa");
        }
        var vehicle = vehicleRepository.save(new Vehicle(company, vehicleCreate.getPlate(), vehicleCreate.getModel(), vehicleCreate.getCapacity()));
        return vehicleMapper.toDTO(vehicle);
    }

    /**
     * Elimina un vehículo existente marcándolo como inactivo.
     *
     * @param vehicleUuid UUID del vehículo a eliminar.
     * @throws NotFoundException si no existe un vehículo con el UUID proporcionado.
     */
    public void deleteVehicle(UUID vehicleUuid) {
        Vehicle existingVehicle = findByUuid(vehicleUuid);
        existingVehicle.setActive(false);
        vehicleRepository.save(existingVehicle);
    }

    /**
     * Obtiene un vehículo existente por su UUID.
     *
     * @param vehicleUuid UUID del vehículo a obtener.
     * @return DTO del vehículo encontrado.
     * @throws NotFoundException si no existe un vehículo con el UUID proporcionado.
     */
    public VehicleDTO getVehicle(UUID vehicleUuid) {
        Vehicle vehicle = findByUuid(vehicleUuid);
        return vehicleMapper.toDTO(vehicle);
    }

}
