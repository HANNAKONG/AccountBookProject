package com.hanna.second.springbootprj.statistics.batch;

import com.hanna.second.springbootprj.statistics.service.StatisticsService;
import com.hanna.second.springbootprj.support.enums.PeriodType;
import com.hanna.second.springbootprj.support.enums.TransactionType;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
public class StatisticsBatchTasklet implements Tasklet {

    private final StatisticsService statisticsService;

    public StatisticsBatchTasklet(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        try {
            String batchType = (String) chunkContext.getStepContext().getJobParameters().get("batchType");
            String baseDate = (String) chunkContext.getStepContext().getJobParameters().get("baseDate");
            Long usersId = (Long) chunkContext.getStepContext().getJobParameters().get("usersId");
            //TransactionType transactionType = (TransactionType) chunkContext.getStepContext().getJobParameters().get("transactionType");
            String transactionTypeString = chunkContext.getStepContext().getStepExecution().getJobParameters().getString("transactionType");
            TransactionType transactionType = TransactionType.valueOf(transactionTypeString);

            if (PeriodType.WEEKLY.name().equals(batchType)) {
                // 주별 집계
                statisticsService.updateWeeklyStatistics(baseDate, usersId, transactionType);
            } else if (PeriodType.MONTHLY.name().equals(batchType)) {
                // 월별 집계
                statisticsService.updateMonthlyStatistics(baseDate, usersId, transactionType);
            } else {
                throw new IllegalArgumentException("Invalid batch type: " + batchType);
            }

            return RepeatStatus.FINISHED;
        } catch (Exception e) {
            throw new JobExecutionException("Job failed", e);
        }

    }
}
