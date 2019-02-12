package pico.erp.user.group;

import com.coreoz.windmill.Windmill;
import com.coreoz.windmill.exports.config.ExportHeaderMapping;
import com.coreoz.windmill.exports.exporters.excel.ExportExcelConfig;
import com.coreoz.windmill.files.FileSource;
import com.coreoz.windmill.imports.Parsers;
import java.io.ByteArrayInputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import pico.erp.shared.Public;
import pico.erp.shared.data.ContentInputStream;
import pico.erp.shared.data.Role;
import pico.erp.shared.event.EventPublisher;
import pico.erp.user.role.RoleId;
import pico.erp.user.role.RoleMapper;

@Component
@Public
@Validated
@Transactional
public class GroupTransporterImpl implements GroupTransporter {

  @Autowired
  private GroupRepository groupRepository;

  @Autowired
  private RoleMapper roleMapper;

  @Autowired
  private EventPublisher eventPublisher;

  @SneakyThrows
  @Override
  public ContentInputStream exportExcel(ExportRequest request) {
    val locale = LocaleContextHolder.getLocale();
    Stream<Group> groups = request.isEmpty() ? Stream.empty() : groupRepository.findAll();
    val workbook = new XSSFWorkbook();
    val bytes = Windmill
      .export(() -> groups.iterator())
      .withHeaderMapping(
        new ExportHeaderMapping<Group>()
          .add("id", e -> e.getId().getValue())
          .add("name", e -> e.getName())
          .add("roles", e ->
            StringUtils.collectionToCommaDelimitedString(
              e.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toList())
            )
          )
      )
      .asExcel(
        ExportExcelConfig.fromWorkbook(workbook).build("groups")
      )
      .toByteArray();
    return ContentInputStream.builder()
      .name(
        String.format("groups-%s.%s",
          DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(OffsetDateTime.now()),
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
    val groups = Parsers.xlsx("groups")
      .trimValues()
      .parse(FileSource.of(request.getInputStream()))
      .skip(1)
      .map(row -> Group.builder()
        .id(GroupId.from(row.cell("id").asString()))
        .name(row.cell("name").asString())
        .roles(
          StringUtils.commaDelimitedListToSet(row.cell("roles").asString())
            .stream()
            .map(RoleId::from)
            .map(roleMapper::map)
            .collect(Collectors.toSet())
        )
        .build()
      );

    groups.forEach(group -> {
      val previous = groupRepository.findBy(group.getId()).orElse(null);
      val response = group.apply(new GroupMessages.PrepareImportRequest(previous));
      if (previous == null) {
        groupRepository.create(group);
      } else if (request.isOverwrite()) {
        groupRepository.update(group);
      }
      eventPublisher.publishEvents(response.getEvents());
    });
  }

}
