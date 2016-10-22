package forms;

import models.User;
import org.mindrot.jbcrypt.BCrypt;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.validation.Constraint;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lubuntu on 10/22/16.
 */
public class LoginForm {
    @Constraints.Required
    public String email;

    @Constraints.Required
    public String password;

    public List<ValidationError> validate(){
        List <ValidationError> error=new ArrayList<>();
        User user=User.find.where().eq("email",email).findUnique();

        if(user==null)
        {
            error.add(new ValidationError("message","incorrect email or Password"));
        }
        else if(!BCrypt.checkpw(password,user.password)){
            error.add(new ValidationError("message","Incorrect email or Password"));
        }
        return error;

    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
