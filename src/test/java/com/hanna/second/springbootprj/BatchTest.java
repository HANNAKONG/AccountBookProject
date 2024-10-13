package com.hanna.second.springbootprj;

import com.hanna.second.springbootprj.support.enums.PeriodType;
import com.hanna.second.springbootprj.support.enums.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = "spring.h2.console.enabled=true")
public class BatchTest {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job updateStatisticsJob;

    @Test
    public void testJobExecution() throws Exception {
        // 필요한 JobParameters를 생성
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("batchType", PeriodType.MONTHLY.name())
                .addString("baseDate", "20241001")
                .addLong("usersId", 1L)
                .addLong("time", System.currentTimeMillis())
                .addString("transactionType", TransactionType.EXPENSE.name())
                .toJobParameters();

        // 배치 작업을 실행
        JobExecution jobExecution = jobLauncher.run(updateStatisticsJob, jobParameters);

        // 배치가 성공적으로 완료되었는지 확인
        assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED");
    }
}