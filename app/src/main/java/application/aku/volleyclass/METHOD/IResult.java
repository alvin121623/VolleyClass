package application.aku.volleyclass.METHOD;

import org.json.JSONObject;

public interface IResult {
    void notifyLoad(String type, boolean load);

    void notifySuccess(String type, JSONObject response);

    void notifyError(String type, String message);

    void notifyUnauth(String type, String message);

    void notifyFailed(String type, String message);
}
