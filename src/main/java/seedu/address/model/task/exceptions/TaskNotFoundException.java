package seedu.address.model.task.exceptions;

/**
 * Signals that the operation is unable to find the specified task.
 */
public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException() {
        super("Task could not be found.");
    }

}
