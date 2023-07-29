package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private long id;
    @NotNull
    private LocalDate start;
    @NotNull
    private LocalDate end;
    private long itemId;
    private long bookerId;
}
