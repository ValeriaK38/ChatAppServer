package chatApp.Entities;

public class NicknameTokenPair {

    private String nickName;
    private String token;

    public NicknameTokenPair(String nickName, String token) {
        this.nickName = nickName;
        this.token = token;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
