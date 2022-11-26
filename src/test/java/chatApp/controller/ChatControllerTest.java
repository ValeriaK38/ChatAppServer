package chatApp.controller;

import chatApp.Entities.ChatMessage;
import chatApp.Entities.RequestMessage;
import chatApp.repository.MessageRepository;
import chatApp.service.ChatService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class ChatControllerTest {

    @Autowired
    ChatController chatController;

    @Autowired
    private ChatService chatService;

    @Autowired
    private static MessageRepository messageRepository;

    List<ChatMessage> messageList;


    @BeforeEach
    public void setup() {
        messageList = new ArrayList<>();

    }

    @AfterEach
    public void cleanup() {
        messageList = null;
    }


    @Test //////////////////////////////// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ Needs to be fixed! User register test must be done first
    void Test_Create_Message_Successfully() {
        System.out.println("-------- Test Creating a new message successfully --------");

        int before_size = chatController.getAllMessages().size();
        //Given we have a message we want to create
        RequestMessage message = new RequestMessage("Leon","Test");

        //When we create the message
        chatController.createMessage(message);

        //Then a message was created and added to the database.
        int after_size = chatController.getAllMessages().size();
        ChatMessage testMessage = chatController.getAllMessages().get(after_size-1);
        assertEquals((after_size - before_size) , 1);
        assertTrue(message.getContent() == testMessage.getContent());
    }

    @Test
    void testCreateMessage() {
        System.out.println("-------- Test trying to create a new message with invalid user --------");
        //Should not even happen because only registered and online users can try to create a message but checking just in case.

        RequestMessage message = new RequestMessage("Leon","Test");

        //When we try to create the message with an invalid user Then we fail
        assertThrows(Exception.class, () ->  chatController.createMessage(message));

        System.out.println("Failed to create a message because couldn't find a user with this token");
    }

    @Test
    void getAllMessages() {
        System.out.println("-------- Test pulling all messages from database --------");
        //Given there are messages in the database

        //When i try to get the list of all the users
        messageList = chatController.getAllMessages();

        //Then I now have a list of all the users in the database
        assertFalse(messageList.isEmpty());

        System.out.println("The first message in the database is:");
        System.out.println(messageList.get(0));
    }
}