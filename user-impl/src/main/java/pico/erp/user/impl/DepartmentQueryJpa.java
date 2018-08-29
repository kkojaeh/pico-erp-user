package pico.erp.user.impl;

import static org.springframework.util.StringUtils.isEmpty;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.shared.ExtendedLabeledValue;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.Public;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.shared.jpa.QueryDslJpaSupport;
import pico.erp.user.DepartmentQuery;
import pico.erp.user.data.DepartmentView;
import pico.erp.user.impl.jpa.QDepartmentEntity;

@Service
@Public
@Transactional(readOnly = true)
@Validated
public class DepartmentQueryJpa implements DepartmentQuery {

  private final QDepartmentEntity department = QDepartmentEntity.departmentEntity;

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private QueryDslJpaSupport queryDslJpaSupport;

  @Override
  public List<? extends LabeledValuable> asLabels(String keyword, long limit) {
    val query = new JPAQuery<LabeledValue>(entityManager);
    val select = Projections.bean(ExtendedLabeledValue.class,
      department.id.value.as("value"),
      department.name.as("label"),
      department.manager.name.as("subLabel"),
      department.manager.id.value.as("stamp")
    );
    query.select(select);
    query.from(department);
    query
      .where(
        department.name.likeIgnoreCase(queryDslJpaSupport.toLikeKeyword("%", keyword, "%")));
    query.limit(limit);
    query.orderBy(department.name.asc());
    return query.fetch();
  }

  @Override
  public Page<DepartmentView> retrieve(DepartmentView.Filter filter, Pageable pageable) {
    val query = new JPAQuery<DepartmentView>(entityManager);
    val select = Projections.bean(DepartmentView.class,
      department.id,
      department.name,
      department.manager.id.as("managerId"),
      department.manager.name.as("managerName"),
      department.createdBy,
      department.createdDate,
      department.lastModifiedBy,
      department.lastModifiedDate
    );
    query.select(select);
    query.from(department);
    query.leftJoin(department.manager);

    val builder = new BooleanBuilder();

    if (!isEmpty(filter.getName())) {
      builder.and(
        department.name
          .likeIgnoreCase(queryDslJpaSupport.toLikeKeyword("%", filter.getName(), "%"))
      );
    }
    if (filter.getManagerId() != null) {
      builder.and(
        department.manager.id.eq(filter.getManagerId())
      );
    }
    query.where(builder);

    return queryDslJpaSupport.paging(query, pageable, select);
  }

}
