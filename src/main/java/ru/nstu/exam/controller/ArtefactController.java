package ru.nstu.exam.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.nstu.exam.bean.ArtefactBean;
import ru.nstu.exam.service.ArtefactService;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/artefact")
@RequiredArgsConstructor
@Tag(name = "Artefact")
public class ArtefactController {
    private final ArtefactService artefactService;


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a file")
    public ArtefactBean uploadFile(@RequestParam("file") MultipartFile file) {
        return artefactService.uploadFile(file);
    }

    @GetMapping(value = "/{artefactId}/download")
    @Operation(summary = "Download a file")
    public void downloadFile(@PathVariable Long artefactId, HttpServletResponse response) {
        artefactService.downloadFile(artefactId, response);
    }

    @GetMapping(value = "/{artefactId}/info")
    @Operation(summary = "Get info about file")
    public ArtefactBean getInfo(@PathVariable Long artefactId) {
        return artefactService.getInfo(artefactId);
    }
}
