package xyz.krakenkat.batchprocessing.reader;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import xyz.krakenkat.batchprocessing.dto.IssueDTO;
import xyz.krakenkat.batchprocessing.dto.TransientDTO;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class IssueReader implements ItemReader<IssueDTO> {

    private int i = -1;
    private static final char DELIMITER = '|';
    private List<TransientDTO> transientDTOList;
    private List<IssueDTO> issueDTOList = new ArrayList<>();

    @Override
    public IssueDTO read() throws Exception {
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
        //log.info("transient: " + this.transientDTOList);
        buildList();
    }

    private void buildList() {
        for(TransientDTO transientDTO : transientDTOList) {
            this.readCSV(transientDTO);
        }
    }

    private void readCSV(TransientDTO transientDTO) {
        try {
            Reader reader = Files.newBufferedReader(Paths.get(ClassLoader.getSystemResource("kamite-manga/" + transientDTO.getKey() + ".csv").toURI()));
            CSVFormat csvFormat = CSVFormat
                    .DEFAULT
                    .builder()
                    .setDelimiter(DELIMITER)
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build();

            CSVParser csvParser = new CSVParser(reader, csvFormat);

            for (CSVRecord csvRecord : csvParser) {
                this.issueDTOList.add(IssueDTO.builder()
                        .title(transientDTO.getId())
                        .name(csvRecord.get(0))
                        .key(csvRecord.get(1))
                        .number(csvRecord.get(2))
                        .cover(csvRecord.get(3))
                        .pages(csvRecord.get(4))
                        .printedPrice(csvRecord.get(5))
                        .currency(csvRecord.get(6))
                        .releaseDate(csvRecord.get(7))
                        .shortReview(csvRecord.get(8))
                        .isbn10(csvRecord.get(9))
                        .edition(csvRecord.get(10))
                        .variant(csvRecord.get(11))
                        .build());
            }
        } catch (Exception ex) {
            log.info("Error parsing " + transientDTO.getKey());
        }
    }
}
