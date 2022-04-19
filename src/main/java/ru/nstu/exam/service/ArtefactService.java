package ru.nstu.exam.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
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
import java.util.UUID;

import static ru.nstu.exam.exception.ExamException.serverError;
import static ru.nstu.exam.exception.ExamException.userError;

@Service
@RequiredArgsConstructor
public class ArtefactService {
    private final ArtefactRepository artefactRepository;

    @Value("${file.max-size-bytes:2097152}") // 2mb
    private long maxFileSize;

    @Value("${file.local-dir:data/files}")
    private String localDirectory;

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
        artefact.setLocalName(generateLocalName(artefactType.getExtension()));
        Artefact saved = artefactRepository.save(artefact);

        File fileToSave = new File(saved.getLocalName());

        try {
            FileUtils.copyInputStreamToFile(file.getInputStream(), fileToSave);
        } catch (IOException e) {
            userError("File upload error");
        }
        ArtefactBean artefactBean = new ArtefactBean();
        artefactBean.setId(saved.getId());
        artefactBean.setArtefactType(saved.getArtefactType());
        artefactBean.setFileName(saved.getFileName());
        artefactBean.setFileSize(saved.getFileSize());
        return artefactBean;
    }

    private String generateLocalName(String extension) {
        return new File(localDirectory, UUID.randomUUID().toString() + "." + extension).getAbsolutePath();
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
        BeanUtils.copyProperties(artefact, artefactBean);
        return artefactBean;
    }

    public Artefact getArtefact(Long id) {
        return artefactRepository.findById(id).orElse(null);
    }

}
