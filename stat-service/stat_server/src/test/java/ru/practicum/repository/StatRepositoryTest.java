package ru.practicum.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.dto.StatResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql("test-data.sql")
class StatRepositoryTest {

    @Autowired
    private StatRepository statRepository;

    private LocalDateTime start;
    private LocalDateTime end;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.of(2024, 11, 1, 0, 0);
        end = LocalDateTime.of(2024, 11, 30, 23, 59);
    }


    @Test
    void testFindAllByTimestampBetweenStartAndEndWithUniqueIp() {
        List<StatResponseDto> result = statRepository.findAllByTimestampBetweenStartAndEndWithUniqueIp(start, end);

        assertThat(result).hasSize(2);
        assertThat(result.getFirst().getApp()).isEqualTo("test-service");
        assertThat(result.getFirst().getUri()).isEqualTo("/api/test");
        assertThat(result.getFirst().getHits()).isEqualTo(2L);
    }

    @Test
    void testFindAllByTimestampBetweenStartAndEndWhereIpNotUnique() {
        List<StatResponseDto> result = statRepository.findAllByTimestampBetweenStartAndEndWhereIpNotUnique(start, end);

        assertThat(result).isNotEmpty().hasSize(2);
        assertThat(result.getFirst().getApp()).isEqualTo("test-service");
        assertThat(result.getFirst().getUri()).isEqualTo("/api/test");
        assertThat(result.getFirst().getHits()).isEqualTo(4L);
    }

    @Test
    void testFindAllByTimestampBetweenStartAndEndWithUrisUniqueIp() {
        List<String> uris = List.of("/api/test");

        List<StatResponseDto> result = statRepository.findAllByTimestampBetweenStartAndEndWithUrisUniqueIp(start, end, uris);

        assertThat(result).isNotEmpty().hasSize(1);
        assertThat(result.getFirst().getUri()).isEqualTo("/api/test");
        assertThat(result.getFirst().getHits()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Поиск неуникальных IP по списку URI")
    void testFindAllByTimestampBetweenStartAndEndWithUrisIpNotUnique() {
        List<String> uris = List.of("/api/test");

        List<StatResponseDto> result = statRepository.findAllByTimestampBetweenStartAndEndWithUrisIpNotUnique(start, end, uris);

        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().getUri()).isEqualTo("/api/test");
        assertThat(result.getFirst().getHits()).isEqualTo(4L);
    }
}