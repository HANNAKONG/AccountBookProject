package com.hanna.second.springbootprj.batch;

import com.hanna.second.springbootprj.support.WeekNumberConverter;
import com.hanna.second.springbootprj.support.enums.PeriodType;
import com.hanna.second.springbootprj.support.enums.TransactionType;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class BatchJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job updateStatisticsJob;

    public BatchJobScheduler(JobLauncher jobLauncher, Job updateStatisticsJob) {
        this.jobLauncher = jobLauncher;
        this.updateStatisticsJob = updateStatisticsJob;
    }

    @Scheduled(cron = "0 0 1 ? * MON") // 매주 월요일 새벽 1시에 실행
    public void runWeeklyJob() throws Exception {

        PeriodType batchType = PeriodType.WEEKLY;

        // 지난 주의 월요일을 가져옴
        LocalDate lastWeekMonday = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY);
        // 주 번호 체계에 맞춘 baseDate 생성
        String baseDate = WeekNumberConverter.convertToWeekNumber(lastWeekMonday.format(DateTimeFormatter.BASIC_ISO_DATE));

        Long usersId = 1L;

        TransactionType transactionType = TransactionType.EXPENSE;

        JobParameters jobParameters = createJobParameters(batchType, baseDate, usersId, transactionType);

        jobLauncher.run(updateStatisticsJob, jobParameters);

    }

    @Scheduled(cron = "0 0 1 1 * ?") // 매달 1일 새벽 1시에 실행
    public void runMonthlyJob() throws Exception {

        PeriodType batchType = PeriodType.MONTHLY;

        LocalDate lastMonthFirstDay = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        String baseDate = lastMonthFirstDay.format(DateTimeFormatter.BASIC_ISO_DATE);

        Long usersId = 1L;

        TransactionType transactionType = TransactionType.EXPENSE;

        JobParameters jobParameters = createJobParameters(batchType, baseDate, usersId, transactionType);

        jobLauncher.run(updateStatisticsJob, jobParameters);


    }

    public JobParameters createJobParameters(PeriodType batchType, String baseDate, Long usersId, TransactionType transactionType) {
        return new JobParametersBuilder()
                .addString("batchType", batchType.name())
                .addString("baseDate", baseDate)
                .addLong("usersId", 1L)
                .addLong("time", System.currentTimeMillis())
                .addString("transactionType", transactionType.name())
                .toJobParameters();
    }
}