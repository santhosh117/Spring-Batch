package com.project.batch.Configuration;

import com.project.batch.Model.Person;
import com.project.batch.Processor.PersonProcessor;
import com.project.batch.Reader.PersonReader;
import com.project.batch.Writer.StringHeaderWriter;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration  extends JobExecutionListenerSupport {

    @Autowired
    PersonReader personReader;

    @Autowired
    PersonProcessor processor;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean(name = "personJob")
    public Job personDetailJob() {

        Step step = stepBuilderFactory.get("step-1")
                .<Person, Person>chunk(5)
                .reader(personReader)
                .processor(processor)
                .writer(restPersonWriter())
                .build();

        Job job = jobBuilderFactory.get("person-job")
                .incrementer(new RunIdIncrementer())
                .listener(this)
                .start(step)
                .build();
        return job;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            System.out.println("BATCH JOB COMPLETED SUCCESSFULLY");
        }
    }

    @Bean
    public ItemWriter<Person> restPersonWriter() {
        FlatFileItemWriter<Person> csvFileWriter = new FlatFileItemWriter<>();
        String exportFileHeader = "ID,FIRST NAME,LAST NAME,GENDER,AGE,FULL NAME";
        StringHeaderWriter headerWriter = new StringHeaderWriter(exportFileHeader);
        csvFileWriter.setHeaderCallback(headerWriter);
        csvFileWriter.setResource(new FileSystemResource("person.csv"));
        csvFileWriter.setAppendAllowed(true);
        csvFileWriter.setLineAggregator(new DelimitedLineAggregator<Person>() {
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<Person>() {
                    {
                        setNames(new String[] { "id", "firstName", "lastName", "gender", "age", "fullName" });
                    }
                });
            }
        });
        return csvFileWriter;
    }
}