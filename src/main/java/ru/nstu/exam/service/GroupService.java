package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.CreateGroupBean;
import ru.nstu.exam.bean.GroupBean;
import ru.nstu.exam.entity.Discipline;
import ru.nstu.exam.entity.Group;
import ru.nstu.exam.repository.GroupRepository;

import java.util.ArrayList;
import java.util.List;

import static ru.nstu.exam.exception.ExamException.userError;

@Service
public class GroupService extends BasePersistentService<Group, GroupBean, GroupRepository> {
    private final DisciplineService disciplineService;

    public GroupService(GroupRepository repository, DisciplineService disciplineService) {
        super(repository);
        this.disciplineService = disciplineService;
    }

    public GroupBean findOne(Long groupId) {
        Group group = findById(groupId);
        if (group == null) {
            userError("Group not found");
        }
        return map(group);
    }

    public GroupBean createGroup(CreateGroupBean groupBean) {
        List<Discipline> disciplines = getDisciplines(groupBean);

        Group group = new Group();
        group.setName(groupBean.getName());

        if (!CollectionUtils.isEmpty(disciplines)) {
            group.setDisciplines(disciplines);
        }
        return map(save(group));
    }

    public GroupBean editGroup(Long id, CreateGroupBean groupBean) {
        Group group = findById(id);
        if (group == null) {
            userError("Group not found");
        }
        List<Discipline> disciplines = getDisciplines(groupBean);

        if (groupBean.getName() == null) {
            userError("Group must have name");
        }
        group.setName(groupBean.getName());

        if (CollectionUtils.isEmpty(disciplines)) {
            userError("Group must have at least one discipline");
        }
        group.setDisciplines(disciplines);
        return map(save(group));
    }

    public List<GroupBean> findByDiscipline(Long disciplineId) {
        if (disciplineId == null) {
            return null;
        }
        Discipline discipline = disciplineService.getById(disciplineId);
        List<Group> groups = getRepository().findAllByDisciplinesContaining(discipline);
        return mapToBeans(groups);
    }

    private List<Discipline> getDisciplines(CreateGroupBean groupBean) {
        List<Long> disciplineIds = groupBean.getDisciplineIds();
        List<Discipline> disciplines = new ArrayList<>(disciplineIds.size());
        if (!CollectionUtils.isEmpty(disciplineIds)) {
            for (Long id : disciplineIds) {
                Discipline discipline = disciplineService.findById(id);
                if (discipline == null) {
                    userError("No discipline found");
                }
                disciplines.add(discipline);
            }
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

}
