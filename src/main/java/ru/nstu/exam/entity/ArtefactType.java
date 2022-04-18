package ru.nstu.exam.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public enum ArtefactType {
    PNG("png"),
    JPG("jpg"),
    JPEG("jpeg"),
    XML("xml"),
    DOC("doc"),
    DOCX("docx"),
    PDF("pdf");

    @Getter
    private final String extension;

    @Getter
    private final static Map<ArtefactType, String> typeToExt = new HashMap<>();
    @Getter
    private final static Map<String, ArtefactType> extToType = new HashMap<>();

    static {
        for (ArtefactType value : values()) {
            typeToExt.put(value, value.extension);
            extToType.put(value.extension, value);
        }
    }

}
