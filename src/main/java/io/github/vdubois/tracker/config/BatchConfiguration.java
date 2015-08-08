package io.github.vdubois.tracker.config;

import io.github.vdubois.tracker.service.batch.tasklet.RefreshPricesTasklet;
import io.github.vdubois.tracker.service.batch.tasklet.SendAlertsTasklet;
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
    public Job refreshPricesJob(JobBuilderFactory jobs, Step refreshPricesStep) {
        return jobs.get("refreshPricesJob").incrementer(new RunIdIncrementer()).flow(refreshPricesStep).end().build();
    }

    @Bean
    public Job sendPricesAlertsJob(JobBuilderFactory jobs, Step sendAlertsStep) {
        return jobs.get("sendPricesAlertsJob").incrementer(new RunIdIncrementer()).flow(sendAlertsStep).end().build();
    }

    @Bean
    public Step refreshPricesStep(StepBuilderFactory stepBuilderFactory, Tasklet refreshPricesTasklet) {
        return stepBuilderFactory.get("step").tasklet(refreshPricesTasklet).build();
    }

    @Bean
    public Step sendAlertsStep(StepBuilderFactory stepBuilderFactory, Tasklet sendAlertsTasklet) {
        return stepBuilderFactory.get("step").tasklet(sendAlertsTasklet).build();
    }

    @Bean
    public Tasklet refreshPricesTasklet() {
        return new RefreshPricesTasklet();
    }

    @Bean
    public Tasklet sendAlertsTasklet() {
        return new SendAlertsTasklet();
    }
}
