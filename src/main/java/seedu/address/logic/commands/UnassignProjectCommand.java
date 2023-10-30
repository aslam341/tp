package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_EMPLOYEE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PROJECT;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_EMPLOYEES;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.employee.Employee;
import seedu.address.model.project.Project;

/**
 * Unassigns a project from an employee.
 */
public class UnassignProjectCommand extends Command {

    public static final String COMMAND_WORD = "unassignP";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Unassigns the project of the employees identified "
            + "by the index number used in the last employee listing. "
            + "\n"
            + "Parameters: PROJECT_INDEX, EMPLOYEE_INDEX (must be a positive integer) "
            + "pr/[PROJECT_INDEX] em/[EMPLOYEE_INDEX ...]\n"
            + "Example: " + COMMAND_WORD + " " + PREFIX_PROJECT + "1 "
            + PREFIX_EMPLOYEE + "2 3 1";

    public static final String MESSAGE_UNASSIGN_PROJECT_SUCCESS = "Member(s) have been un-assigned!\n%1$s";
    public static final String MESSAGE_UNASSIGN_PROJECT_FAILURE = "The following employee is not in the specified "
            + "project. \n"
            + "Employee: %1$s (Index: %2$s) "
            + "Project: %3$s (Index: %4$s)\n"
            + "Please check the project and employee index again. "
            + "Using the findP command to isolate the project and it's employees may be useful.";

    private final Index projectIndex;
    private final List<Index> employeeIndexes;

    /**
     * @param projectIndex index of the project in the filtered project list to update
     * @param employeeIndexes indexes of the employees to be unassigned from the project
     */
    public UnassignProjectCommand(Index projectIndex, List<Index> employeeIndexes) {
        requireAllNonNull(projectIndex, employeeIndexes);

        this.projectIndex = projectIndex;
        this.employeeIndexes = employeeIndexes;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        List<Employee> lastShownEmployeeList = model.getFilteredEmployeeList();
        List<Project> lastShownProjectList = model.getFilteredProjectList();

        if (projectIndex.getZeroBased() >= lastShownProjectList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PROJECT_DISPLAYED_INDEX);
        }

        Project projectToEdit = lastShownProjectList.get(projectIndex.getZeroBased());
        Project editedProject = new Project(projectToEdit.name, projectToEdit.employeeList, projectToEdit.getTasks(),
                projectToEdit.getProjectPriority(), projectToEdit.getDeadline(), projectToEdit.getCompletionStatus());

        // Check if all employees are in the project
        for (Index employeeIndex : employeeIndexes) {
            if (employeeIndex.getZeroBased() >= lastShownEmployeeList.size()) {
                throw new CommandException(Messages.MESSAGE_INVALID_EMPLOYEE_DISPLAYED_INDEX);
            }

            Employee employeeToRemove = lastShownEmployeeList.get(employeeIndex.getZeroBased());
            if (!editedProject.employeeList.contains(employeeToRemove)) {
                throw new CommandException(String.format(MESSAGE_UNASSIGN_PROJECT_FAILURE,
                        employeeToRemove.getName(), employeeIndex.getOneBased(),
                        projectToEdit.name, projectIndex.getOneBased()));
            }
        }

        // Remove employees from project
        for (Index employeeIndex : employeeIndexes) {
            Employee employeeToRemove = lastShownEmployeeList.get(employeeIndex.getZeroBased());
            editedProject.removeEmployee(employeeToRemove);
        }

        model.setProject(projectToEdit, editedProject);
        model.updateFilteredEmployeeList(PREDICATE_SHOW_ALL_EMPLOYEES);

        return new CommandResult(generateSuccessMessage(editedProject));
    }

    private String generateSuccessMessage(Project projectToEdit) {
        return String.format(MESSAGE_UNASSIGN_PROJECT_SUCCESS, Messages.format(projectToEdit));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof UnassignProjectCommand)) {
            return false;
        }

        // state check
        UnassignProjectCommand e = (UnassignProjectCommand) other;
        return projectIndex.equals(e.projectIndex)
                && employeeIndexes.equals(e.employeeIndexes);
    }
}