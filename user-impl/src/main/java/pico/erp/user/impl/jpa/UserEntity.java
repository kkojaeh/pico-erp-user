package pico.erp.user.impl.jpa;


import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.data.Auditor;
import pico.erp.shared.jpa.CrytoAttributeConverter;
import pico.erp.user.data.RoleId;
import pico.erp.user.data.UserId;

@Entity(name = "User")
@Table(name = "USR_USER", indexes = {
  @Index(name = "USR_USER_EMAIL_IDX", columnList = "EMAIL", unique = true)
})
@Data
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  boolean accountNonExpired;

  boolean accountNonLocked;

  boolean credentialsNonExpired;

  boolean enabled;

  /**
   * 패스워드
   */
  /*
  @Column(updatable = false, length = TypeDefinitions.PASSWORD_ENCODED_LENGTH)
  String password;
  */

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "CREATED_BY_ID", updatable = false, length = TypeDefinitions.ID_LENGTH)),
    @AttributeOverride(name = "name", column = @Column(name = "CREATED_BY_NAME", updatable = false, length = TypeDefinitions.NAME_LENGTH))
  })
  @CreatedBy
  Auditor createdBy;

  @CreatedDate
  @Column(updatable = false)
  OffsetDateTime createdDate;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "LAST_MODIFIED_BY_ID", length = TypeDefinitions.ID_LENGTH)),
    @AttributeOverride(name = "name", column = @Column(name = "LAST_MODIFIED_BY_NAME", length = TypeDefinitions.NAME_LENGTH))
  })
  @LastModifiedBy
  Auditor lastModifiedBy;

  @LastModifiedDate
  OffsetDateTime lastModifiedDate;

  @EmbeddedId
  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "ID", length = TypeDefinitions.ID_LENGTH))
  })
  UserId id;

  @Column(length = TypeDefinitions.ID_LENGTH)
  String name;

  @Column(length = TypeDefinitions.TITLE_LENGTH)
  String position;

  @Column(length = TypeDefinitions.EMAIL_LENGTH * 2)
  @Convert(converter = CrytoAttributeConverter.class)
  String email;

  @Column(length = TypeDefinitions.PHONE_NUMBER_LENGTH * 2)
  @Convert(converter = CrytoAttributeConverter.class)
  String mobilePhoneNumber;

  @Builder.Default
  @ManyToMany
  @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
  @JoinTable(
    name = "USR_USER_GROUP",
    joinColumns = {@JoinColumn(name = "USER_ID")},
    inverseJoinColumns = {@JoinColumn(name = "GROUP_ID")}
  )
  Set<GroupEntity> groups = new HashSet<>();

  @Builder.Default
  @ElementCollection(fetch = FetchType.LAZY)
  @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "ROLE_ID", length = TypeDefinitions.ID_LENGTH, nullable = false))
  })
  @CollectionTable(name = "USR_USER_ROLE", joinColumns = @JoinColumn(name = "USER_ID"), uniqueConstraints = {
    @UniqueConstraint(name = "USR_USER_ROLE_UC", columnNames = {"USER_ID", "ROLE_ID"})
  })
  @OrderColumn
  Set<RoleId> roles = new HashSet<>();

  @ManyToOne
  @JoinColumn(name = "DEPARTMENT_ID")
  DepartmentEntity department;

}
