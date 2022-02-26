package xyz.krakenkat.batchprocessing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IssueDTO {
    private String title;
    private String name;
    private String key;
    private String number;
    private String cover;
    private String pages;
    private String printedPrice;
    private String currency;
    private String releaseDate;
    private String shortReview;
    private String isbn10;
    private String edition;
    private String variant;
}
