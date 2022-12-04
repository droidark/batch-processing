package xyz.krakenkat.batchprocessing.processor;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.batch.item.ItemProcessor;
import xyz.krakenkat.batchprocessing.dto.TitleDTO;
import xyz.krakenkat.batchprocessing.model.mongo.Title;
import xyz.krakenkat.batchprocessing.util.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class TitleProcessor implements ItemProcessor<TitleDTO, Title> {

    DateFormat format = new SimpleDateFormat(Constants.DATE_FORMAT);

    @Override
    public Title process(final TitleDTO titleDTO) throws Exception {
        return Title
                .builder()
                .publisher(new ObjectId(titleDTO.getPublisher().trim()))
                .name(titleDTO.getName().trim())
                .key(titleDTO.getKey().trim())
                .cover(titleDTO.getCover().trim())
                .demography(titleDTO.getDemography().trim())
                .format(titleDTO.getFormat().trim())
                .type(titleDTO.getType().trim())
                .frequency(titleDTO.getFrequency().trim())
                .status(titleDTO.getStatus().trim())
                .totalIssues(Integer.parseInt(titleDTO.getTotalIssues().trim()))
                .releaseDate(format.parse(titleDTO.getReleaseDate().trim()))
                .genres(Arrays.stream(titleDTO.getGenres().split(",")).map(String::trim).toList())
                .authors(getAuthors(titleDTO.getAuthors()))
                .build();
    }

    private Map<String, List<String>> getAuthors(String authors) {
        Map<String, List<String>> creators = new HashMap<>();
        String[] division = authors.split(";");
        for (String s : division) {
            String[] subdiv = s.split(":");
            List<String> names = Arrays.stream(subdiv[1].split(",")).map(String::trim).toList();
            creators.put(subdiv[0]
                    .replace("Writer", "story by")
                    .replace("Illustrator", "art by")
                    .replace("Author", "created by")
                    .trim()
                    .toLowerCase(Locale.ROOT), names);
        }
        return  creators;
    }


}
