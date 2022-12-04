package xyz.krakenkat.batchprocessing;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import xyz.krakenkat.batchprocessing.dto.IssueDTO;
import xyz.krakenkat.batchprocessing.dto.TitleDTO;
import xyz.krakenkat.batchprocessing.model.Issue;
import xyz.krakenkat.batchprocessing.model.Title;
import xyz.krakenkat.batchprocessing.processor.IssueProcessor;
import xyz.krakenkat.batchprocessing.processor.TitleProcessor;
import xyz.krakenkat.batchprocessing.reader.IssueReader;
import xyz.krakenkat.batchprocessing.writer.IssueWriter;
import xyz.krakenkat.batchprocessing.writer.TitleWriter;


@Slf4j
@ComponentScan
@Configuration
@EnableBatchProcessing
@NoArgsConstructor
public class BatchConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    private final String DELIMITER = "|";

    private final String[] TITLES_HEADER = {"PUBLISHER",
            "NAME",
            "KEY",
            "COVER",
            "DEMOGRAPHY",
            "FORMAT",
            "TYPE",
            "FREQUENCY",
            "STATUS",
            "TOTAL ISSUES",
            "RELEASE DATE",
            "GENRES",
            "AUTHORS"};

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

    @Value("${batch-execution.file-name}")
    private String csvFile;

    @Bean
    public FlatFileItemReader<TitleDTO> titleReader() {
        System.out.println(csvFile);
        return new FlatFileItemReaderBuilder<TitleDTO>()
                .name("titleReader")
                .resource(new ClassPathResource(csvFile + ".csv"))
                .delimited()
                .delimiter(DELIMITER)
                .names(TITLES_HEADER).fieldSetMapper(new BeanWrapperFieldSetMapper<TitleDTO>() {{
                    setTargetType(TitleDTO.class);
                }})
                .linesToSkip(1)
                .build();
    }

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
    public IssueWriter issueWriter() {
        return new IssueWriter();
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
                .get("issueStep")
                .<IssueDTO, Issue> chunk(1)
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
