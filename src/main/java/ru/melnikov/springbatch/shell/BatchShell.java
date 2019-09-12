package ru.melnikov.springbatch.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
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
import org.springframework.shell.table.BorderStyle;
import org.springframework.shell.table.Table;
import org.springframework.shell.table.TableBuilder;
import org.springframework.shell.table.TableModelBuilder;

import java.util.ArrayList;
import java.util.List;

@ShellComponent("Backup commands")
@ShellCommandGroup("Backup commands")
@RequiredArgsConstructor
public class BatchShell {

    private final JobLauncher jobLauncher;
    private final Job saveAllBooksToDB;
    private final JobOperator jobOperator;
    private final JobExplorer jobExplorer;
    private final JobRegistry jobRegistry;

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

    @ShellMethod(value = "get Execution detail", key = {"detail"})
    public String getStatus(@ShellOption(value = {"-l"}) long l) throws NoSuchJobExecutionException {

        return jobOperator.getSummary(l);
    }

    @ShellMethod(value = "get All Jobs Status", key = {"status"})
    public Table getAllJob(){
        List<String> jobs=jobExplorer.getJobNames();
        List<JobInstance> instances=new ArrayList<>();
        jobs.forEach(job->instances.addAll(jobExplorer.findJobInstancesByJobName(job,0,100)));
        List<JobExecution> executions = new ArrayList<>();
        instances.forEach(instance->executions.addAll(jobExplorer.getJobExecutions(instance)));

        return getExecutionsTable(executions);
    }

    @ShellMethod(value = "restart", key = {"restart"})
    public String restartJob(@ShellOption(value = {"-l"}) long l) throws NoSuchJobExecutionException, JobParametersInvalidException, JobRestartException, JobInstanceAlreadyCompleteException, NoSuchJobException {
        Long id = jobOperator.restart(l);
        JobExecution restartExecution = jobExplorer.getJobExecution(id);
        return "job id " + restartExecution.getId();
    }


    private Table getExecutionsTable(List<JobExecution> executions) {
        TableModelBuilder<Object> modelBuilder = new TableModelBuilder<>();
        modelBuilder.addRow()
                .addValue("Execution Id")
                .addValue("Job Id")
                .addValue("Instance Id")
                .addValue("Status")
                .addValue("Start time")
                .addValue("End time");
        executions.forEach(e -> {
                    modelBuilder.addRow()
                            .addValue(e.getId())
                            .addValue(e.getJobId())
                            .addValue(e.getJobInstance().getInstanceId())
                            .addValue(e.getStatus())
                            .addValue(e.getStartTime())
                            .addValue(e.getEndTime());
                }
        );

        TableBuilder builder = new TableBuilder(modelBuilder.build());

        return builder.addFullBorder(BorderStyle.fancy_double).build();
    }

}
