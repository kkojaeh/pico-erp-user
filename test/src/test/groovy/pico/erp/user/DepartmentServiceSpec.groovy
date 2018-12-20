package pico.erp.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Lazy
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.shared.IntegrationConfiguration
import pico.erp.user.department.DepartmentExceptions
import pico.erp.user.department.DepartmentId
import pico.erp.user.department.DepartmentRequests
import pico.erp.user.department.DepartmentService
import spock.lang.Specification

@SpringBootTest(classes = [IntegrationConfiguration])
@Transactional
@Rollback
@ActiveProfiles("test")
class DepartmentServiceSpec extends Specification {

  @Lazy
  @Autowired
  DepartmentService departmentService

  def id = DepartmentId.from("test")

  def unknownId = DepartmentId.from("unknown")

  def name = "테스트 부서"

  def managerId = UserId.from("kjh")

  def setup() {
    departmentService.create(
      new DepartmentRequests.CreateRequest(
        id: id,
        name: name,
        managerId: managerId
      )
    )
  }

  def "존재 - 아이디로 확인"() {
    when:
    def exists = departmentService.exists(id)

    then:
    exists == true
  }

  def "존재 - 존재하지 않는 아이디로 확인"() {
    when:
    def exists = departmentService.exists(unknownId)

    then:
    exists == false
  }

  def "조회 - 아이디로 조회"() {
    when:
    def department = departmentService.get(id)

    then:

    department.id == id
    department.name == name
    department.managerId == managerId
  }

  def "조회 - 존재하지 않는 아이디로 조회"() {
    when:
    departmentService.get(unknownId)

    then:
    thrown(DepartmentExceptions.NotFoundException)
  }

  def "수정 - 수정"() {
    when:

    departmentService.update(
      new DepartmentRequests.UpdateRequest(
        id: id,
        name: "테스트 부서 2",
        managerId: null
      )
    )

    def department = departmentService.get(id)

    then:
    department.name == "테스트 부서 2"
    department.managerId == null
  }

}
