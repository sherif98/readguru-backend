package io.readguru.readguru.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddTitleRequest {
    private String titleName;
    private String author;
    private String cover;
}
