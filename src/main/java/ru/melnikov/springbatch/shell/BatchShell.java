package ru.melnikov.springbatch.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent("Backup commands")
@ShellCommandGroup("Backup commands")
@RequiredArgsConstructor
public class BatchShell {

    private final JobLauncher jobLauncher;
    private final Job saveAllBooksToDB;
    private final JobOperator jobOperator;
    private final JobExplorer jobExplorer;

    @ShellMethod(value = "backup All", key = {"backup"})
    public String backupAll() {
        try {
            JobParameters jobParameters =
                    new JobParametersBuilder()
                            .toJobParameters();


            JobExecution execution = jobLauncher.run(saveAllBooksToDB,jobParameters);
            return execution.getStatus().name();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ShellMethod(value = "get Job Status", key = {"status"})
    public String getStatus(@ShellOption(value = {"-l"}) long l) throws NoSuchJobExecutionException {

        return jobOperator.getSummary(l);
    }

    @ShellMethod(value = "get All Jobs Status", key = {"jobs"})
    public String getAllJob(){

        return jobExplorer.getJobNames().toString();
    }

    @ShellMethod(value = "restart", key = {"restart"})
    public String restartJob(@ShellOption(value = {"-l"}) long l) throws NoSuchJobExecutionException, JobParametersInvalidException, JobRestartException, JobInstanceAlreadyCompleteException, NoSuchJobException {
        Long id = jobOperator.restart(l);
        return "job id " + id;
    }

}
