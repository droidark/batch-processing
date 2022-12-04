package xyz.krakenkat.batchprocessing.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import xyz.krakenkat.batchprocessing.model.Issue;

import java.util.List;

@Slf4j
public class IssueWriter implements ItemWriter<Issue> {

    private final String COLLECTION_NAME = "issue";

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void write(List<? extends Issue> issues) throws Exception {
        for (Issue issue : issues) {
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
                log.info("The issue " + issue.getName() + " already exists in the DB");
            }
        }
    }
}
