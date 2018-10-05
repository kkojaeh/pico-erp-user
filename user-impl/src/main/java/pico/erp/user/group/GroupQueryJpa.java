package pico.erp.user.group;

import static org.springframework.util.StringUtils.isEmpty;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
import pico.erp.shared.LabeledValue;
import pico.erp.shared.Public;
import pico.erp.shared.data.LabeledValuable;
import pico.erp.shared.data.Role;
import pico.erp.shared.jpa.QueryDslJpaSupport;
import pico.erp.user.QUserEntity;
import pico.erp.user.role.RoleId;
import pico.erp.user.role.RoleRepository;

@Service
@Public
@Transactional(readOnly = true)
@Validated
public class GroupQueryJpa implements GroupQuery {

  private final QGroupEntity group = QGroupEntity.groupEntity;

  private final QUserEntity user = QUserEntity.userEntity;

  @PersistenceContext
  private EntityManager entityManager;

  @Autowired
  private QueryDslJpaSupport queryDslJpaSupport;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private MessageSource messageSource;

  @SuppressWarnings("Duplicates")
  @Override
  public List<? extends LabeledValuable> asLabels(String keyword, long limit) {
    val query = new JPAQuery<LabeledValue>(entityManager);
    val select = Projections.bean(LabeledValue.class,
      group.id.as("value"),
      group.name.as("label")
    );
    query.select(select);
    query.from(group);
    query.where(group.name.toLowerCase()
      .likeIgnoreCase(queryDslJpaSupport.toLikeKeyword("%", keyword, "%")));
    query.orderBy(group.name.asc());
    query.limit(limit);
    return query.fetch();
  }

  @Override
  public List<GroupJoinedUserView> findAllGroupJoinedUser(GroupId groupId) {
    val groupEntity = entityManager.find(GroupEntity.class, groupId);
    if (groupEntity == null) {
      return Collections.emptyList();
    }
    val query = new JPAQuery<GroupJoinedUserView>(entityManager);

    val select = Projections.bean(GroupJoinedUserView.class,
      Expressions.as(Expressions.constant(groupId), "groupId"),
      user.id.as("userId"),
      user.name.as("userName")
    );
    query.select(select);
    query.from(user);
    query.where(user.groups.contains(groupEntity));
    return query.fetch();
  }

  @Override
  public List<GroupRoleGrantedOrNotView> findAllGroupRoleGrantedOrNot(GroupId groupId) {
    val roleIds = new HashSet<String>();
    Optional.ofNullable(groupId)
      .map(id -> groupRepository.findBy(id).orElse(null))
      .ifPresent(group -> roleIds.addAll(group.getRoles().stream()
        .map(Role::getId)
        .collect(Collectors.toSet())));

    val locale = LocaleContextHolder.getLocale();
    return roleRepository.findAll()
      .map(role -> GroupRoleGrantedOrNotView.builder()
        .groupId(groupId)
        .roleId(RoleId.from(role.getId()))
        .roleName(messageSource.getMessage(role.getNameCode(), null, role.getNameCode(), locale))
        .roleDescription(messageSource
          .getMessage(role.getDescriptionCode(), null, role.getDescriptionCode(), locale))
        .granted(roleIds.contains(role.getId()))
        .build()
      ).collect(Collectors.toList());

  }

  @Override
  public Page<GroupView> retrieve(GroupView.Filter filter, Pageable pageable) {

    val query = new JPAQuery<GroupView>(entityManager);
    val select = Projections.bean(GroupView.class,
      group.id,
      group.name,
      group.createdBy,
      group.createdDate,
      group.lastModifiedBy,
      group.lastModifiedDate
    );

    query.select(select);

    query.from(group);

    val builder = new BooleanBuilder();

    if (!isEmpty(filter.getName())) {
      builder.and(group.name
        .likeIgnoreCase(queryDslJpaSupport.toLikeKeyword("%", filter.getName(), "%")));
    }
    query.where(builder);

    return queryDslJpaSupport.paging(query, pageable, select);
  }
}
