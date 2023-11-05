package xyz.krakenkat.batchprocessing;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;
import xyz.krakenkat.batchprocessing.dto.IssueDTO;
import xyz.krakenkat.batchprocessing.dto.TitleDTO;
import xyz.krakenkat.batchprocessing.model.mongo.Issue;
import xyz.krakenkat.batchprocessing.model.mongo.Title;
import xyz.krakenkat.batchprocessing.processor.IssueProcessor;
import xyz.krakenkat.batchprocessing.processor.TitleProcessor;
import xyz.krakenkat.batchprocessing.reader.IssueReader;
import xyz.krakenkat.batchprocessing.util.Constants;
import xyz.krakenkat.batchprocessing.writer.IssueWriter;
import xyz.krakenkat.batchprocessing.writer.TitleWriter;


@Slf4j
@Configuration
@NoArgsConstructor
public class BatchConfiguration {

    @Value("${batch-execution.file-name}")
    private String csvFile;

    @Bean
    public FlatFileItemReader<TitleDTO> titleReader() {
        System.out.println(csvFile);
        return new FlatFileItemReaderBuilder<TitleDTO>()
                .name("titleReader")
                .resource(new ClassPathResource(csvFile + ".csv"))
                .delimited()
                .delimiter(Constants.DELIMITER)
                .names(Constants.TITLES_HEADER)
<<<<<<< HEAD
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
=======
                .fieldSetMapper(new BeanWrapperFieldSetMapper<TitleDTO>() {{
>>>>>>> f6139b48ec7d3bf6e2c8828f9c2dd60036cca289
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
    public Step titleStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("titleStep", jobRepository)
                .<TitleDTO, Title> chunk(1, transactionManager)
                .reader(titleReader())
                .processor(titleProcessor())
                .writer(titleWriter())
                .listener(promotionListener())
                .build();
//        return stepBuilderFactory
//                .get("titleStep")
//                .<TitleDTO, Title> chunk(1)
//                .reader(titleReader())
//                .processor(titleProcessor())
//                .writer(titleWriter())
//                .listener(promotionListener())
//                .build();
    }

    @Bean
    public Step issueStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("issueStep", jobRepository)
                .<IssueDTO, Issue> chunk(1, transactionManager)
                .reader(issueReader())
                .processor(issueItemProcessor())
                .writer(issueWriter())
                .build();
//        return stepBuilderFactory
//                .get("issueStep")
//                .<IssueDTO, Issue> chunk(1)
//                .reader(issueReader())
//                .processor(issueItemProcessor())
//                .writer(issueWriter())
//                .build();
    }

    @Bean
    public Job job(JobRepository jobRepository, Step titleStep, Step issueStep) {
        return new JobBuilder("importJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(titleStep)
                .next(issueStep)
                .end()
                .build();
//        return jobBuilderFactory
//                .get("importJob")
//                .start(titleStep())
//                .next(issueStep())
//                .build();
    }

    @Bean
    public ExecutionContextPromotionListener promotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[] {"transient"});
        listener.setStrict(true);
        return listener;
    }
}
