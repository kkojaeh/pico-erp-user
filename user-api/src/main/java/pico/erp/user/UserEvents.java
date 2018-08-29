package pico.erp.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pico.erp.shared.event.Event;
import pico.erp.user.data.UserId;

public interface UserEvents {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class CreatedEvent implements Event {

    public final static String CHANNEL = "event.user-user.created";

    private UserId userId;

    private String password;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class DeletedEvent implements Event {

    public final static String CHANNEL = "event.user-user.deleted";

    private UserId userId;

    public String channel() {
      return CHANNEL;
    }

  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  class UpdatedEvent implements Event {

    public final static String CHANNEL = "event.user-user.updated";

    private UserId userId;

    public String channel() {
      return CHANNEL;
    }

  }
}
