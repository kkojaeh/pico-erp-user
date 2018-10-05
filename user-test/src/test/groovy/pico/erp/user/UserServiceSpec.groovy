package pico.erp.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.shared.IntegrationConfiguration
import pico.erp.user.department.DepartmentId
import pico.erp.user.department.DepartmentRequests
import pico.erp.user.department.DepartmentService
import pico.erp.user.role.RoleExceptions
import pico.erp.user.role.RoleId
import spock.lang.Specification

@SpringBootTest(classes = [IntegrationConfiguration])
@Transactional
@Rollback
@ActiveProfiles("test")
@Configuration
@ComponentScan("pico.erp.config")
class UserServiceSpec extends Specification {

  @Lazy
  @Autowired
  UserService userService

  @Lazy
  @Autowired
  DepartmentService departmentService

  def setup() {
    departmentService.create(new DepartmentRequests.CreateRequest(id: DepartmentId.from("BIZ"), name: "영업부"))
  }

  def "존재하는 사용자가 확인"() {
    when:
    def exists = userService.exists(UserId.from("kjh"))

    then:
    exists == true
  }

  def "존재하지 않는 사용자가 확인"() {
    when:
    def exists = userService.exists(UserId.from("!kjh"))

    then:
    exists == false
  }

  def "존재하는 사용자 조회"() {
    when:
    def user = userService.get(UserId.from("kjh"))

    then:

    user.id.value == "kjh"
    user.name == "고재훈"
  }

  def "존재하지 않는 사용자 조회"() {
    when:
    userService.get(UserId.from("!kjh"))

    then:
    thrown(UserExceptions.NotFoundException)
  }

  def "패스워드 룰에 맞지 않는 사용자 생성"() {
    when:
    // 짧은 패스워드
    userService.create(new UserRequests.CreateRequest(id: UserId.from("kjh2"), password: "password", email: "kjh2@kd-ace.co.kr", name: "고재훈", mobilePhoneNumber: "+821000000000"))

    then:
    thrown(UserExceptions.PasswordInvalidException)
  }

  def "기존 사용자의 이메일과 동일한 이메일로 사용자 생성"() {
    when:
    userService.create(new UserRequests.CreateRequest(id: UserId.from("kjh2"), password: "psdkljfs132!@", email: "kjh@kd-ace.co.kr", name: "고재훈2", mobilePhoneNumber: "+821000000000"))

    then:
    thrown(UserExceptions.EmailAlreadyExistsException)
  }

  def "권한을 부여하고 확인"() {
    when:
    userService.grantRole(new UserRequests.GrantRoleRequest(id: UserId.from('kjh'), roleId: RoleId.from(UserRoles.USER_MANAGER.getId())))

    then:
    userService.hasRole(UserId.from('kjh'), RoleId.from(UserRoles.USER_MANAGER.getId())) == true
  }

  def "제거한 권한을 확인"() {
    when:
    userService.grantRole(new UserRequests.GrantRoleRequest(id: UserId.from('kjh'), roleId: RoleId.from(UserRoles.USER_MANAGER.getId())))
    userService.revokeRole(new UserRequests.RevokeRoleRequest(id: UserId.from('kjh'), roleId: RoleId.from(UserRoles.USER_MANAGER.getId())))

    then:
    userService.hasRole(UserId.from('kjh'), RoleId.from(UserRoles.USER_MANAGER.getId())) == false
  }

  def "기존에 존재하던 권한을 부여"() {
    when:
    userService.grantRole(new UserRequests.GrantRoleRequest(id: UserId.from('kjh'), roleId: RoleId.from(UserRoles.USER_MANAGER.getId())))
    userService.grantRole(new UserRequests.GrantRoleRequest(id: UserId.from('kjh'), roleId: RoleId.from(UserRoles.USER_MANAGER.getId())))

    then:
    thrown(RoleExceptions.AlreadyExistsException)
  }

  def "부여되지 않았던 권한을 해제"() {
    when:
    userService.revokeRole(new UserRequests.RevokeRoleRequest(id: UserId.from('kjh'), roleId: RoleId.from(UserRoles.USER_MANAGER.getId())))

    then:
    thrown(RoleExceptions.NotFoundException)
  }
}
