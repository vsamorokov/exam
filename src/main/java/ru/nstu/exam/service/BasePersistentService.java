package ru.nstu.exam.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.nstu.exam.bean.EntityBean;
import ru.nstu.exam.entity.PersistableEntity;
import ru.nstu.exam.repository.PersistableEntityRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public abstract class BasePersistentService<T extends PersistableEntity, B extends EntityBean, R extends PersistableEntityRepository<T>> {

    @Getter
    private final R repository;

    @Transactional
    public T save(T entity) {
        return repository.save(entity);
    }

    @Transactional
    public void delete(T entity) {
        entity.setDeleted(true);
        repository.save(entity);
    }

    @Transactional
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional
    public List<B> findAll() {
        return repository.findAll().stream().map(this::map).collect(Collectors.toList());
    }

    @Transactional
    public T findById(Long id) {
        if(id == null) {
            return null;
        }
        return repository.findById(id).orElse(null);
    }

    @Transactional
    public T getById(Long id) {
        if(id == null) {
            return null;
        }
        return repository.getById(id);
    }

    protected abstract B map(T entity);
    protected abstract T map(B bean);

    protected List<B> mapToBeans(List<T> entities){
        return entities.stream().map(this::map).collect(Collectors.toList());
    }
}
