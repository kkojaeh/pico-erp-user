package pico.erp.user

import kkojaeh.spring.boot.component.SpringBootTestComponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.shared.TestParentApplication
import pico.erp.user.group.GroupQuery
import pico.erp.user.group.GroupView
import spock.lang.Specification

@SpringBootTest(classes = [UserApplication])
@SpringBootTestComponent(parent = TestParentApplication, siblings = [])
@Transactional
@Rollback
@ActiveProfiles("test")
class GroupQuerySpec extends Specification {

  @Autowired
  GroupQuery groupQuery

  def "그룹 조회 - 조회 조건에 맞게 조회"() {
    expect:
    def page = groupQuery.retrieve(condition, pageable)
    page.totalElements == totalElements

    where:
    condition                        | pageable               || totalElements
    new GroupView.Filter(name: "생산") | new PageRequest(0, 10) || 1
    new GroupView.Filter(name: "재경") | new PageRequest(0, 10) || 1
  }

}
