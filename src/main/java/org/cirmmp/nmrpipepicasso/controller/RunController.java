package org.cirmmp.nmrpipepicasso.controller;

import org.cirmmp.nmrpipepicasso.message.ResponseMessage;
import org.cirmmp.nmrpipepicasso.model.Job;
import org.cirmmp.nmrpipepicasso.model.OutRunCommnad;
import org.cirmmp.nmrpipepicasso.model.PostRun;
import org.cirmmp.nmrpipepicasso.model.StatusSSE;
import org.cirmmp.nmrpipepicasso.service.FilesStorageService;
import org.cirmmp.nmrpipepicasso.service.RunCommandLocal;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Controller
public class RunController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RunController.class);
    private final ExecutorService executor =  Executors.newFixedThreadPool(10);
    @Autowired
    FilesStorageService storageService;
    @Autowired
    RunCommandLocal runCommandLocal;

    @CrossOrigin
    @PostMapping("/run")
    public ResponseEntity<ResponseMessage> runNexflow(@RequestBody @NotNull PostRun postRun)  throws Exception {
        String sessid = postRun.getDirtmp();
        LOGGER.info(postRun.toString());
        LOGGER.info("RUN- "+postRun.toString());
        LOGGER.info("sessid- "+sessid);
        LOGGER.info(sessid);
        Job job = new Job();
        String message = "Test RUN ";
        Path root = storageService.getRoot();
        Path sesspath = root.resolve(Paths.get(sessid));
        job.setDirectory(sesspath.toString());
        job.setSessid(sessid);
        CompletableFuture<OutRunCommnad> outRunCommnad = runCommandLocal.runjob(job);
        LOGGER.info(root.toString());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseMessage(message));
    }

    @CrossOrigin
    @GetMapping("/runsse")
    public SseEmitter runNexflowsse(@RequestParam("sessid") String sessid) {
        LOGGER.info(sessid);
        String message = "Test RUNSSE ";
        Path root = storageService.getRoot();
        Path sesspath = root.resolve(Paths.get(sessid));

        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        sseEmitter.onCompletion(() -> LOGGER.info("SseEmitter is completed"));

        sseEmitter.onTimeout(() -> LOGGER.info("SseEmitter is timed out"));

        sseEmitter.onError((ex) -> LOGGER.info("SseEmitter got error:", ex));
        StatusSSE statusSSE =new StatusSSE();
        statusSSE.setDir(sessid);
        statusSSE.setStatus("notfinished");
        statusSSE.setStatuscsv("CSVnot finished");
        statusSSE.setStatusroi("ROInot finished");
        executor.execute(() -> {


            for (int i = 0; i < 20; i++) {

                try {

                    if (Files.exists(sesspath.resolve("roi.jpg"))) {

                        LOGGER.info("SSE Sending ROI FINISHED"+ i+ sessid);
                        statusSSE.setStatusroi("Finished");
                       // sseEmitter.send(" Exist roi.jpg "+i+sessid);
                    }
                    if (Files.exists(sesspath.resolve("out.csv"))) {

                        LOGGER.info("SSE Sending CSV FINISHED"+ i+ sessid);
                        statusSSE.setStatuscsv("Finished");
                        // sseEmitter.send(" Exist roi.jpg "+i+sessid);

                    }
                    if (Files.exists(sesspath.resolve("roi.jpg")) && Files.exists(sesspath.resolve("out.csv"))) {
                        statusSSE.setStatus("Finished");
                    }
                    sseEmitter.send(statusSSE);


                    sleep(3, sseEmitter);
                } catch (Exception e) {
                    e.printStackTrace();
                    sseEmitter.completeWithError(e);
                }

            }
            sseEmitter.complete();
        });

        LOGGER.info("Controller exits");
        return sseEmitter;



        //LOGGER.info(root.toString());


    }
    private void sleep(int seconds, SseEmitter sseEmitter) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            sseEmitter.completeWithError(e);
        }
    }
}
