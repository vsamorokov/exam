package ru.nstu.exam.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;
import ru.nstu.exam.entity.convert.RatingMappingStringConverter;
import ru.nstu.exam.entity.utils.RatingMapping;
import ru.nstu.exam.enums.AnswerStatus;
import ru.nstu.exam.enums.TaskType;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.nstu.exam.exception.ExamException.serverError;

@Data
@Entity
@Table(name = "rating_system")
@EqualsAndHashCode(callSuper = true)
@Where(clause = "deleted = false")
public class RatingSystem extends PersistableEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Convert(converter = RatingMappingStringConverter.class)
    @Column(name = "rating_mappings", nullable = false, columnDefinition = "string", length = 2048)
    private List<RatingMapping> ratingMappings;

    @OneToMany(mappedBy = "ratingSystem")
    private List<ExamRule> examRules;

    public Map<TaskType, Map<Integer, AnswerStatus>> getRatingMap() {
        Map<TaskType, Map<Integer, AnswerStatus>> map = new HashMap<>();
        for (RatingMapping mapping : ratingMappings) {
            if (map
                    .computeIfAbsent(mapping.getTaskType(), k -> new HashMap<>())
                    .put(mapping.getRating(), mapping.getStatus()) != null) {
                serverError("Duplicate rating mapping");
            }

        }
        return map;
    }
}
