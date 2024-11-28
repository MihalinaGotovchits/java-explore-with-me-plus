package ru.practicum.model.mapper;

import ru.practicum.dto.StatDto;
import ru.practicum.model.Stat;

public class StatMapper {
    public static Stat toStat(StatDto statDto) {
        return Stat.builder()
                .ip(statDto.getIp())
                .app(statDto.getApp())
                .uri(statDto.getUri())
                .timestamp(statDto.getTimestamp())
                .build();
    }

    public static StatDto toStatDto(Stat stat) {
        return StatDto.builder()
                .app(stat.getApp())
                .ip(stat.getIp())
                .uri(stat.getUri())
                .timestamp(stat.getTimestamp())
                .build();
    }
}
