package eventSystem.forms.user;

import javax.validation.constraints.NotNull;

public class EditUserForm {
    @NotNull
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
