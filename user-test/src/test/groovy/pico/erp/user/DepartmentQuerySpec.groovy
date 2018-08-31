package pico.erp.user

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.shared.IntegrationConfiguration
import pico.erp.user.data.DepartmentView
import spock.lang.Specification

import java.util.concurrent.TimeUnit

@SpringBootTest(classes = [IntegrationConfiguration])
@Transactional
@Rollback
@ActiveProfiles("test")
class DepartmentQuerySpec extends Specification {

  def setup() {
    TimeUnit.SECONDS.sleep(3)
  }

  @Autowired
  DepartmentQuery departmentQuery

  @Autowired
  UserService userService

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
