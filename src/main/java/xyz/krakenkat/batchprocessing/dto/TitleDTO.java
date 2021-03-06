package xyz.krakenkat.batchprocessing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TitleDTO {
    private String publisher;
    private String name;
    private String key;
    private String cover;
    private String demography;
    private String format;
    private String frequency;
    private String status;
    private String totalIssues;
    private String releaseDate;
}
