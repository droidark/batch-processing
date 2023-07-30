package xyz.krakenkat.batchprocessing.processor;

import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.batch.item.ItemProcessor;
import xyz.krakenkat.batchprocessing.model.Issue;
import xyz.krakenkat.batchprocessing.dto.IssueDTO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Slf4j
public class IssueProcessor implements ItemProcessor<IssueDTO, Issue> {

    private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

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
                .shortReview(issueDTO.getShortReview()
                        .replace("Â¿", "¿")
                        .replace("Â¡", "¡")
                        .replace("Â´", "'")
                        .replace("Ã±", "ñ")
                        .replace("Ã‘", "Ñ")
                        .replace("Ã¡", "á")
                        .replace("Ã©", "é")
                        .replace("Ã\u00AD", "í")
                        .replace("Ã³", "ó")
                        .replace("Ãº", "ú")
                        .replace("Ã\u0081", "Á")
                        .replace("Ã‰", "É")
                        .replace("Ã\u008D", "Í")
                        .replace("Ã“", "Ó")
                        .replace("Ãš", "Ú")
                        .replace("â€¦", "...")
                        .replace("â€“", "\"")
                        .trim())
                .isbn(issueDTO.getIsbn10().equals("") ? "000-0-00-000000-0" : issueDTO.getIsbn10().trim())
                .edition(Integer.parseInt(issueDTO.getEdition().trim()))
                .variant(Boolean.parseBoolean(issueDTO.getVariant().trim()))
                .build();
    }
}
