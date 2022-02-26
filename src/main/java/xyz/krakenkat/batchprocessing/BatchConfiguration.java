package xyz.krakenkat.batchprocessing;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoOperations;
import xyz.krakenkat.batchprocessing.dto.IssueDTO;
import xyz.krakenkat.batchprocessing.dto.TitleDTO;
import xyz.krakenkat.batchprocessing.model.Issue;
import xyz.krakenkat.batchprocessing.model.Title;
import xyz.krakenkat.batchprocessing.processor.IssueProcessor;
import xyz.krakenkat.batchprocessing.processor.TitleProcessor;
import xyz.krakenkat.batchprocessing.reader.IssueReader;
import xyz.krakenkat.batchprocessing.writer.TitleWriter;

@Configuration
@Slf4j
@EnableBatchProcessing
@AllArgsConstructor
public class BatchConfiguration {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;

    private MongoOperations mongoOperations;

    private final String DELIMITER = "|";

    private final String[] TITLES_HEADER = {"PUBLISHER",
            "NAME",
            "KEY",
            "COVER",
            "DEMOGRAPHY",
            "FORMAT",
            "FREQUENCY",
            "STATUS",
            "TOTAL ISSUES",
            "RELEASE DATE"};

    private final String[] ISSUES_HEADER = {"TITLE",
            "NAME",
            "KEY",
            "NUMBER",
            "COVER",
            "PAGES",
            "PRINTED_PRICE",
            "CURRENCY",
            "RELEASE_DATE",
            "SHORT_REVIEW",
            "ISBN10",
            "EDITION",
            "VARIANT"
    };

    @Bean
    public FlatFileItemReader<TitleDTO> titleReader() {
        return new FlatFileItemReaderBuilder<TitleDTO>()
                .name("titleReader")
                .resource(new ClassPathResource("panini-manga-titles.csv"))
                .delimited()
                .delimiter(DELIMITER)
                .names(TITLES_HEADER).fieldSetMapper(new BeanWrapperFieldSetMapper<TitleDTO>() {{
                    setTargetType(TitleDTO.class);
                }})
                .linesToSkip(1)
                .build();
    }

//    @Bean
//    public FlatFileItemReader<IssueDTO> issueReader() {
//        return new FlatFileItemReaderBuilder<IssueDTO>()
//                .name("issueReader")
//                .resource(new ClassPathResource("maid-sama.csv"))
//                .delimited()
//                .delimiter(DELIMITER)
//                .names(ISSUES_HEADER)
//                .fieldSetMapper(new BeanWrapperFieldSetMapper<IssueDTO>() {{
//                    setTargetType(IssueDTO.class);
//                }}).linesToSkip(1)
//                .build();
//    }

    @Bean
    public IssueReader issueReader() {
        return new IssueReader();
    }

    @Bean
    public TitleProcessor titleProcessor() {
        return new TitleProcessor();
    }

    public IssueProcessor issueItemProcessor() {
        return new IssueProcessor();
    }

    @Bean
    public TitleWriter titleWriter() {
        return new TitleWriter();
    }

    @Bean
    public MongoItemWriter<Issue> issueWriter() {
        return new MongoItemWriterBuilder<Issue>().template(mongoOperations).collection("issue").build();
    }

    @Bean
    public Step titleStep() {
        return stepBuilderFactory
                .get("titleStep")
                .<TitleDTO, Title> chunk(1)
                .reader(titleReader())
                .processor(titleProcessor())
                .writer(titleWriter())
                .listener(promotionListener())
                .build();
    }

    @Bean
    public Step issueStep() {
        return stepBuilderFactory
                .get("issueStep").<IssueDTO, Issue> chunk(1)
                .reader(issueReader())
                .processor(issueItemProcessor())
                .writer(issueWriter())
                .build();
    }

    @Bean
    public Job job() {
        return jobBuilderFactory
                .get("importJob")
                .start(titleStep())
                .next(issueStep())
                .build();
    }

    @Bean
    public ExecutionContextPromotionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[] {"transient"});
        listener.setStrict(true);
        return listener;
    }
}
