package xyz.krakenkat.batchprocessing;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import xyz.krakenkat.batchprocessing.dto.TitleDTO;
import xyz.krakenkat.batchprocessing.model.Issue;
import xyz.krakenkat.batchprocessing.dto.IssueDTO;
import xyz.krakenkat.batchprocessing.model.Title;
import xyz.krakenkat.batchprocessing.processor.IssueItemProcessor;
import xyz.krakenkat.batchprocessing.processor.TitleItemProcessor;
import xyz.krakenkat.batchprocessing.step.NotifierTasklet;

@Configuration
@Slf4j
@EnableBatchProcessing
@AllArgsConstructor
@NoArgsConstructor
public class BatchConfiguration {

    public JobBuilderFactory jobBuilderFactory;

    public StepBuilderFactory stepBuilderFactory;

    private final String DELIMITER = "|";

    @Value("${batchExecution}")
    private String batchExecution;

    @Bean
    public FlatFileItemReader<TitleDTO> titleReader() {
        log.info("batchExecution -> " + batchExecution);
        return new FlatFileItemReaderBuilder<TitleDTO>()
                .name("titleReader")
                .resource(new ClassPathResource("panini-manga-titles.csv"))
                .delimited()
                .delimiter(DELIMITER).names(new String[] {
                        "PUBLISHER",
                        "NAME",
                        "KEY",
                        "COVER",
                        "DEMOGRAPHY",
                        "FORMAT",
                        "FREQUENCY",
                        "STATUS",
                        "TOTAL ISSUES",
                        "RELEASE DATE"
                }).fieldSetMapper(new BeanWrapperFieldSetMapper<TitleDTO>(){{
                    setTargetType(TitleDTO.class);
                }}).linesToSkip(1)
                .build();
    }

    @Bean
    public FlatFileItemReader<IssueDTO> reader() {
        return new FlatFileItemReaderBuilder<IssueDTO>()
                .name("issueReader")
                .resource(new ClassPathResource("maid-sama.csv"))
                .delimited()
                .delimiter(DELIMITER)
                .names(new String[] {
                        "title",
                        "name",
                        "key",
                        "number",
                        "cover",
                        "pages",
                        "printedPrice",
                        "currency",
                        "releaseDate",
                        "shortReview",
                        "isbn10",
                        "edition",
                        "variant"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<IssueDTO>(){{
                    setTargetType(IssueDTO.class);
                }})
                .linesToSkip(1)
                .build();
    }

    @Bean
    public TitleItemProcessor titleProcessor() { return new TitleItemProcessor(); }

    @Bean
    public IssueItemProcessor processor() {
        return new IssueItemProcessor();
    }

    @Bean
    public ItemWriter<Title> titleWriter() {
        MongoItemWriter<Title> writer = new MongoItemWriter<>();
        try {
            writer.setTemplate(mongoTemplate());
        } catch (Exception e) {
            log.error(e.toString());
        }
        writer.setCollection("title");
        return writer;
    }

    @Bean
    public ItemWriter<Issue> issueWriter() {
        MongoItemWriter<Issue> writer = new MongoItemWriter<>();
        try {
            writer.setTemplate(mongoTemplate());
        } catch (Exception e) {
            log.error(e.toString());
        }
        writer.setCollection("issue");
        return writer;
    }

    @Bean
    public JobExecutionDecider decider() {
        return (JobExecution jobExecution, StepExecution stepExecution) -> batchExecution.equals("TITLE") ? new FlowExecutionStatus("TITLE") : new FlowExecutionStatus("ISSUE");
    }

//    @Bean
//    public Job importJob(JobBuilderFactory jobs, Step step1, JobExecutionListener listener) {
//        return jobs.get("importJob")
//                .incrementer(new RunIdIncrementer())
//                .listener(listener)
//                .flow(step1)
//                .end()
//                .build();
//    }

    @Bean
    public Job importJob(JobBuilderFactory jobs, Step step1, Step step2, Step step3) {
        return jobs
                .get("importJob")
                .incrementer(new RunIdIncrementer())
                .start(step1)
                .next(decider()).on("TITLE").to(step2)
                .from(decider()).on("ISSUE").to(step3)
                .end()
                .build();
    }

    @Bean
    public Step step1(StepBuilderFactory sbf) {
        return sbf
                .get("step1")
                .tasklet(new NotifierTasklet())
                .build();
    }

    @Bean
    public Step step2(StepBuilderFactory stepBuilderFactory,
                      ItemReader<TitleDTO> reader,
                      ItemWriter<Title> writer,
                      ItemProcessor<TitleDTO, Title> processor) {
        return stepBuilderFactory.get("step2")
                .<TitleDTO, Title> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step step3(StepBuilderFactory stepBuilderFactory,
                      ItemReader<IssueDTO> reader,
                      ItemWriter<Issue> writer,
                      ItemProcessor<IssueDTO, Issue> processor) {
        return stepBuilderFactory.get("step3")
                .<IssueDTO, Issue> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory("mongodb://localhost:27017/collector");
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        MappingMongoConverter converter = new MappingMongoConverter(mongoDatabaseFactory(), new MongoMappingContext());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDatabaseFactory(), converter);
        return mongoTemplate;

    }
}
