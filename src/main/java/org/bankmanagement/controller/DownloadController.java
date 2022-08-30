package org.bankmanagement.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bankmanagement.aspect.log.LogMethod;
import org.bankmanagement.kafka.DownloadSchedulerProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;

@RestController
@RequiredArgsConstructor
public class DownloadController {

    private final DownloadSchedulerProducer downloadSchedulerProducer;

    @SneakyThrows
    @LogMethod
    @PostMapping("/download")
    public void requestDownload(@RequestParam String url) {
        downloadSchedulerProducer.sendDownloadRequest(new URL(url));
    }
}
