package ru.practicum.mapper;

import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class UriMapper {
    public static<T> List<String> toUris(List<T> ids, String path) {
        return ids.stream().map(id -> path + "/" + id).toList();
    }
}