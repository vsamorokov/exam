package ru.nstu.exam.service.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import ru.nstu.exam.bean.AccountBean;
import ru.nstu.exam.bean.StudentBean;
import ru.nstu.exam.entity.Account;
import ru.nstu.exam.entity.Student;

@Component
@RequiredArgsConstructor
public class StudentMapper implements Mapper<StudentBean, Student> {

    @Override
    public StudentBean map(Student entity, int level) {
        StudentBean studentBean = new StudentBean();
        studentBean.setId(entity.getId());
        studentBean.setStatus(entity.getStatus());
        AccountBean accountBean = new AccountBean();
        Account account = entity.getAccount();
        if (account != null) {
            accountBean.setId(account.getId());
            BeanUtils.copyProperties(account, accountBean, "password");
        }
        studentBean.setAccount(accountBean);

        return studentBean;
    }
}
