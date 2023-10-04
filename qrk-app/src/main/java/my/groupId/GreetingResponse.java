package my.groupId;

import io.smallrye.common.constraint.NotNull;

public record GreetingResponse(@NotNull String message, @NotNull Integer age) {
}
