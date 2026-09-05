package com.gastonnicora.trips.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.gastonnicora.trips.dtos.entities.BusDTO;
import com.gastonnicora.trips.dtos.request.bus.BusCreate;
import com.gastonnicora.trips.entities.Bus;
import com.gastonnicora.trips.entities.Company;
import com.gastonnicora.trips.exceptions.ConflictException;
import com.gastonnicora.trips.exceptions.NotFoundException;
import com.gastonnicora.trips.mappers.BusMapper;
import com.gastonnicora.trips.repositories.BusRepository;

@Service
public class BusService {

    private final BusRepository busRepository;
    private final BusMapper busMapper;

    public BusService(BusRepository busRepository, BusMapper busMapper) {
        this.busRepository = busRepository;
        this.busMapper = busMapper;
    }

    /**
     * Crea un nuevo bus asociado a una empresa.
     *
     * @param company Empresa a la que se asociará el bus.
     * @param busCreate DTO que contiene los datos del bus a crear.
     * @return DTO del bus creado.
     * @throws ConflictException si ya existe un bus con la misma placa para la
     * empresa.
     */
    public BusDTO createBus(Company company, BusCreate busCreate) {
        Optional<Bus> existingBus = busRepository.findByCompanyUuidAndPlate(company.getUuid(), busCreate.getPlate());
        if (existingBus.isPresent()) {
            throw new ConflictException("Ya existe un bus con la misma placa para esta empresa");
        }
        var bus = busRepository.save(new Bus(company, busCreate.getPlate(), busCreate.getModel(), busCreate.getCapacity()));
        return busMapper.toDTO(bus);
    }

    /**
     * Elimina un bus asociado a una empresa.
     *
     * @param company Empresa a la que está asociado el bus.
     * @param plate Placa del bus a eliminar.
     * @throws NotFoundException si no existe un bus con la placa especificada
     * para la empresa.
     */
    public void deleteBus(Company company, String plate) {
        Optional<Bus> existingBus = busRepository.findByCompanyUuidAndPlate(company.getUuid(), plate);
        if (existingBus.isEmpty()) {
            throw new NotFoundException("No existe un bus con la placa especificada para esta empresa");
        }
        existingBus.get().setActive(false);
        busRepository.save(existingBus.get());
    }

}
