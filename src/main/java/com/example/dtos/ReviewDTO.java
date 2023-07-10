package com.example.dtos;

import com.example.entities.Hotel;
import com.example.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {
    private Long id;
    private String text;
    private int stars;
    private LocalDateTime date;
    private String userDisplayName;
}
