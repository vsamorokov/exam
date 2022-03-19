package ru.nstu.exam.service;

import org.springframework.stereotype.Service;
import ru.nstu.exam.bean.GroupBean;
import ru.nstu.exam.entity.Group;
import ru.nstu.exam.repository.GroupRepository;

import java.util.List;

@Service
public class GroupService extends BasePersistentService<Group, GroupBean, GroupRepository> {

    public GroupService(GroupRepository repository) {
        super(repository);
    }


    public GroupBean createGroup(GroupBean groupBean) {
        return map(save(map(groupBean)));
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
