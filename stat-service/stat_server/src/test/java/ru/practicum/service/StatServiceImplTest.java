package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.exception.WrongTimeException;
import ru.practicum.model.Stat;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatServiceImplTest {
    private LocalDateTime start;
    private LocalDateTime end;

    @Mock
    private StatRepository statRepository;

    @InjectMocks
    private StatServiceImpl statService;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.of(2024, 1, 1, 1, 1);
        end = LocalDateTime.of(2024, 1, 2, 1, 1);
    }

    @Test
    void createStat_ShouldCreateStat() {
        LocalDateTime timestamp = LocalDateTime.now();
        StatDto statDto = new StatDto();
        Stat stat = Stat.builder()
                .id(1L)
                .app("someApp").uri("someUri")
                .ip("someIp")
                .timestamp(timestamp)
                .build();
        when(statRepository.save(any())).thenReturn(stat);

        statDto = statService.createStat(statDto);

        assertThat(statDto).isNotNull();
        assertThat(statDto.getApp()).isEqualTo("someApp");
        assertThat(statDto.getUri()).isEqualTo("someUri");
        assertThat(statDto.getIp()).isEqualTo("someIp");
        assertThat(statDto.getTimestamp()).isEqualTo(timestamp);
        verify(statRepository, times(1)).save(any());
        verifyNoMoreInteractions(statRepository);
    }

    @Test
    void readStat_ShouldThrowWrongTimeExceptionWhenStartTimeIsAfterEndTime() {
        List<String> uris = new ArrayList<>();

        assertThatThrownBy(() -> statService.readStat(end, start, uris, true))
                .isInstanceOf(WrongTimeException.class)
                .hasMessage("Время начала не может быть позже времени завершения");
        verify(statRepository, never()).save(any());
        verifyNoMoreInteractions(statRepository);
    }

    @Test
    void readStat_ShouldReadStatWhenUrisAreEmptyAndUniqueIsTrue() {
        List<StatResponseDto> responseDtos = new ArrayList<>();
        when(statRepository.findAllByTimestampBetweenStartAndEndWithUniqueIp(any(), any()))
                .thenReturn(responseDtos);

        List<StatResponseDto> retrievedDtos = statService.readStat(start, end, List.of(), true);

        assertThat(retrievedDtos)
                .isNotNull()
                .isEqualTo(responseDtos);
        verify(statRepository, times(1))
                .findAllByTimestampBetweenStartAndEndWithUniqueIp(any(), any());
        verifyNoMoreInteractions(statRepository);
    }

    @Test
    void readStat_ShouldReadStatWhenUrisAreEmptyAndUniqueIsFalse() {
        List<StatResponseDto> responseDtos = new ArrayList<>();
        when(statRepository.findAllByTimestampBetweenStartAndEndWhereIpNotUnique(any(), any()))
                .thenReturn(responseDtos);

        List<StatResponseDto> retrievedDtos = statService.readStat(start, end, List.of(), false);

        assertThat(retrievedDtos)
                .isNotNull()
                .isEqualTo(responseDtos);
        verify(statRepository, times(1))
                .findAllByTimestampBetweenStartAndEndWhereIpNotUnique(any(), any());
        verifyNoMoreInteractions(statRepository);
    }

    @Test
    void readStat_ShouldReadStatWhenUrisAreNotEmptyAndUniqueIsTrue() {
        List<StatResponseDto> responseDtos = new ArrayList<>();
        List<String> uris = List.of("uri1", "uri2");
        when(statRepository.findAllByTimestampBetweenStartAndEndWithUrisUniqueIp(start, end, uris))
                .thenReturn(responseDtos);

        List<StatResponseDto> retrievedDtos = statService.readStat(start, end, uris, true);

        assertThat(retrievedDtos)
                .isNotNull()
                .isEqualTo(responseDtos);

        verify(statRepository, times(1))
                .findAllByTimestampBetweenStartAndEndWithUrisUniqueIp(start, end, uris);
        verifyNoMoreInteractions(statRepository);
    }

    @Test
    void readStat_ShouldReadStatWhenUrisAreNotEmptyAndUniqueIsFalse() {
        List<StatResponseDto> responseDtos = new ArrayList<>();
        List<String> uris = List.of("uri1", "uri2");
        when(statRepository.findAllByTimestampBetweenStartAndEndWithUrisIpNotUnique(start, end, uris))
                .thenReturn(responseDtos);

        List<StatResponseDto> retrievedDtos = statService.readStat(start, end, uris, false);

        assertThat(retrievedDtos)
                .isNotNull()
                .isEqualTo(responseDtos);

        verify(statRepository, times(1))
                .findAllByTimestampBetweenStartAndEndWithUrisIpNotUnique(start, end, uris);
        verifyNoMoreInteractions(statRepository);
    }
}