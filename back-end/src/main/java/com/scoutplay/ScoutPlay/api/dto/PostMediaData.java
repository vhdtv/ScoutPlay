package com.scoutplay.ScoutPlay.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class PostMediaData {
    protected String src;
    protected String poster;
    protected String mimeType;
}
