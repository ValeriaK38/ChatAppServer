package chatApp.Entities;

import javax.persistence.*;

@Entity
@Table(name = "message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="message_id")
    private int id;
    @Column(name="message_sender")
    private String sender;
    @Column(name="message_content")
    private String content;

    public ChatMessage() {
    }

    public ChatMessage(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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
                "sender='" + sender + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
