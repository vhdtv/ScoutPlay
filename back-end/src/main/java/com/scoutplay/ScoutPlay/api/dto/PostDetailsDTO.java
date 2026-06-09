package com.scoutplay.ScoutPlay.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PostDetailsDTO extends PostDTO {
    private UserSummaryDTO autor;
    private Metadata metadados;
}

@Getter
@Setter
class Metadata {
    protected Boolean segueConta;
}