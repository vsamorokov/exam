package ru.nstu.exam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.nstu.exam.bean.ArtefactBean;
import ru.nstu.exam.entity.Artefact;
import ru.nstu.exam.entity.ArtefactType;
import ru.nstu.exam.repository.ArtefactRepository;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneOffset.UTC;
import static ru.nstu.exam.exception.ExamException.serverError;
import static ru.nstu.exam.exception.ExamException.userError;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArtefactService {
    private final ArtefactRepository artefactRepository;

    @Value("${file.max-size-bytes:2097152}") // 2mb
    private long maxFileSize;

    @Value("${file.local-dir:data/files}")
    private String localDirectory;

    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS");

    @PostConstruct
    private void init() {
        File dir = new File(localDirectory);
        if (dir.isFile()) {
            serverError(localDirectory + " is a file");
        }
        if (!dir.exists() && !dir.mkdirs()) {
            serverError("Unable to create files directory");
        }
    }

    public ArtefactBean uploadFile(MultipartFile file) {
        if (file == null) {
            userError("File must not be null");
        }
        if (file.getSize() > maxFileSize) {
            userError("File size is too big (max is " + maxFileSize * 1.0 / (1024 * 1024) + " MB)");
        }
        if (!StringUtils.hasText(file.getOriginalFilename())) {
            userError("File name must not be empty");
        }
        ArtefactType artefactType = getArtefactType(file);
        if (artefactType == null) {
            userError("Unknown file type");
        }
        Artefact artefact = new Artefact();
        artefact.setFileSize(file.getSize());
        artefact.setFileName(file.getOriginalFilename());
        artefact.setArtefactType(artefactType);
        artefact.setLocalName(generateLocalName(file.getOriginalFilename(), artefactType.getExtension()));
        Artefact saved = artefactRepository.save(artefact);

        File fileToSave = new File(saved.getLocalName());

        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), fileToSave);
        } catch (IOException e) {
            delete(artefact, false);
            userError("File upload error");
        }
        ArtefactBean artefactBean = new ArtefactBean();
        artefactBean.setId(saved.getId());
        artefactBean.setArtefactType(saved.getArtefactType());
        artefactBean.setFileName(saved.getFileName());
        artefactBean.setFileSize(saved.getFileSize());
        return artefactBean;
    }

    private String generateLocalName(String originalName, String extension) {

        File dir = new File(localDirectory, extension);
        if (!dir.mkdirs()) {
            serverError("Cannot create local directory");
        }

        String fileName = originalName + "_" + LocalDateTime.now(UTC).format(timeFormat) + "." + extension;

        return new File(dir, fileName).getAbsolutePath();
    }

    private ArtefactType getArtefactType(MultipartFile file) {
        if (file.getOriginalFilename() == null) {
            return null;
        }
        String[] parts = file.getOriginalFilename().split("\\.");
        String ext = parts[parts.length - 1];
        return ArtefactType.getExtToType().get(ext);
    }

    public void downloadFile(Long artefactId, HttpServletResponse response) {
        Artefact artefact = artefactRepository
                .findById(artefactId)
                .orElseGet(() -> userError("Artefact not found"));

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + artefact.getFileName());
        response.setContentLengthLong(artefact.getFileSize());
        try (
                FileInputStream inputStream = new FileInputStream(artefact.getLocalName());
                ServletOutputStream outputStream = response.getOutputStream()
        ) {
            IOUtils.copyLarge(inputStream, outputStream);
            outputStream.flush();
        } catch (IOException e) {
            serverError("Error during file send", e);
        }
    }

    public ArtefactBean getInfo(Long artefactId) {
        Artefact artefact = artefactRepository.findById(artefactId).orElseGet(() -> userError("Artefact not found"));
        ArtefactBean artefactBean = new ArtefactBean();
        artefactBean.setId(artefact.getId());
        artefactBean.setArtefactType(artefact.getArtefactType());
        artefactBean.setFileSize(artefact.getFileSize());
        artefactBean.setFileName(artefact.getFileName());
        return artefactBean;
    }

    public Artefact getArtefact(Long id) {
        return artefactRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        Artefact artefact = artefactRepository.findById(id).orElseGet(() -> userError("Artefact not found"));
        delete(artefact);
    }

    public void delete(Artefact artefact) {
        delete(artefact, true);
    }

    private void delete(Artefact artefact, boolean withFile) {
        if (withFile && !FileUtils.deleteQuietly(new File(artefact.getLocalName()))) {
            log.error("File: " + artefact.getLocalName() + " was not deleted");
        }
        artefactRepository.delete(artefact);
    }
}
