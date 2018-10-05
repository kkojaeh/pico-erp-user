package pico.erp.user.group;

import com.ebay.xcelite.reader.SheetReader;
import com.ebay.xcelite.sheet.XceliteSheetImpl;
import com.ebay.xcelite.writer.SheetWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import pico.erp.shared.Public;
import pico.erp.shared.data.ContentInputStream;
import pico.erp.user.group.GroupRequests.CreateRequest;
import pico.erp.user.group.GroupRequests.UpdateRequest;

@Component
@Public
@Validated
@Transactional
public class GroupXporterXcelite implements GroupXporter {

  @Autowired
  private GroupService groupService;

  @Autowired
  private GroupRepository groupRepository;

  @SneakyThrows
  @Override
  public ContentInputStream exportExcel(ExportRequest request) {
    XSSFWorkbook workbook = new XSSFWorkbook();

    SheetWriter<GroupXportData> groupWriter = new XceliteSheetImpl(workbook.createSheet("groups"))
      .getBeanWriter(GroupXportData.class);
    groupWriter.generateHeaderRow(true);
    if (request.isEmpty()) {
      groupWriter.write(Collections.emptyList());
    } else {
      groupWriter.write(
        groupRepository.getAll()
          .map(this::translate)
          .collect(Collectors.toList())
      );
    }
    @Cleanup
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    workbook.write(baos);

    return ContentInputStream.builder()
      .name(
        String.format("group-%s.%s",
          DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(OffsetDateTime.now()),
          "xlsx"
        )
      )
      .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
      .contentLength(baos.size())
      .inputStream(new ByteArrayInputStream(baos.toByteArray()))
      .build();
  }

  @SneakyThrows
  @Override
  public void importExcel(ImportRequest request) {
    @Cleanup
    InputStream xlsxInputStream = request.getInputStream();
    XSSFWorkbook workbook = new XSSFWorkbook(xlsxInputStream);

    SheetReader<GroupXportData> groupReader = new XceliteSheetImpl(workbook.getSheet("groups"))
      .getBeanReader(GroupXportData.class);
    groupReader.skipHeaderRow(true);
    Collection<GroupXportData> groups = groupReader.read();

    Map<GroupId, Boolean> exists = new HashMap<>();

    groups.stream()
      .forEach(data -> {
        exists.put(data.getId(), groupService.exists(data.getId()));
      });

    groups.stream()
      .filter(data -> !exists.get(data.getId()))
      .map(this::toCreate)
      .forEach(groupService::create);

    if (request.isOverwrite()) {
      groups.stream()
        .filter(data -> exists.get(data.getId()))
        .map(this::toUpdate)
        .forEach(groupService::update);
    }
  }

  CreateRequest toCreate(GroupXportData data) {
    return CreateRequest.builder()
      .id(data.getId())
      .name(data.getName())
      .build();
  }

  UpdateRequest toUpdate(GroupXportData data) {
    return UpdateRequest.builder()
      .id(data.getId())
      .name(data.getName())
      .build();
  }

  GroupXportData translate(Group group) {
    return GroupXportData.builder()
      .id(group.getId())
      .name(group.getName())
      .build();
  }
}
