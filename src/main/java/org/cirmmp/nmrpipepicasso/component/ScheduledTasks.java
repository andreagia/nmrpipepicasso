package org.cirmmp.nmrpipepicasso.component;

import org.cirmmp.nmrpipepicasso.service.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Stream;


@Component
@EnableAsync
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    @Autowired
    TempDir tempDirimpl;

    @Async
    @Scheduled(fixedRate = 50000)
    public void reportCurrentTime() throws IOException {
        Stream<Path> oldir = tempDirimpl.getolddir();
      //  log.info(System::toString);
        log.info("The time is now {}", dateFormat.format(new Date()));

    }
}

