package com.hanna.second.springbootprj.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final StatisticsBatchTasklet statisticsBatchTasklet;

    public BatchConfig(JobBuilderFactory jobBuilderFactory,
                       StepBuilderFactory stepBuilderFactory,
                       StatisticsBatchTasklet statisticsBatchTasklet) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.statisticsBatchTasklet = statisticsBatchTasklet;
    }

    @Bean
    public Job updateStatisticsJob() {
        return jobBuilderFactory.get("updateStatisticsJob")
                .start(updateStatisticsStep())
                .build();
    }

    @Bean
    public Step updateStatisticsStep() {
        return stepBuilderFactory.get("updateStatisticsStep")
                .tasklet(statisticsBatchTasklet)
                .build();
    }
}
