package ru.practicum.service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.exception.WrongTimeException;
import ru.practicum.model.Stat;
import ru.practicum.model.mapper.StatMapper;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;

    @Override
    @Transactional
    public StatDto createStat(StatDto statDto) {
        log.info("createStat - invoked");
        Stat stat = statRepository.save(StatMapper.toStat(statDto));
        log.info("createStat - stat save successfully - {}", stat);
        return StatMapper.toStatDto(stat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatResponseDto> readStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("readStat - invoked");

        if (start.isAfter(end)) {
            log.error("Время начала не может быть позже времени завершения");
            throw new WrongTimeException("Время начала не может быть позже времени завершения");
        }

        if (uris.isEmpty()) {
            if (unique) {
                log.info("readStat - success - unique = true, uris empty");
                return statRepository.findAllByTimestampBetweenStartAndEndWithUniqueIp(start, end);
            } else {
                log.info("readStat - success - unique = false, uris empty");
                return statRepository.findAllByTimestampBetweenStartAndEndWhereIpNotUnique(start, end);
            }
        } else {
            if (unique) {
                log.info("readStat - success - unique = true, uris not empty");
                return statRepository.findAllByTimestampBetweenStartAndEndWithUrisUniqueIp(start, end, uris);
            } else {
                log.info("readStat - success - unique = false, uris not empty");
                return statRepository.findAllByTimestampBetweenStartAndEndWithUrisIpNotUnique(start, end, uris);
            }
        }
    }
}
