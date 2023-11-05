package xyz.krakenkat.batchprocessing.processor;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.batch.item.ItemProcessor;
import xyz.krakenkat.batchprocessing.model.mongo.Issue;
import xyz.krakenkat.batchprocessing.dto.IssueDTO;
import xyz.krakenkat.batchprocessing.util.Constants;
import xyz.krakenkat.batchprocessing.util.Utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Slf4j
public class IssueProcessor implements ItemProcessor<IssueDTO, Issue> {

    private final DateFormat format = new SimpleDateFormat(Constants.DATE_FORMAT);

    @Override
    public Issue process(final IssueDTO issueDTO) throws Exception {
        return Issue
                .builder()
                .title(new ObjectId(issueDTO.getTitle().trim()))
                .name(issueDTO.getName().trim())
                .key(issueDTO.getKey().trim())
                .number(Double.parseDouble(issueDTO.getNumber().trim()))
                .cover(issueDTO.getCover().trim())
                .pages(Integer.parseInt(issueDTO.getPages().trim()))
                .printedPrice(Double.parseDouble(issueDTO.getPrintedPrice().trim()))
                .currency(issueDTO.getCurrency().trim())
                .releaseDate(format.parse(issueDTO.getReleaseDate().trim()))
                .shortReview(Utilities.formatText(issueDTO.getShortReview()))
                .isbn(issueDTO.getIsbn10().isEmpty() ? Constants.ISBN : issueDTO.getIsbn10().trim())
                .edition(Integer.parseInt(issueDTO.getEdition().trim()))
                .variant(Boolean.parseBoolean(issueDTO.getVariant().trim()))
                .build();
    }
}
