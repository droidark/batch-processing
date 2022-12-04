package xyz.krakenkat.batchprocessing.model.mongo;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document("issue")
public class Issue {
    @Id
    private String id;
    private ObjectId title;
    private String name;
    private String key;
    private Double number;
    private String cover;
    private Integer pages;
    private Double printedPrice;
    private Double digitalPrice;
    private String currency;
    private Date releaseDate;
    private String shortReview;
    private String event;
    private String storyArch;
    private String isbn;
    private Long barcode;
    private Integer edition;
    private Boolean variant;
    private String variantOf;
    private Integer likesCounter;
    private Integer dislikesCounter;
    private Map<String, String> authors;
}
