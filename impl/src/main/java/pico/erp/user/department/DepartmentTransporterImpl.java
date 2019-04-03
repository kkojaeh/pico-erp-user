package pico.erp.user.department;

import com.coreoz.windmill.Windmill;
import com.coreoz.windmill.exports.config.ExportHeaderMapping;
import com.coreoz.windmill.exports.exporters.excel.ExportExcelConfig;
import com.coreoz.windmill.files.FileSource;
import com.coreoz.windmill.imports.Parsers;
import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Stream;
import kkojaeh.spring.boot.component.ComponentBean;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.shared.data.ContentInputStream;
import pico.erp.shared.event.EventPublisher;
import pico.erp.user.UserId;
import pico.erp.user.UserMapper;

@Component
@ComponentBean
@Validated
@Transactional
public class DepartmentTransporterImpl implements DepartmentTransporter {

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private DepartmentRepository departmentRepository;

  @Autowired
  private EventPublisher eventPublisher;

  @SneakyThrows
  @Override
  public ContentInputStream exportExcel(ExportRequest request) {
    Stream<Department> departments =
      request.isEmpty() ? Stream.empty() : departmentRepository.getAll();
    val workbook = new XSSFWorkbook();
    val bytes = Windmill
      .export(() -> departments.iterator())
      .withHeaderMapping(
        new ExportHeaderMapping<Department>()
          .add("id", e -> e.getId().getValue())
          .add("name", e -> e.getName())
          .add("managerId", e -> e.getManager() != null ? e.getManager().getId() : null)
      )
      .asExcel(
        ExportExcelConfig.fromWorkbook(workbook).build("departments")
      )
      .toByteArray();
    return ContentInputStream.builder()
      .name(
        String.format("departments-%s.%s",
          DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now()),
          ContentInputStream.XLSX_CONTENT_EXTENSION
        )
      )
      .contentType(ContentInputStream.XLSX_CONTENT_TYPE)
      .contentLength(bytes.length)
      .inputStream(new ByteArrayInputStream(bytes))
      .build();

  }

  @SneakyThrows
  @Override
  public void importExcel(ImportRequest request) {
    val departments = Parsers.xlsx("departments")
      .trimValues()
      .parse(FileSource.of(request.getInputStream()))
      .skip(1)
      .map(row -> Department.builder()
        .id(DepartmentId.from(row.cell("id").asString()))
        .name(row.cell("name").asString())
        .manager(
          Optional.ofNullable(row.cell("email").asString())
            .map(UserId::from)
            .map(userMapper::map)
            .orElse(null)
        )
        .build()
      );

    departments.forEach(department -> {
      val previous = departmentRepository.findBy(department.getId()).orElse(null);
      val response = department.apply(new DepartmentMessages.PrepareImportRequest(previous));
      if (previous == null) {
        departmentRepository.create(department);
      } else if (request.isOverwrite()) {
        departmentRepository.update(department);
      }
      eventPublisher.publishEvents(response.getEvents());
    });
  }

}
