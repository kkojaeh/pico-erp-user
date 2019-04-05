package pico.erp.user.group;


import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
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
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pico.erp.shared.TypeDefinitions;
import pico.erp.shared.data.Auditor;
import pico.erp.user.role.RoleId;

@Entity(name = "Group")
@Table(name = "USR_GROUP")
@Data
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners({AuditingEntityListener.class})
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "CREATED_BY_ID", updatable = false, length = TypeDefinitions.ID_LENGTH)),
    @AttributeOverride(name = "name", column = @Column(name = "CREATED_BY_NAME", updatable = false, length = TypeDefinitions.NAME_LENGTH))
  })
  @CreatedBy
  Auditor createdBy;

  @Column(updatable = false)
  OffsetDateTime createdDate;

  @Embedded
  @AttributeOverrides({
    @AttributeOverride(name = "id", column = @Column(name = "LAST_MODIFIED_BY_ID", length = TypeDefinitions.ID_LENGTH)),
    @AttributeOverride(name = "name", column = @Column(name = "LAST_MODIFIED_BY_NAME", length = TypeDefinitions.NAME_LENGTH))
  })
  @LastModifiedBy
  Auditor lastModifiedBy;

  OffsetDateTime lastModifiedDate;

  @Id
  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "ID", length = TypeDefinitions.ID_LENGTH))
  })
  GroupId id;

  @Column(length = 30)
  String name;

  @Builder.Default
  @ElementCollection(fetch = FetchType.LAZY)
  @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
  @AttributeOverrides({
    @AttributeOverride(name = "value", column = @Column(name = "ROLE_ID", length = TypeDefinitions.ID_LENGTH, nullable = false))
  })
  @CollectionTable(name = "USR_GROUP_ROLE", joinColumns = @JoinColumn(name = "GROUP_ID"), uniqueConstraints = {
    @UniqueConstraint(columnNames = {"GROUP_ID", "ROLE_ID"})
  })
  @OrderColumn
  Set<RoleId> roles = new HashSet<>();

  @PrePersist
  private void onCreate() {
    createdDate = OffsetDateTime.now();
    lastModifiedDate = OffsetDateTime.now();
  }

  @PreUpdate
  private void onUpdate() {
    lastModifiedDate = OffsetDateTime.now();
  }

}
