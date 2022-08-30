package org.cirmmp.nmrpipepicasso.service;



import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.cirmmp.nmrpipepicasso.model.Job;
import org.cirmmp.nmrpipepicasso.model.OutRunCommnad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RunCommandLocalImpl implements RunCommandLocal{

    Logger logger = LoggerFactory.getLogger(RunCommandLocalImpl.class);

    @Value("${nextflow.bin}")
    private String nextflowbin;
    @Value("${java.home}")
    private String javahome;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    private RunCommand runCommand;

    @Override
    @Async("asyncExecutor")
    public CompletableFuture<OutRunCommnad> runjob(Job job) throws Exception {

        logger.info("Sono in runcommandlocal");
        Resource nextflows = resourceLoader.getResource("classpath:config/fullWFcollect.nf");
        try (InputStream inputStream = nextflows.getInputStream()) {
            Files.copy(inputStream,Paths.get(job.getDirectory()+"/fullWFcollect.nf"), StandardCopyOption.REPLACE_EXISTING);
            IOUtils.closeQuietly(inputStream);
        }
        Resource nextflowsc = resourceLoader.getResource("classpath:config/nextflow.config");
        try (InputStream inputStream = nextflowsc.getInputStream()) {
            Files.copy(inputStream,Paths.get(job.getDirectory()+"/nextflow.config"), StandardCopyOption.REPLACE_EXISTING);
            IOUtils.closeQuietly(inputStream);
        }
        Resource nextflowscp = resourceLoader.getResource("classpath:config/convert.py");
        try (InputStream inputStream = nextflowscp.getInputStream()) {
            Files.copy(inputStream,Paths.get(job.getDirectory()+"/convert.py"), StandardCopyOption.REPLACE_EXISTING);
            IOUtils.closeQuietly(inputStream);
        }

        logger.info(job.getDirectory());

        //instert tag to nextflow
        //Path nextflowt = Paths.get(job.getDirectory()+"/tutorial.nf.t");
        /*Path nextflow = Paths.get(job.getDirectory()+"/"+job.getExec());
        try (Stream<String> lines = Files.lines(nextflow)) {
            List<String> replaced = lines
                    .map(line-> line.replaceAll("#REPLACETAG#", job.getTag()))
                    .collect(Collectors.toList());
            Files.write(nextflow, replaced);
        }*/
        //List<String> cmdexe = Arrays.asList(nextflowbin, "-q", "-bg", "run tutorial.nf", "-with-weblog http://localhost:8080");
        List<String> cmdexe = Arrays.asList(nextflowbin, "-q", "-bg", "run", "fullWFcollect.nf", "-with-report");
        logger.info(cmdexe.stream().reduce("",(a,b) -> a.concat(b).concat(" ")));
        Map<String,String> env = Collections.EMPTY_MAP;
        //env.put("JAVA_HOME",javahome);
        //env.put("PATH", "$JAVA_HOME/bin;$PATH");
        logger.info("JOB started");
        OutRunCommnad outRunCommnad = runCommand.run(cmdexe, env, new File(job.getDirectory()));
        return CompletableFuture.completedFuture(outRunCommnad);
    }
}
