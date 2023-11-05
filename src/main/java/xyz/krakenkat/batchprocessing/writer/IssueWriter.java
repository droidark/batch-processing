package xyz.krakenkat.batchprocessing.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
<<<<<<< HEAD
import xyz.krakenkat.batchprocessing.model.mongo.Issue;
=======
import xyz.krakenkat.batchprocessing.model.Issue;
>>>>>>> f6139b48ec7d3bf6e2c8828f9c2dd60036cca289

@Slf4j
public class IssueWriter implements ItemWriter<Issue> {

    private static final String COLLECTION_NAME = "issue";

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void write(Chunk<? extends Issue> chunk) {
        for (Issue issue : chunk.getItems()) {
            Issue dbIssue = mongoOperations.findOne(
                    Query.query(Criteria
                            .where("title")
                            .is(issue.getTitle())
                            .and("key")
                            .is(issue.getKey())
                            .and("variant")
                            .is(false)),
                    Issue.class, COLLECTION_NAME);
            if (dbIssue == null) {
                mongoOperations.save(issue, COLLECTION_NAME);
            } else {
                log.info("The issue {} already exists in the DB", issue.getName());
            }
        }
    }
}
