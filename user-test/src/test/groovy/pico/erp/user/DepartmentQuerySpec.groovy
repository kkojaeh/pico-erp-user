package pico.erp.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Lazy
import org.springframework.data.domain.PageRequest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.shared.IntegrationConfiguration
import pico.erp.user.department.DepartmentQuery
import pico.erp.user.department.DepartmentService
import pico.erp.user.department.DepartmentView
import spock.lang.Specification

@SpringBootTest(classes = [IntegrationConfiguration])
@Transactional
@Rollback
@ActiveProfiles("test")
class DepartmentQuerySpec extends Specification {

  def setup() {
  }

  @Lazy
  @Autowired
  DepartmentQuery departmentQuery

  @Lazy
  @Autowired
  UserService userService

  @Lazy
  @Autowired
  DepartmentService departmentService

  def "사용자 조회 - 조회 조건에 맞게 조회"() {
    expect:
    def page = departmentQuery.retrieve(condition, pageable)
    page.totalElements == totalElements

    where:
    condition                             | pageable               || totalElements
    new DepartmentView.Filter(name: "생산") | new PageRequest(0, 10) || 1
    new DepartmentView.Filter(name: "영업") | new PageRequest(0, 10) || 1
  }

}
