package ru.nstu.exam.entity.convert;

import ru.nstu.exam.entity.utils.RatingMapping;
import ru.nstu.exam.enums.AnswerStatus;
import ru.nstu.exam.enums.TaskType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class RatingMappingStringConverter implements AttributeConverter<List<RatingMapping>, String> {

    @Override
    public String convertToDatabaseColumn(List<RatingMapping> ratingMappings) {
        StringBuilder sb = new StringBuilder();

        ratingMappings.forEach(rm -> sb
                .append(rm.getTaskType().name()).append(",")
                .append(rm.getRating()).append(",")
                .append(rm.getStatus()).append(";")
        );

        return sb.toString();
    }

    @Override
    public List<RatingMapping> convertToEntityAttribute(String str) {
        List<RatingMapping> mappings = new ArrayList<>();

        String[] entries = str.split(";");

        for (String entryStr : entries) {
            String[] entry = entryStr.split(",");
            mappings.add(RatingMapping.builder()
                    .taskType(TaskType.valueOf(entry[0]))
                    .rating(Integer.parseInt(entry[1]))
                    .status(AnswerStatus.valueOf(entry[2]))
                    .build()
            );
        }
        return mappings;
    }
}
