package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.CreateRatingSystemBean;
import ru.nstu.exam.bean.RatingSystemBean;
import ru.nstu.exam.entity.RatingSystem;
import ru.nstu.exam.entity.utils.RatingMapping;
import ru.nstu.exam.enums.AnswerStatus;
import ru.nstu.exam.repository.RatingSystemRepository;

import java.util.ArrayList;
import java.util.List;

import static ru.nstu.exam.exception.ExamException.userError;


@Service
public class RatingSystemService extends BasePersistentService<RatingSystem, RatingSystemBean, RatingSystemRepository> {

    public RatingSystemService(RatingSystemRepository repository) {
        super(repository);
    }

    public RatingSystemBean getOne(Long id) {
        RatingSystem ratingSystem = findById(id);
        if (ratingSystem == null) {
            userError("Rating system not found");
        }
        return map(ratingSystem);
    }

    public RatingSystemBean create(CreateRatingSystemBean bean) {
        if (bean.getName() == null) {
            userError("Name must not be null");
        }
        if (CollectionUtils.isEmpty(bean.getApprovedRatingsForQuestion())) {
            userError("Approved ratings for questions must have at least one element");
        }
        if (CollectionUtils.isEmpty(bean.getRejectedRatingsForQuestion())) {
            userError("Rejected ratings for questions must have at least one element");
        }
        if (CollectionUtils.isEmpty(bean.getApprovedRatingsForExercise())) {
            userError("Approved ratings for exercises must have at least one element");
        }
        if (CollectionUtils.isEmpty(bean.getRejectedRatingsForExercise())) {
            userError("Rejected ratings for exercises must have at least one element");
        }
        RatingSystem ratingSystem = new RatingSystem();
        ratingSystem.setName(bean.getName());
        List<RatingMapping> ratingMappings = new ArrayList<>();
        for (Integer approvedRating : bean.getApprovedRatingsForQuestion()) {
            ratingMappings.add(RatingMapping.builder()
                    .rating(approvedRating)
                    .status(AnswerStatus.APPROVED)
                    .build()
            );
        }
        for (Integer rejectedRating : bean.getRejectedRatingsForQuestion()) {
            ratingMappings.add(RatingMapping.builder()
                    .rating(rejectedRating)
                    .status(AnswerStatus.REJECTED)
                    .build()
            );
        }
        for (Integer approvedRating : bean.getApprovedRatingsForExercise()) {
            ratingMappings.add(RatingMapping.builder()
                    .rating(approvedRating)
                    .status(AnswerStatus.APPROVED)
                    .build()
            );
        }
        for (Integer rejectedRating : bean.getRejectedRatingsForExercise()) {
            ratingMappings.add(RatingMapping.builder()
                    .rating(rejectedRating)
                    .status(AnswerStatus.REJECTED)
                    .build()
            );
        }
        ratingSystem.setRatingMappings(ratingMappings);
        return map(save(ratingSystem));
    }

    @Override
    protected RatingSystemBean map(RatingSystem entity) {
        RatingSystemBean bean = new RatingSystemBean();
        bean.setId(entity.getId());
        bean.setName(entity.getName());
        bean.setRatingMapping(entity.getRatingMap());
        return bean;
    }

    @Override
    protected RatingSystem map(RatingSystemBean bean) {
        return null;
    }
}
