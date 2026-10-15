package com.gastonnicora.trips.unit.refreshToken;

import com.gastonnicora.trips.entities.RefreshToken;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import com.gastonnicora.trips.services.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeactivateAllByUserUuidTest {

    @Mock
    private RefreshTokenRepository repo;

    @InjectMocks
    private RefreshTokenService service;

    @Test
    void shouldDeactivateAllTokensForUser() {

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
    void shouldDoNothingWhenUserHasNoTokens() {

        UUID uuid = UUID.randomUUID();

        when(repo.findAllByUser_UuidAndActiveTrue(uuid))
                .thenReturn(List.of());

        service.deactivateAllByUserUuid(uuid);

        verify(repo, never()).save(any(RefreshToken.class));
    }
}
