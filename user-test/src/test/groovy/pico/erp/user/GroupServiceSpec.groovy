package pico.erp.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.shared.IntegrationConfiguration
import pico.erp.user.data.GroupId
import pico.erp.user.data.RoleId
import pico.erp.user.data.UserId
import pico.erp.user.group.GroupExceptions
import pico.erp.user.group.GroupRequests
import pico.erp.user.group.GroupService
import pico.erp.user.role.RoleExceptions
import spock.lang.Specification

@SpringBootTest(classes = [IntegrationConfiguration])
@Transactional
@Rollback
@ActiveProfiles("test")
class GroupServiceSpec extends Specification {

  @Autowired
  GroupService groupService

  @Autowired
  UserService userService

  def setup() {
    groupService.create(new GroupRequests.CreateRequest(id: GroupId.from("sa"), name: "슈퍼어드민"))
  }

  def "존재하는 그룹을 확인"() {
    when:
    def exists = groupService.exists(GroupId.from("sa"))

    then:
    exists == true
  }

  def "존재하지 않는 그룹을 확인"() {
    when:
    def exists = groupService.exists(GroupId.from("!sa"))

    then:
    exists == false
  }

  def "존재하는 그룹 조회"() {
    when:
    def group = groupService.get(GroupId.from("sa"))

    then:

    group.id.value == "sa"
    group.name == "슈퍼어드민"
  }

  def "존재하지 않는 그룹 조회"() {
    when:
    groupService.get(GroupId.from("!sa"))

    then:
    thrown(GroupExceptions.NotFoundException)
  }

  def "권한을 부여하고 확인"() {
    when:
    groupService.grantRole(new GroupRequests.GrantRoleRequest(id: GroupId.from("sa"), roleId: RoleId.from(ROLE.USER_MANAGER.getId())))

    then:
    groupService.hasRole(GroupId.from("sa"), RoleId.from(ROLE.USER_MANAGER.getId())) == true
  }

  def "제거한 권한을 확인"() {
    when:
    groupService.grantRole(new GroupRequests.GrantRoleRequest(id: GroupId.from("sa"), roleId: RoleId.from(ROLE.USER_MANAGER.getId())))
    groupService.revokeRole(new GroupRequests.RevokeRoleRequest(id: GroupId.from("sa"), roleId: RoleId.from(ROLE.USER_MANAGER.getId())))

    then:
    groupService.hasRole(GroupId.from("sa"), RoleId.from(ROLE.USER_MANAGER.getId())) == false
  }

  def "기존에 존재하던 권한을 부여"() {
    when:
    groupService.grantRole(new GroupRequests.GrantRoleRequest(id: GroupId.from("sa"), roleId: RoleId.from(ROLE.USER_MANAGER.getId())))
    groupService.grantRole(new GroupRequests.GrantRoleRequest(id: GroupId.from("sa"), roleId: RoleId.from(ROLE.USER_MANAGER.getId())))

    then:
    thrown(RoleExceptions.AlreadyExistsException)
  }

  def "부여되지 않았던 권한을 해제"() {
    when:
    groupService.revokeRole(new GroupRequests.RevokeRoleRequest(id: GroupId.from("sa"), roleId: RoleId.from(ROLE.USER_MANAGER.getId())))

    then:
    thrown(RoleExceptions.NotFoundException)
  }


  def "사용자를 추가하고 확인"() {
    when:
    groupService.addUser(new GroupRequests.AddUserRequest(id: GroupId.from("sa"), userId: UserId.from("kjh")))

    then:
    groupService.hasUser(GroupId.from("sa"), UserId.from("kjh")) == true
  }

  def "제거한 사용자를 확인"() {
    when:
    groupService.addUser(new GroupRequests.AddUserRequest(id: GroupId.from("sa"), userId: UserId.from("kjh")))
    groupService.removeUser(new GroupRequests.RemoveUserRequest(id: GroupId.from("sa"), userId: UserId.from("kjh")))

    then:
    groupService.hasUser(GroupId.from("sa"), UserId.from("kjh")) == false
  }

  def "기존에 존재하던 사용자를 추가"() {
    when:
    groupService.addUser(new GroupRequests.AddUserRequest(id: GroupId.from("sa"), userId: UserId.from("kjh")))
    groupService.addUser(new GroupRequests.AddUserRequest(id: GroupId.from("sa"), userId: UserId.from("kjh")))

    then:
    thrown(UserExceptions.GroupAlreadyExistsException)
  }

  def "존재하지 않았던 사용자를 제거"() {
    when:
    groupService.removeUser(new GroupRequests.RemoveUserRequest(id: GroupId.from("sa"), userId: UserId.from("kjh")))

    then:
    thrown(UserExceptions.GroupNotFoundException)
  }
}
