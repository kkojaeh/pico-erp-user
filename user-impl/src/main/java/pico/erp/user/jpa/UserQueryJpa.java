package pico.erp.user.jpa;

import static org.springframework.util.StringUtils.isEmpty;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.shared.ExtendedLabeledValue;
import pico.erp.shared.LabeledValue;
import pico.erp.shared.Public;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.shared.data.Role;
import pico.erp.shared.jpa.QueryDslJpaSupport;
import pico.erp.user.User;
import pico.erp.user.UserQuery;
import pico.erp.user.UserRepository;
import pico.erp.user.data.RoleId;
import pico.erp.user.data.UserId;
import pico.erp.user.data.UserRoleGrantedOrNotView;
import pico.erp.user.data.UserView;
import pico.erp.user.role.RoleRepository;

@Service
@Public
@Transactional(readOnly = true)
@Validated
public class UserQueryJpa implements UserQuery {

  private final QUserEntity user = QUserEntity.userEntity;

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private QueryDslJpaSupport queryDslJpaSupport;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private RoleRepository roleRepository;

  @SuppressWarnings("Duplicates")
  @Override
  public List<? extends LabeledValuable> asLabels(String keyword, long limit) {
    val query = new JPAQuery<LabeledValue>(entityManager);
    val select = Projections.bean(ExtendedLabeledValue.class,
      user.id.value.as("value"),
      user.name.as("label"),
      user.email.as("subLabel"),
      user.position.as("stamp")
    );
    query.select(select);
    query.from(user);
    query.where(user.name
      .likeIgnoreCase(queryDslJpaSupport.toLikeKeyword("%", keyword, "%"))
      .and(user.enabled.eq(true)));
    query.orderBy(user.name.asc());
    query.limit(limit);
    return query.fetch();
  }

  @Override
  public List<UserRoleGrantedOrNotView> findAllUserRoleGrantedOrNot(UserId userId) {
    User user = Optional.ofNullable(userId)
      .map(id -> userRepository.findBy(id)
        .orElse(null)
      )
      .orElse(null);
    Set<String> roleIds = new HashSet<>();

    if (user != null) {
      roleIds.addAll(
        user.getRoles()
          .stream()
          .map(Role::getId)
          .collect(Collectors.toSet())
      );
    }

    Locale locale = LocaleContextHolder.getLocale();

    return roleRepository.findAll()
      .map(role -> UserRoleGrantedOrNotView.builder()
        .userId(userId)
        .roleId(RoleId.from(role.getId()))
        .roleName(messageSource.getMessage(role.getNameCode(), null, role.getNameCode(), locale))
        .roleDescription(messageSource
          .getMessage(role.getDescriptionCode(), null, role.getDescriptionCode(), locale))
        .granted(roleIds.contains(role.getId()))
        .build()
      ).collect(Collectors.toList());
  }

  @Override
  public Page<UserView> retrieve(UserView.Filter filter, Pageable pageable) {

    val query = new JPAQuery<UserView>(entityManager);
    val select = Projections.bean(UserView.class,
      user.id,
      user.name,
      user.position,
      user.email,
      user.enabled,
      user.mobilePhoneNumber,
      user.department.id.as("departmentId"),
      user.department.name.as("departmentName"),
      user.createdBy,
      user.createdDate,
      user.lastModifiedBy,
      user.lastModifiedDate
    );

    query.select(select);

    val builder = new BooleanBuilder();

    if (!isEmpty(filter.getName())) {
      builder.and(user.name
        .likeIgnoreCase(queryDslJpaSupport.toLikeKeyword("%", filter.getName(), "%")));
    }
    if (filter.getDepartmentId() != null) {
      builder.and(user.department.id.eq(filter.getDepartmentId()));
    }
    if (filter.getEnabled() != null) {
      builder.and(user.enabled.eq(filter.getEnabled()));
    }
    query.from(user);
    query.leftJoin(user.department);
    query.where(builder);

    return queryDslJpaSupport.paging(query, pageable, select);
  }


}
