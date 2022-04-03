package ru.nstu.exam.service;

import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.DisciplineBean;
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


    public GroupBean createGroup(GroupBean groupBean) {
        List<DisciplineBean> disciplineBeans = groupBean.getDisciplines();
        List<Discipline> disciplines = new ArrayList<>();
        if (!CollectionUtils.isEmpty(disciplineBeans)) {
            for (DisciplineBean disciplineBean : disciplineBeans) {
                if (disciplineBean.getId() == null) {
                    userError("Discipline must have an id");
                }
                Discipline discipline = disciplineService.findById(disciplineBean.getId());
                if (discipline == null) {
                    userError("No discipline found");
                }
                disciplines.add(discipline);
            }
        }

        Group group = map(groupBean);

        if (!CollectionUtils.isEmpty(disciplines)) {
            group.setDisciplines(disciplines);
        }
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
