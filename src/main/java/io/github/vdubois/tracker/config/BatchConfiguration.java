package io.github.vdubois.tracker.config;

import io.github.vdubois.tracker.service.batch.tasklet.RefreshPricesTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by vdubois on 18/07/15.
 */
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Bean
    public Job refreshPricesJob(JobBuilderFactory jobs, Step step) {
        return jobs.get("refreshPricesJob").incrementer(new RunIdIncrementer()).flow(step).end().build();
    }

    @Bean
    public Step step(StepBuilderFactory stepBuilderFactory, Tasklet tasklet) {
        return stepBuilderFactory.get("step").tasklet(tasklet).build();
    }

    @Bean
    public Tasklet refreshPricesTasklet() {
        return new RefreshPricesTasklet();
    }
}
