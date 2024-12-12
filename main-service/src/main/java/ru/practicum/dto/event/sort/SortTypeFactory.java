package ru.practicum.dto.event.sort;

public class SortTypeFactory {
    public static String getSortColumn(SortType sortType) {
        return switch (sortType) {
            case EVENT_DATE -> "eventDate";
            case VIEWS -> "views";
        };
    }
}