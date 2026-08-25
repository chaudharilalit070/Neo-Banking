import jakarta.validation.constraints.NotNull;

public record CustomerLifecycleActionRequest(

        @NotNull(message = "Lifecycle action is required")
        CustomerLifecycleAction action

) {
}