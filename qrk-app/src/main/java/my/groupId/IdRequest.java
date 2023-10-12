package my.groupId;

import jakarta.validation.constraints.NotEmpty;

public record IdRequest(@NotEmpty Long id) {

}
