package io.github.vdubois.tracker.service.batch.tasklet;

import io.github.vdubois.tracker.domain.ProductToTrack;
import io.github.vdubois.tracker.repository.ProductToTrackRepository;
import io.github.vdubois.tracker.service.PriceService;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by vdubois on 18/07/15.
 */
public class RefreshPricesTasklet implements Tasklet {
    
    @Inject
    private ProductToTrackRepository productToTrackRepository;

    @Inject
    private PriceService priceService;
    
    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        List<ProductToTrack> productsToTrack = productToTrackRepository.findAll();
        for (ProductToTrack productToTrack : productsToTrack) {
            productToTrack.setLastKnownPrice(priceService.extractPriceFromURLWithDOMSelectorIfFilled(productToTrack));
            priceService.recordPriceForProductToTrack(productToTrack); 
        }
        return RepeatStatus.FINISHED;
    }

}
