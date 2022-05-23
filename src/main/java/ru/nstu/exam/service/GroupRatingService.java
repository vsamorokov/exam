package ru.nstu.exam.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.GroupRatingBean;
import ru.nstu.exam.bean.full.FullGroupRatingBean;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.ExamRule;
import ru.nstu.exam.entity.Group;
import ru.nstu.exam.entity.GroupRating;
import ru.nstu.exam.repository.GroupRatingRepository;
import ru.nstu.exam.service.mapper.FullGroupRatingMapper;

import static ru.nstu.exam.enums.StudentRatingState.EMPTY;
import static ru.nstu.exam.enums.StudentRatingState.NOT_ALLOWED;
import static ru.nstu.exam.utils.Utils.checkNotNull;
import static ru.nstu.exam.utils.Utils.checkTrue;

@Slf4j
@Service
public class GroupRatingService extends BasePersistentService<GroupRating, GroupRatingBean, GroupRatingRepository> {

    private final DisciplineService disciplineService;
    private final GroupService groupService;
    private final ExamRuleService examRuleService;
    private final StudentRatingService studentRatingService;
    private final FullGroupRatingMapper groupRatingMapper;

    public GroupRatingService(GroupRatingRepository repository, DisciplineService disciplineService, GroupService groupService, ExamRuleService examRuleService, StudentRatingService studentRatingService, FullGroupRatingMapper groupRatingMapper) {
        super(repository);
        this.disciplineService = disciplineService;
        this.groupService = groupService;
        this.examRuleService = examRuleService;
        this.studentRatingService = studentRatingService;
        this.groupRatingMapper = groupRatingMapper;
    }

    public GroupRatingBean findOne(Long id) {
        GroupRating groupRating = findById(id);
        checkNotNull(groupRating, "Group rating not found");
        return map(groupRating);
    }

    public FullGroupRatingBean findFull(Long id, int level) {
        GroupRating groupRating = findById(id);
        checkNotNull(groupRating, "Group rating not found");
        return groupRatingMapper.map(groupRating, level);
    }

    public GroupRatingBean create(GroupRatingBean bean) {
        GroupRating groupRating = new GroupRating();
        fill(groupRating, bean);
        GroupRating saved = save(groupRating);
        try {
            studentRatingService.create(saved);
        } catch (Exception e) {
            delete(saved);
            throw e;
        }
        return map(saved);
    }

    public GroupRatingBean update(GroupRatingBean bean) {
        GroupRating groupRating = findById(bean.getId());
        checkNotNull(groupRating, "Group rating with id " + bean.getId() + " not found");
        fill(groupRating, bean);
        return map(save(groupRating));
    }

    private void fill(GroupRating groupRating, GroupRatingBean bean) {
        checkNotNull(bean.getName(), "Group rating name cannot be null");
        Discipline discipline = disciplineService.findById(bean.getDisciplineId());
        checkNotNull(discipline, "Discipline with id " + bean.getDisciplineId() + " not found");

        Group group = groupService.findById(bean.getGroupId());
        checkNotNull(discipline, "Group with id " + bean.getGroupId() + " not found");

        ExamRule examRule = examRuleService.findById(bean.getExamRuleId());
        checkNotNull(discipline, "Exam rule with id " + bean.getExamRuleId() + " not found");

        groupRating.setName(bean.getName());
        groupRating.setDiscipline(discipline);
        groupRating.setGroup(group);
        groupRating.setExamRule(examRule);
    }

    public void delete(Long id) {
        GroupRating groupRating = findById(id);
        checkNotNull(groupRating, "Group rating not found");
        delete(groupRating);
    }

    @Override
    public void delete(GroupRating entity) {
        checkTrue(entity.getStudentRatings().stream().allMatch(r -> r.getStudentRatingState().in(EMPTY, NOT_ALLOWED)), "Wrong rating state");

        entity.getStudentRatings().forEach(studentRatingService::delete);

        super.delete(entity);
    }

    @Override
    protected GroupRatingBean map(GroupRating entity) {
        GroupRatingBean bean = new GroupRatingBean();
        bean.setId(entity.getId());
        bean.setName(entity.getName());
        bean.setGroupId(entity.getGroup().getId());
        bean.setDisciplineId(entity.getDiscipline().getId());
        bean.setExamRuleId(entity.getExamRule().getId());
        return bean;
    }

    public GroupRating find(Discipline discipline, Group group) {
        return getRepository().findByDisciplineAndGroup(discipline, group);
    }

}
