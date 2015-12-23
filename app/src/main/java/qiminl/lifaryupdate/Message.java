package qiminl.lifaryupdate;

/**
 *Class Message is created to handle php response message
 *      contains structure:
 *              [success]: 1 & 0 indicate success or fail
 *              [message]: String of message
 */
public class Message {
    private String message = "";
    private String success = "";
    Message(){}
    public String getMessage(){
        return message;
    }
    public String getSuccess(){
        return success;
    }
    public String toString(){
        String result = "{\"success\":"+success+",\"message\":\""+message+"\"}";
        return result;
    }
}