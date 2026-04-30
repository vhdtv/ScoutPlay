package com.scoutplay.ScoutPlay.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Resposta padronizada para respostas com paginação
 * Encapsula dados paginados com informações de paginação
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private int currentPage;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;

    /**
     * Cria resposta de paginação a partir de Page do Spring
     */
    public static <T> PageResponse<T> fromPage(Page<T> page) {
        return PageResponse.<T>builder()
            .content(page.getContent())
            .currentPage(page.getNumber())
            .pageSize(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .hasNext(page.hasNext())
            .hasPrevious(page.hasPrevious())
            .build();
    }
}
