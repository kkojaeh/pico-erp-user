package pico.erp.user

import kkojaeh.spring.boot.component.SpringBootTestComponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import pico.erp.shared.TestParentApplication
import spock.lang.Specification

@SpringBootTest(classes = [UserApplication])
@SpringBootTestComponent(parent = TestParentApplication, siblings = [])
@Transactional
@Rollback
@ActiveProfiles("test")
@Configuration
class UserTransporterSpec extends Specification {

  @Autowired
  UserTransporter userTransporter

  @Autowired
  UserService userService

  @Value("classpath:user-import-data.xlsx")
  Resource importData

  def "export"() {
    when:
    def inputStream = userTransporter.exportExcel(
      new UserTransporter.ExportRequest(
        empty: false
      )
    )

    then:
    inputStream.contentLength > 0
  }

  def "import - 덮어쓴다"() {
    when:
    userTransporter.importExcel(
      new UserTransporter.ImportRequest(
        inputStream: importData.getInputStream(),
        overwrite: true
      )
    )
    def previous = userService.get(UserId.from("kjh"))
    def created = userService.get(UserId.from("test"))
    then:
    previous.name == "고재훈2"
    previous.position == "사원"
    created.id == UserId.from("test")
    created.name == "테스터"
    created.mobilePhoneNumber == "+821011111111"
    created.accountNonExpired == true
    created.accountNonLocked == true
    created.credentialsNonExpired == true
    created.enabled == true
  }

  def "import - 덮어쓰지 않는다"() {
    when:
    userTransporter.importExcel(
      new UserTransporter.ImportRequest(
        inputStream: importData.getInputStream(),
        overwrite: false
      )
    )
    def previous = userService.get(UserId.from("kjh"))
    then:
    previous.name != "고재훈2"
    previous.position != "사원"
  }
}
