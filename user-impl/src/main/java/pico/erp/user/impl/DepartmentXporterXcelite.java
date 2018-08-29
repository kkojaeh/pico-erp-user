package pico.erp.user.impl;

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
import java.util.Optional;
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
import pico.erp.user.DepartmentRequests;
import pico.erp.user.DepartmentRequests.CreateRequest;
import pico.erp.user.DepartmentRequests.UpdateRequest;
import pico.erp.user.DepartmentService;
import pico.erp.user.DepartmentXporter;
import pico.erp.user.core.DepartmentRepository;
import pico.erp.user.core.UserRepository;
import pico.erp.user.data.DepartmentId;
import pico.erp.user.domain.Department;
import pico.erp.user.domain.User;
import pico.erp.user.impl.xcelite.DepartmentXportData;

@Component
@Public
@Validated
@Transactional
public class DepartmentXporterXcelite implements DepartmentXporter {

  @Autowired
  private DepartmentService departmentService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private DepartmentRepository departmentRepository;

  @SneakyThrows
  @Override
  public ContentInputStream exportExcel(ExportRequest request) {
    XSSFWorkbook workbook = new XSSFWorkbook();

    SheetWriter<DepartmentXportData> departmentWriter = new XceliteSheetImpl(
      workbook.createSheet("departments"))
      .getBeanWriter(DepartmentXportData.class);
    departmentWriter.generateHeaderRow(true);
    if (request.isEmpty()) {
      departmentWriter.write(Collections.emptyList());
    } else {
      departmentWriter.write(
        departmentRepository.getAll()
          .map(this::translate)
          .collect(Collectors.toList())
      );
    }
    @Cleanup
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    workbook.write(baos);

    return ContentInputStream.builder()
      .name(
        String.format("department-%s.%s",
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
    SheetReader<DepartmentXportData> departmentReader = new XceliteSheetImpl(
      workbook.getSheet("departments")).getBeanReader(DepartmentXportData.class);
    departmentReader.skipHeaderRow(true);
    Collection<DepartmentXportData> departments = departmentReader.read();

    Map<DepartmentId, Boolean> exists = new HashMap<>();

    departments.stream()
      .forEach(data -> {
        exists.put(data.getId(), departmentService.exists(data.getId()));
      });

    departments.stream()
      .filter(data -> !exists.get(data.getId()))
      .map(this::toCreate)
      .forEach(departmentService::create);

    if (request.isOverwrite()) {
      departments.stream()
        .filter(data -> exists.get(data.getId()))
        .map(this::toUpdate)
        .forEach(departmentService::update);
    }
  }

  CreateRequest toCreate(DepartmentXportData data) {
    return CreateRequest.builder()
      .id(data.getId())
      .name(data.getName())
      .managerId(
        Optional.ofNullable(data.getManagerId())
          .map(id -> userRepository.exists(id) ? id : null)
          .orElse(null)
      )
      .build();
  }

  UpdateRequest toUpdate(DepartmentXportData data) {
    return UpdateRequest.builder()
      .id(data.getId())
      .name(data.getName())
      .managerId(
        Optional.ofNullable(data.getManagerId())
          .map(id -> userRepository.exists(id) ? id : null)
          .orElse(null)
      )
      .build();
  }

  DepartmentXportData translate(Department department) {
    return DepartmentXportData.builder()
      .id(department.getId())
      .name(department.getName())
      .managerId(
        Optional.ofNullable(department.getManager())
          .map(User::getId)
          .orElse(null)
      )
      .build();
  }
}
