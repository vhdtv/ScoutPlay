package com.scoutplay.ScoutPlay.api.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDataOutputDTO {
    private UUID postId;
    private String texto;
    private UserSummaryDTO por;
}
