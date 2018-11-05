
package com.ebebek.demo.validator;

import com.ebebek.demo.model.User;
import com.ebebek.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserValidator implements Validator {
	private Pattern pattern;  
	private Matcher matcher;


	@Autowired
	private UserService userService;

	@Override
	public boolean supports(Class<?> aClass) {
		return User.class.equals(aClass);
	}

	@Override
	public void validate(Object o, Errors errors) {
		User user = (User) o;

		validateUserName(user.getUsername(), errors);
        validateEmailAdress(user.getEmail(), errors);
        validatePassword(user.getPassword(), user.getPasswordConfirm(), errors);
	}

	private void validateUserName(String name, Errors errors) {

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "NotEmpty");

        if (name.length() < 3 || name.length() > 32) {
            errors.rejectValue("username", "Username.size");
        }
        if (userService.findByUsername(name) != null) {
            errors.rejectValue("username", "Username.duplication");
        }
	}

	private void validateEmailAdress(String email, Errors errors) {

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty");

        if (userService.findByEmail(email) != null) {
            errors.rejectValue("email", "Email.duplication");
        }

        if (!email.isEmpty()) {
            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
            pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(email);

            if (!matcher.matches()) {
                errors.rejectValue("email", "Email.type");
            }
        }
    }

    private void validatePassword(String password, String passConfirmation, Errors errors) {

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");


        if (password.length() < 7 || password.length() > 32) {
            errors.rejectValue("password", "Password.size");
        }

        if (!password.isEmpty()) {
            String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{7,}$";
            pattern = Pattern.compile(PASSWORD_PATTERN);
            matcher = pattern.matcher(password);

            if (!matcher.matches()) {
                errors.rejectValue("password", "Password.type");
            }
        }

        if (!passConfirmation.equals(password)) {
            errors.rejectValue("passwordConfirm", "Password.confirmation");
        }
    }
}
