package xyz.krakenkat.batchprocessing.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import xyz.krakenkat.batchprocessing.dto.TransientDTO;
import xyz.krakenkat.batchprocessing.model.Title;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TitleWriter implements ItemWriter<Title> {

    @Autowired
    private MongoOperations mongoOperations;

    private final String COLLECTION_NAME = "title";

    private StepExecution stepExecution;

    private List<TransientDTO> transientDTOList = new ArrayList<>();

    @Override
    public void write(List<? extends Title> list) throws Exception {
        for(Title title : list) {
            Title savedTitle = mongoOperations.save(title, COLLECTION_NAME);
            this.transientDTOList.add(TransientDTO.builder().key(savedTitle.getKey()).id(savedTitle.getId()).build());
            log.info("Getting id: " + savedTitle.getId());
        }
    }

    @AfterStep
    public void saveTransient(StepExecution stepExecution) {
        log.info("TransientDTO size: " + transientDTOList.size());
        this.stepExecution = stepExecution;
        this.stepExecution.getExecutionContext().put("transient", this.transientDTOList);
    }
}
