package eventSystem.forms.user;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
