package xyz.krakenkat.batchprocessing.reader;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import xyz.krakenkat.batchprocessing.dto.IssueDTO;
import xyz.krakenkat.batchprocessing.dto.TransientDTO;
import xyz.krakenkat.batchprocessing.util.Constants;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class IssueReader implements ItemReader<IssueDTO> {

    private int i = -1;
    private List<TransientDTO> transientDTOList;
    private final List<IssueDTO> issueDTOList = new ArrayList<>();

    @Value("${batch-execution.folder}")
    private String folder;

    @Override
    public IssueDTO read() {
        if (i < this.issueDTOList.size() - 1) {
            i++;
            return issueDTOList.get(i);
        }
        return null;
    }

    @BeforeStep
    public void retrieveSharedData(StepExecution stepExecution) {
        JobExecution jobExecution = stepExecution.getJobExecution();
        ExecutionContext jobContext = jobExecution.getExecutionContext();
        this.transientDTOList = (List<TransientDTO>) jobContext.get("transient");
        buildList();
    }

    private void buildList() {
        for(TransientDTO transientDTO : transientDTOList) {
            this.readCSV(transientDTO);
        }
    }

    private void readCSV(TransientDTO transientDTO) {
        try (Reader reader = Files
                .newBufferedReader(Paths.get(ClassLoader
                        .getSystemResource(folder + "/" + transientDTO.getKey() + Constants.FILE_EXTENSION)
                        .toURI()
                ))) {

            CSVFormat csvFormat = CSVFormat
                        .DEFAULT
                        .builder()
                        .setDelimiter(Constants.DELIMITER)
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .build();

                issueDTOList.addAll(new CSVParser(reader, csvFormat)
                        .stream()
                        .map(rec -> IssueDTO
                                .builder()
                                .title(transientDTO.getId())
                                .name(rec.get(0))
                                .key(rec.get(1))
                                .number(rec.get(2))
                                .cover(rec.get(3))
                                .pages(rec.get(4))
                                .printedPrice(rec.get(5))
                                .currency(rec.get(6))
                                .releaseDate(rec.get(7))
                                .shortReview(rec.get(8))
                                .isbn10(rec.get(9))
                                .edition(rec.get(10))
                                .variant(rec.get(11))
                                .build())
                        .toList());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
