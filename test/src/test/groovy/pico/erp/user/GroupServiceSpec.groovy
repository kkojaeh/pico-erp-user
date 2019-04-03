package pico.erp.user

import kkojaeh.spring.boot.component.SpringBootTestComponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.shared.ComponentDefinitionServiceLoaderTestComponentSiblingsSupplier
import pico.erp.shared.TestParentApplication
import pico.erp.user.group.GroupExceptions
import pico.erp.user.group.GroupId
import pico.erp.user.group.GroupRequests
import pico.erp.user.group.GroupService
import pico.erp.user.role.RoleExceptions
import pico.erp.user.role.RoleId
import spock.lang.Specification

@SpringBootTest(classes = [UserApplication])
@SpringBootTestComponent(parent = TestParentApplication, siblingsSupplier = ComponentDefinitionServiceLoaderTestComponentSiblingsSupplier.class)
@Transactional
@Rollback
@ActiveProfiles("test")
class GroupServiceSpec extends Specification {

  @Autowired
  GroupService groupService

  def id = GroupId.from("sa")

  def unknownId = GroupId.from("unknown")

  def name = "슈퍼어드민"

  def roleId = RoleId.from(UserApi.Roles.USER_MANAGER.getId())

  def userId = UserId.from("kjh")

  def setup() {
    groupService.create(
      new GroupRequests.CreateRequest(
        id: id,
        name: name
      )
    )
  }

  def "존재 - 아이디로 확인"() {
    when:
    def exists = groupService.exists(id)

    then:
    exists == true
  }

  def "존재 - 존재하지 않는 아이디로 확인"() {
    when:
    def exists = groupService.exists(unknownId)

    then:
    exists == false
  }

  def "조회 - 아이디로 조회"() {
    when:
    def group = groupService.get(id)

    then:

    group.id == id
    group.name == name
  }

  def "조회 - 존재하지 않는 아이디로 조회"() {
    when:
    groupService.get(unknownId)

    then:
    thrown(GroupExceptions.NotFoundException)
  }

  def "권한부여 - 권한 부여"() {
    when:
    groupService.grantRole(
      new GroupRequests.GrantRoleRequest(
        id: id,
        roleId: roleId
      )
    )

    then:
    groupService.hasRole(id, roleId)
  }

  def "권한부여 - 중복 권한 부여"() {
    when:
    groupService.grantRole(
      new GroupRequests.GrantRoleRequest(
        id: id,
        roleId: roleId
      )
    )
    groupService.grantRole(
      new GroupRequests.GrantRoleRequest(
        id: id,
        roleId: roleId
      )
    )

    then:
    thrown(RoleExceptions.AlreadyExistsException)
  }

  def "권한해제 - 부여한 권한을 해제"() {
    when:
    groupService.grantRole(
      new GroupRequests.GrantRoleRequest(
        id: id,
        roleId: roleId
      )
    )
    groupService.revokeRole(
      new GroupRequests.RevokeRoleRequest(
        id: id,
        roleId: roleId
      )
    )
    then:
    groupService.hasRole(GroupId.from("sa"), RoleId.from(UserApi.Roles.USER_MANAGER.getId())) == false
  }

  def "권한해제 - 부여되지 않았던 권한을 해제"() {
    when:
    groupService.revokeRole(
      new GroupRequests.RevokeRoleRequest(
        id: id,
        roleId: roleId
      )
    )

    then:
    thrown(RoleExceptions.NotFoundException)
  }


  def "사용자 추가 - 사용자 추가하"() {
    when:
    groupService.addUser(
      new GroupRequests.AddUserRequest(
        id: id,
        userId: userId
      )
    )

    then:
    groupService.hasUser(id, userId) == true
  }

  def "사용자 제거 - 추가한 사용자를 제거"() {
    when:
    groupService.addUser(
      new GroupRequests.AddUserRequest(
        id: id,
        userId: userId
      )
    )
    groupService.removeUser(
      new GroupRequests.RemoveUserRequest(
        id: id,
        userId: userId
      )
    )

    then:
    groupService.hasUser(id, userId) == false
  }

  def "사용자 추가 - 중복된 사용자를 추가"() {
    when:
    groupService.addUser(
      new GroupRequests.AddUserRequest(
        id: id,
        userId: userId
      )
    )
    groupService.addUser(
      new GroupRequests.AddUserRequest(
        id: id,
        userId: userId
      )
    )

    then:
    thrown(UserExceptions.GroupAlreadyExistsException)
  }

  def "사용자 제거 - 추가되지 않았던 사용자를 제거"() {
    when:
    groupService.removeUser(
      new GroupRequests.RemoveUserRequest(
        id: id,
        userId: userId
      )
    )

    then:
    thrown(UserExceptions.GroupNotFoundException)
  }
}
