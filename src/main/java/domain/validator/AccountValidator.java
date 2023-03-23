package domain.validator;

import domain.Account;

public class AccountValidator implements Validator<Account> {
    @Override
    public void validate(Account entity) throws ValidationException {
        String errMsg="";
        if(entity.getUsername() == null)
            errMsg += "The username can t be null!";
        if(entity.getUsername().length() < 3)
            errMsg += "The username is too short!";
        if(entity.getUsername().contains(";"))
            errMsg += "The username can t contain ';' !";

        if(entity.getEmail() == null)
            errMsg += "The email can t be null!";
        if(entity.getEmail().length() < 3)
            errMsg += "The email is too short!";
        if(entity.getEmail().contains(";"))
            errMsg += "The email can t contain ';' !";

        if(entity.getPassword() == null)
            errMsg += "The password can t be null!";
        if(entity.getPassword().length() < 3)
            errMsg += "The password is too short!";
        if(entity.getPassword().contains(";"))
            errMsg += "The password can t contain ';' !";
        if(!errMsg.equals(""))
            throw new ValidationException(errMsg);
    }
}

