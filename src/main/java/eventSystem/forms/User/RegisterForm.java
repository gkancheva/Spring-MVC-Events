package eventSystem.forms.User;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class RegisterForm {
    @Size(min=2, max=30, message = "Username size should be in the range [2...30]")
    private String username;

    @Size(min = 2, message = "Email not correct")
    private String eMail;

    @NotNull
    @Size(min=1, max=50)
    private String password;

    @NotNull
    @Size(min=1, max=50)
    private String retypePassword;

    @NotNull
    @Size(min=1, max=200)
    private String fullName;

    public String getRetypePassword() {
        return retypePassword;
    }

    public void setRetypePassword(String retypePassword) {
        this.retypePassword = retypePassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }
}
