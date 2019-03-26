package pico.erp.user.department;

import static org.springframework.util.StringUtils.isEmpty;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import kkojaeh.spring.boot.component.Give;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.shared.ExtendedLabeledValue;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.shared.jpa.QueryDslJpaSupport;
import pico.erp.user.QUserEntity;

@Service
@Give
@Transactional(readOnly = true)
@Validated
public class DepartmentQueryJpa implements DepartmentQuery {

  private final QDepartmentEntity department = QDepartmentEntity.departmentEntity;

  private final QUserEntity user = QUserEntity.userEntity;

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
      user.name.as("subLabel"),
      user.id.value.as("stamp")
    );
    query.select(select);
    query.from(department);
    query.leftJoin(user)
      .on(department.managerId.eq(user.id));
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
      user.id.as("managerId"),
      user.name.as("managerName"),
      department.createdBy,
      department.createdDate,
      department.lastModifiedBy,
      department.lastModifiedDate
    );
    query.select(select);
    query.from(department);
    query.leftJoin(user)
      .on(department.managerId.eq(user.id));

    val builder = new BooleanBuilder();

    if (!isEmpty(filter.getName())) {
      builder.and(
        department.name
          .likeIgnoreCase(queryDslJpaSupport.toLikeKeyword("%", filter.getName(), "%"))
      );
    }
    if (filter.getManagerId() != null) {
      builder.and(
        department.managerId.eq(filter.getManagerId())
      );
    }
    query.where(builder);

    return queryDslJpaSupport.paging(query, pageable, select);
  }

}
