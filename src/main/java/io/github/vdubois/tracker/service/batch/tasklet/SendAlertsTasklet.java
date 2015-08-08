package io.github.vdubois.tracker.service.batch.tasklet;

import io.github.vdubois.tracker.service.MailService;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import javax.inject.Inject;

/**
 * Created by vdubois on 01/08/15.
 */
public class SendAlertsTasklet implements Tasklet {

    @Inject
    private MailService mailService;

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        mailService.sendPriceAlerts();
        return RepeatStatus.FINISHED;
    }
}
