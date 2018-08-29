package pico.erp.user.impl.xcelite;

import com.ebay.xcelite.annotations.Column;
import com.ebay.xcelite.annotations.Row;
import com.ebay.xcelite.converters.ColumnValueConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.user.data.GroupId;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Row(colsOrder = {"id", "name"})
public class GroupXportData {

  @Column(converter = GroupIdColumnValueConverter.class)
  GroupId id;

  @Column
  String name;

  public static class GroupIdColumnValueConverter implements
    ColumnValueConverter<String, GroupId> {

    @Override
    public GroupId deserialize(String value) {
      return GroupId.from(value);
    }

    @Override
    public String serialize(GroupId value) {
      return value.getValue();
    }

  }

}
