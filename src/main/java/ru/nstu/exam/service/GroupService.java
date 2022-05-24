package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.GroupBean;
import ru.nstu.exam.bean.full.FullGroupBean;
import ru.nstu.exam.entity.Exam;
import ru.nstu.exam.entity.Group;
import ru.nstu.exam.entity.Student;
import ru.nstu.exam.repository.GroupRepository;
import ru.nstu.exam.service.mapper.FullGroupMapper;

import static ru.nstu.exam.exception.ExamException.userError;
import static ru.nstu.exam.utils.Utils.*;

@Service
public class GroupService extends BasePersistentService<Group, GroupBean, GroupRepository> {
    private final FullGroupMapper groupMapper;
    private final ExamService examService;
    private final StudentService studentService;

    public GroupService(GroupRepository repository, FullGroupMapper groupMapper, @Lazy ExamService examService, @Lazy StudentService studentService) {
        super(repository);
        this.groupMapper = groupMapper;
        this.examService = examService;
        this.studentService = studentService;
    }

    public GroupBean findOne(Long groupId) {
        Group group = findById(groupId);
        if (group == null) {
            userError("Group not found");
        }
        return map(group);
    }

    public FullGroupBean findFull(Long groupId, int level) {
        Group group = findById(groupId);
        if (group == null) {
            userError("Group not found");
        }
        return groupMapper.map(group, level);
    }

    public GroupBean createGroup(GroupBean groupBean) {
        Group group = new Group();
        checkGroup(groupBean);
        group.setName(groupBean.getName());
        return map(save(group));
    }

    public GroupBean editGroup(GroupBean bean) {
        checkGroup(bean);
        Group group = findById(bean.getId());
        checkNotNull(group, "Group not found");
        group.setName(bean.getName());
        return map(save(group));
    }

    private void checkGroup(GroupBean groupBean) {
        String name = groupBean.getName();
        checkNotEmpty(name, "Group must have name");
        checkFalse(getRepository().countByName(name) > 0, "Group with name (" + name + ") already exists");
    }

    public void delete(Long id) {
        Group group = findById(id);
        checkNotNull(group, "Group not found");

        delete(group);
    }

    @Override
    public void delete(Group group) {
        for (Exam exam : CollectionUtils.emptyIfNull(group.getExams())) {
            examService.delete(exam);
        }
        for (Student student : CollectionUtils.emptyIfNull(group.getStudents())) {
            studentService.delete(student);
        }
        super.delete(group);
    }

    @Override
    protected GroupBean map(Group entity) {
        GroupBean groupBean = new GroupBean();
        groupBean.setId(entity.getId());
        groupBean.setName(entity.getName());
        return groupBean;
    }

    @Override
    protected Group map(GroupBean bean) {
        Group group = new Group();
        group.setName(bean.getName());
        return group;
    }

}
