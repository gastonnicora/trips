package com.gastonnicora.trips.unit.user;

import com.gastonnicora.trips.dtos.request.user.UserPut;
import com.gastonnicora.trips.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Cambiar el email, subir la versión y desactivar los refresh tokens debe
 * ocurrir en una sola transacción. Spring solo aplica {@code @Transactional}
 * a métodos públicos invocados desde fuera del bean, así que la anotación
 * tiene que estar en los puntos de entrada y no en el método privado.
 */
class UpdateUserTransactionTest {

    @Test
    void updateCurrentUserShouldRunInsideATransaction() throws NoSuchMethodException {
        Method method = UserService.class.getMethod("updateCurrentUser", UserPut.class);

        assertNotNull(method.getAnnotation(Transactional.class),
                "updateCurrentUser debe ser @Transactional");
    }

    @Test
    void updateUserByUuidShouldRunInsideATransaction() throws NoSuchMethodException {
        Method method = UserService.class.getMethod("updateUserByUuid", UUID.class, UserPut.class);

        assertNotNull(method.getAnnotation(Transactional.class),
                "updateUserByUuid debe ser @Transactional");
    }
}