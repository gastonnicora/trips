package com.gastonnicora.trips.unit.worker;

import com.gastonnicora.trips.enums.RoleCompany;
import com.gastonnicora.trips.exceptions.BadRequestException;
import com.gastonnicora.trips.services.WorkerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class VerifyRoleTest {

    @InjectMocks
    private WorkerService workerService;

    @Test
    void shouldNotThrowExceptionWhenRolesAreValid() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(
                () -> ReflectionTestUtils.invokeMethod(
                        workerService, "verifyRole",
                        Set.of(RoleCompany.ADMIN)
                                                      )
                                                           );
    }

    @Test
    void shouldThrowBadRequestExceptionWhenRolesAreEmpty() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> ReflectionTestUtils.invokeMethod(
                        workerService, "verifyRole", Set.of()
                                                      )
                                                    );

        assertEquals(
                "Se debe asignar al menos un rol al trabajador",
                exception.getMessage()
                    );
    }

    @Test
    void shouldThrowBadRequestExceptionWhenRolesAreNull() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> ReflectionTestUtils.invokeMethod(
                        workerService, "verifyRole", (Set<RoleCompany>) null
                                                      )
                                                    );

        assertEquals(
                "Se debe asignar al menos un rol al trabajador",
                exception.getMessage()
                    );
    }

    @Test
    void shouldThrowBadRequestExceptionWhenRolesContainOwner() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> ReflectionTestUtils.invokeMethod(
                        workerService, "verifyRole",
                        Set.of(RoleCompany.OWNER)
                                                      )
                                                    );

        assertEquals(
                "No se puede asignar el rol de OWNER a un trabajador",
                exception.getMessage()
                    );
    }
}