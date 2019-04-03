package pico.erp.user

import kkojaeh.spring.boot.component.SpringBootTestComponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.shared.ComponentDefinitionServiceLoaderTestComponentSiblingsSupplier
import pico.erp.shared.TestParentApplication
import pico.erp.user.department.DepartmentId
import pico.erp.user.role.RoleExceptions
import pico.erp.user.role.RoleId
import spock.lang.Specification

@SpringBootTest(classes = [UserApplication])
@SpringBootTestComponent(parent = TestParentApplication, siblingsSupplier = ComponentDefinitionServiceLoaderTestComponentSiblingsSupplier.class)
@Transactional
@Rollback
@ActiveProfiles("test")
@Configuration
class UserServiceSpec extends Specification {

  @Lazy
  @Autowired
  UserService userService

  def id = UserId.from("test")

  def unknownId = UserId.from("unknown")

  def name = "테스터"

  def password = "Passw0rd!"

  def mobilePhoneNumber = "+821011111112"

  def departmentId = DepartmentId.from("management")

  def position = "사원"

  def email = "email@email.com"

  def roleId = RoleId.from(UserApi.Roles.USER_MANAGER.getId())

  def setup() {
    userService.create(
      new UserRequests.CreateRequest(
        id: id,
        name: name,
        email: email,
        password: password,
        position: position,
        mobilePhoneNumber: mobilePhoneNumber,
        departmentId: departmentId
      )
    )
  }

  def "존재 - 아이디로 확인"() {
    when:
    def exists = userService.exists(id)

    then:
    exists == true
  }

  def "auditor - auditor 전환"() {
    when:
    def user = userService.get(id)
    def auditor = userService.getAuditor(id)

    then:
    user.id.value == auditor.id
    user.name == auditor.name
  }

  def "존재 - 존재하지 않는 아이디로 확인"() {
    when:
    def exists = userService.exists(unknownId)

    then:
    exists == false
  }

  def "조회 - 아이디로 조회"() {
    when:
    def user = userService.get(id)

    then:

    user.id == id
    user.name == name
  }

  def "조회 - 존재하지 않는 아이디로 조회"() {
    when:
    userService.get(unknownId)

    then:
    thrown(UserExceptions.NotFoundException)
  }

  def "패스워드 - 룰에 맞지 않는 사용자 생성"() {
    when:

    userService.create(
      new UserRequests.CreateRequest(
        id: UserId.from("kjh2"),
        password: "password",// 짧은 패스워드
        email: "kjh2@kd-ace.co.kr",
        name: "고재훈",
        mobilePhoneNumber: "+821000000000"
      )
    )

    then:
    thrown(UserExceptions.PasswordInvalidException)
  }

  def "이메일 - 동일한 이메일로 사용자 생성"() {
    when:
    userService.create(
      new UserRequests.CreateRequest(
        id: UserId.from("kjh2"),
        password: "psdkljfs132!@",
        email: email,
        name: "고재훈2",
        mobilePhoneNumber: "+821000000000"
      )
    )

    then:
    thrown(UserExceptions.EmailAlreadyExistsException)
  }

  def "권한 부여 - 권한 부여"() {
    when:
    userService.grantRole(
      new UserRequests.GrantRoleRequest(id: id, roleId: roleId)
    )

    then:
    userService.hasRole(id, roleId) == true
  }

  def "권한 해제 - 부여한 권한을 해제"() {
    when:
    userService.grantRole(
      new UserRequests.GrantRoleRequest(id: id, roleId: roleId)
    )
    userService.revokeRole(
      new UserRequests.RevokeRoleRequest(id: id, roleId: roleId)
    )

    then:
    userService.hasRole(id, roleId) == false
  }

  def "권한 부여 - 중복된 권한 부여"() {
    when:
    userService.grantRole(
      new UserRequests.GrantRoleRequest(id: id, roleId: roleId)
    )
    userService.grantRole(
      new UserRequests.GrantRoleRequest(id: id, roleId: roleId)
    )

    then:
    thrown(RoleExceptions.AlreadyExistsException)
  }

  def "권한 해제 - 부여되지 않은 권한을 해제"() {
    when:
    userService.revokeRole(
      new UserRequests.RevokeRoleRequest(id: id, roleId: roleId)
    )

    then:
    thrown(RoleExceptions.NotFoundException)
  }
}
