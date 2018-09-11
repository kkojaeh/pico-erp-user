package pico.erp.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.shared.IntegrationConfiguration
import pico.erp.user.data.DepartmentId
import pico.erp.user.data.UserId
import pico.erp.user.department.DepartmentExceptions
import pico.erp.user.department.DepartmentRequests
import pico.erp.user.department.DepartmentService
import spock.lang.Specification

import java.util.concurrent.TimeUnit

@SpringBootTest(classes = [IntegrationConfiguration])
@Transactional
@Rollback
@ActiveProfiles("test")
class DepartmentServiceSpec extends Specification {

  @Autowired
  DepartmentService departmentService

  @Autowired
  UserService userService

  def setup() {
    TimeUnit.SECONDS.sleep(3)
    departmentService.create(new DepartmentRequests.CreateRequest(id: DepartmentId.from("PROD"), name: "생산부", managerId: UserId.from("kjh")))
    departmentService.create(new DepartmentRequests.CreateRequest(id: DepartmentId.from("BIZ"), name: "영업부"))
  }

  def "존재하는 부서 확인"() {
    when:
    def exists = departmentService.exists(DepartmentId.from("PROD"))

    then:
    exists == true
  }

  def "존재하지 않는 부서 확인"() {
    when:
    def exists = departmentService.exists(DepartmentId.from("!PROD"))

    then:
    exists == false
  }

  def "존재하는 부서 조회"() {
    when:
    def department = departmentService.get(DepartmentId.from("PROD"))

    then:

    department.id.value == "PROD"
    department.name == "생산부"
    department.managerId == UserId.from("kjh")
  }

  def "존재하지 않는 부서 조회"() {
    when:
    departmentService.get(DepartmentId.from("!PROD"))

    then:
    thrown(DepartmentExceptions.NotFoundException)
  }

  def "관리자 없음으로 변경"() {
    when:
    departmentService.update(
      new DepartmentRequests.UpdateRequest(
        id: DepartmentId.from("PROD"),
        name: "생산부",
        managerId: null
      )
    )

    def department = departmentService.get(DepartmentId.from("PROD"))

    then:
    department.managerId == null
  }

}
