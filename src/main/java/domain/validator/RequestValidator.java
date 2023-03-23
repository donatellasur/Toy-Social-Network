package domain.validator;

import domain.Request;

public class RequestValidator implements Validator<Request> {
    @Override
    public void validate(Request entity) throws ValidationException {
        String errMsg="";
        if(entity.getIdUser1() == null)
            errMsg += "The first id can't be null!";
        if(entity.getIdUser2() == null)
            errMsg += "The second id can't be null!";
        if(entity.getStatus() == null)
            errMsg += "The status can't be null!";
        if(!entity.getStatus().equals("pending") && !entity.getStatus().equals("accepted") && !entity.getStatus().equals("rejected"))
            errMsg += "The status can only be pending, accepted or rejected!";
        if(!errMsg.equals(""))
            throw new ValidationException(errMsg);
    }
}

