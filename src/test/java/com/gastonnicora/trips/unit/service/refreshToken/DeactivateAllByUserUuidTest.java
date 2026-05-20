package com.gastonnicora.trips.unit.service.refreshToken;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gastonnicora.trips.entities.RefreshToken;
import com.gastonnicora.trips.entities.User;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import com.gastonnicora.trips.repositories.UserRepository;
import com.gastonnicora.trips.services.RefreshTokenService;

@ExtendWith(MockitoExtension.class)
public class DeactivateAllByUserUuidTest {
    @Mock
    private RefreshTokenRepository repo;

    @Mock
    private UserRepository userRepo;


    @InjectMocks
    private RefreshTokenService service;

    @Test
    void shouldDeactivateAllTokens() {

        UUID uuid = UUID.randomUUID();

        RefreshToken token1 = new RefreshToken();
        token1.setActive(true);

        RefreshToken token2 = new RefreshToken();
        token2.setActive(true);

        when(repo.findAllByUser_UuidAndActiveTrue(uuid))
                .thenReturn(List.of(token1, token2));

        service.deactivateAllByUserUuid(uuid);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);

        verify(repo, times(2)).save(captor.capture());

        List<RefreshToken> saved = captor.getAllValues();

        assertTrue(saved.stream().allMatch(rt -> !rt.isActive()));
    }

    @Test
    void shouldDeactivateAllTokensIfEmptyList() {

        UUID uuid = UUID.randomUUID();

        when(repo.findAllByUser_UuidAndActiveTrue(uuid))
                .thenReturn(List.of());

        service.deactivateAllByUserUuid(uuid);

        verify(repo, never()).save(any(RefreshToken.class));
    }
}
