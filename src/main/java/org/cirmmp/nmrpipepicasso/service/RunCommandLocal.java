package org.cirmmp.nmrpipepicasso.service;




import org.cirmmp.nmrpipepicasso.model.Job;
import org.cirmmp.nmrpipepicasso.model.OutRunCommnad;

import java.util.concurrent.CompletableFuture;

public interface RunCommandLocal {
    CompletableFuture<OutRunCommnad> runjob(Job job) throws Exception;
}
