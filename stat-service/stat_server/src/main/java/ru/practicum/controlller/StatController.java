package ru.practicum.controlller;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.Create;
import ru.practicum.dto.StatDto;
import ru.practicum.dto.StatResponseDto;
import ru.practicum.service.StatService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.utill.Constants.DATE_FORMAT;

@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class StatController {
    private final StatService statService;

    @PostMapping("/hit")
    public ResponseEntity<Object> addStatEvent(@RequestBody @Validated(Create.class) StatDto statDto) {
        StatDto statEvent = statService.createStat(statDto);
        log.info("Returning response from POST request to /hit/ endpoint - statDto = {}", statDto);
        return new ResponseEntity<>(statEvent, HttpStatus.CREATED);
    }

    @GetMapping("/stats")
    public List<StatResponseDto> readStatEvent(@Nullable @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime start,
                                                               @Nullable @RequestParam @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime end,
                                                               @RequestParam(defaultValue = "") List<String> uris,
                                                               @RequestParam(defaultValue = "false") boolean unique) {
        List<StatResponseDto> stats = statService.readStat(start, end, uris, unique);
        log.info("GET/stats - stat size = {}", stats.size());
        return stats;
    }
}
