package xyz.krakenkat.batchprocessing.model.mongo;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("title")
public class Title implements Serializable {
    @Id
    private String id;
    private ObjectId publisher;
    private String name;
    private String key;
    private String cover;
    private String demography;
    private String format;
    private String type;
    private String frequency;
    private String status;
    private Integer totalIssues;
    private Date releaseDate;
    private List<String> genres;
    private Map<String, List<String>> authors;
}
