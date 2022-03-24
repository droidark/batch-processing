package xyz.krakenkat.batchprocessing.processor;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.batch.item.ItemProcessor;
import xyz.krakenkat.batchprocessing.dto.TitleDTO;
import xyz.krakenkat.batchprocessing.model.Title;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Slf4j
public class TitleProcessor implements ItemProcessor<TitleDTO, Title> {

    DateFormat format = new SimpleDateFormat("YYYY-MM-dd");

    @Override
    public Title process(final TitleDTO titleDTO) throws Exception {
        final Title transformedTitle = Title
                .builder()
                .publisher(new ObjectId(titleDTO.getPublisher().trim()))
                .name(titleDTO.getName().trim())
                .key(titleDTO.getKey().trim())
                .cover(titleDTO.getCover().trim())
                .demography(titleDTO.getDemography().trim())
                .format(titleDTO.getFormat().trim())
                .frequency(titleDTO.getFrequency().trim())
                .status(titleDTO.getStatus().trim())
                .totalIssues(Integer.parseInt(titleDTO.getTotalIssues().trim()))
                .releaseDate(format.parse(titleDTO.getReleaseDate().trim()))
                .build();
        //log.info("Converting (" + titleDTO + ") into " + transformedTitle + ")");
        return transformedTitle;
    }
}
