package xyz.krakenkat.batchprocessing.model;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("title")
public class Title {
    private String id;
    private ObjectId publisher;
    private String name;
    private String key;
    private String cover;
    private String demography;
    private String format;
    private String frequency;
    private String status;
    private Integer totalIssues;
    private Date releaseDate;
    private List<String> genres;
}
