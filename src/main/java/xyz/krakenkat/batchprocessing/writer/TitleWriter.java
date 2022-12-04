package xyz.krakenkat.batchprocessing.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import xyz.krakenkat.batchprocessing.dto.TransientDTO;
import xyz.krakenkat.batchprocessing.model.mongo.Title;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class TitleWriter implements ItemWriter<Title> {

    @Autowired
    private MongoOperations mongoOperations;

    private final String COLLECTION_NAME = "title";

    private StepExecution stepExecution;

    private List<TransientDTO> transientDTOList = new ArrayList<>();

    @Override
    public void write(Chunk<? extends Title> chunk) throws Exception {
        Title savedTitle;
        for(Title title : chunk.getItems()) {
            Title dbTitle = mongoOperations.findOne(
                    Query.query(
                            Criteria
                                    .where("publisher")
                                    .is(title.getPublisher())
                                    .and("key")
                                    .is(title.getKey())),
                            Title.class,
                    COLLECTION_NAME);

            if (dbTitle == null) {
                savedTitle = mongoOperations.save(title, COLLECTION_NAME);
            } else {
                savedTitle = dbTitle;
                log.info("The title " + savedTitle.getName() + " already exists");
            }
            this.transientDTOList.add(TransientDTO.builder().key(savedTitle.getKey()).id(savedTitle.getId()).build());
        }
    }

    private Optional<Title> getTitle(Title title) {
        return Optional.ofNullable(mongoOperations.findOne(Query
                .query(Criteria
                        .where("publisher")
                        .is(title.getPublisher())
                        .and("key")
                        .is(title.getKey())),
                Title.class, COLLECTION_NAME));
    }

    @AfterStep
    public void saveTransient(StepExecution stepExecution) {
        //log.info("TransientDTO size: " + transientDTOList.size());
        this.stepExecution = stepExecution;
        this.stepExecution.getExecutionContext().put("transient", this.transientDTOList);
    }
}
