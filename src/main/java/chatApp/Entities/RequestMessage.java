package chatApp.Entities;

import javax.persistence.*;

@Entity
@Table(name = "RequestMessage")
public class RequestMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "message_id")
    private int id;

    @Column(name = "sender_token")
    private String token;
    @Column(name = "message_content")
    private String content;

    public RequestMessage() {
    }

    public RequestMessage(String token, String content) {
        this.token = token;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "Token='" + token + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
