package pl.mg.rocketleaguecontroller.exception;

/**
 * Created by Maciej on 01.03.2016.
 */
public class ConnectionException extends Exception {

    private int type;

    public ConnectionException(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
