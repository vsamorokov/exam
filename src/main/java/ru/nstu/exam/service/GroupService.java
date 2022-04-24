package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import liquibase.repackaged.org.apache.commons.collections4.ListUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.CreateGroupBean;
import ru.nstu.exam.bean.FullGroupBean;
import ru.nstu.exam.bean.GroupBean;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.Exam;
import ru.nstu.exam.entity.Group;
import ru.nstu.exam.entity.Student;
import ru.nstu.exam.repository.GroupRepository;
import ru.nstu.exam.service.mapper.FullGroupMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.nstu.exam.exception.ExamException.userError;

@Service
public class GroupService extends BasePersistentService<Group, GroupBean, GroupRepository> {
    private final DisciplineService disciplineService;
    private final FullGroupMapper groupMapper;
    private final ExamService examService;
    private final StudentService studentService;

    public GroupService(GroupRepository repository, DisciplineService disciplineService, FullGroupMapper groupMapper, @Lazy ExamService examService, @Lazy StudentService studentService) {
        super(repository);
        this.disciplineService = disciplineService;
        this.groupMapper = groupMapper;
        this.examService = examService;
        this.studentService = studentService;
    }

    public FullGroupBean findOne(Long groupId, int level) {
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

    public GroupBean editGroup(Long id, GroupBean groupBean) {
        Group group = findById(id);
        if (group == null) {
            userError("Group not found");
        }
        checkGroup(groupBean);
        group.setName(groupBean.getName());
        return map(save(group));
    }

    private void checkGroup(GroupBean groupBean) {
        String name = groupBean.getName();
        if (name == null) {
            userError("Group must have name");
        }
        Long countByName = getRepository().countByName(name);
        if (countByName > 0) {
            userError("Group with name (" + name + ") already exists");
        }
    }

    public List<GroupBean> findByDiscipline(Long disciplineId) {
        if (disciplineId == null) {
            return null;
        }
        Discipline discipline = disciplineService.getById(disciplineId);
        List<Group> groups = getRepository().findAllByDisciplinesContaining(discipline);
        return mapToBeans(groups);
    }

    public void delete(Long id) {
        Group group = findById(id);
        if (group == null) {
            userError("Group not found");
        }
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

    private List<Discipline> getDisciplines(CreateGroupBean groupBean) {
        Collection<Long> disciplineIds = CollectionUtils.emptyIfNull(groupBean.getDisciplineIds());
        List<Discipline> disciplines = new ArrayList<>(disciplineIds.size());
        for (Long id : disciplineIds) {
            Discipline discipline = disciplineService.findById(id);
            if (discipline == null) {
                userError("No discipline found");
            }
            disciplines.add(discipline);
        }
        return disciplines;
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

    public void addDisciplines(Long groupId, List<Long> disciplineIds) {
        Group group = findById(groupId);
        if (group == null) {
            userError("Group not found");
        }
        if (disciplineIds == null) {
            userError("Disciplines list must not be null");
        }

        List<Discipline> original = group.getDisciplines();
        Set<Long> originalIds = ListUtils.emptyIfNull(original)
                .stream()
                .map(AbstractPersistable::getId)
                .collect(Collectors.toSet());

        List<Discipline> toAdd = new ArrayList<>(disciplineIds.size());
        for (Long id : disciplineIds) {
            if (originalIds.contains(id)) {
                continue;
            }
            Discipline discipline = disciplineService.findById(id);
            if (discipline == null) {
                userError("Discipline with id " + id + " not found");
            }
            toAdd.add(discipline);
        }
        if (original == null) {
            group.setDisciplines(toAdd);
        } else {
            original.addAll(toAdd);
        }
        save(group);
    }

    public void removeDisciplines(Long groupId, List<Long> disciplineIds) {
        Group group = findById(groupId);
        if (group == null) {
            userError("Group not found");
        }
        if (disciplineIds == null) {
            userError("Disciplines list must not be null");
        }

        List<Discipline> original = group.getDisciplines();
        if (CollectionUtils.isEmpty(original)) {
            return;
        }
        Set<Long> originalIds = original.stream()
                .map(AbstractPersistable::getId)
                .collect(Collectors.toSet());

        List<Discipline> toRemove = new ArrayList<>(disciplineIds.size());
        for (Long id : disciplineIds) {
            if (!originalIds.contains(id)) {
                continue;
            }
            Discipline discipline = disciplineService.findById(id);
            if (discipline == null) {
                userError("Discipline with id " + id + " not found");
            }
            toRemove.add(discipline);
        }
        original.removeAll(toRemove);
        save(group);
    }
}
