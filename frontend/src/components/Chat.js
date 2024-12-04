import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { FiSend, FiPhone, FiVideo } from 'react-icons/fi';

const Container = styled.div`
  display: flex;
  height: 100vh;
  background-color: #f0f2f5;
`;

const ContactList = styled.div`
  width: 30%;
  background-color: white;
  border-right: 1px solid #e0e0e0;
`;

const ChatArea = styled.div`
  flex: 1;
  display: flex;
  flex-direction: column;
`;

const Header = styled.div`
  padding: 16px;
  background-color: #f0f2f5;
  border-bottom: 1px solid #e0e0e0;
`;

const MessageList = styled.div`
  flex: 1;
  padding: 16px;
  overflow-y: auto;
`;

const InputArea = styled.form`
  display: flex;
  padding: 16px;
  background-color: #f0f2f5;
`;

const Input = styled.input`
  flex: 1;
  padding: 8px;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
`;

const Button = styled.button`
  margin-left: 8px;
  padding: 8px 16px;
  background-color: #0084ff;
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
`;

const ContactItem = styled.div`
  padding: 16px;
  border-bottom: 1px solid #e0e0e0;
  cursor: pointer;
  &:hover {
    background-color: #f0f2f5;
  }
`;

const Message = styled.div`
  margin-bottom: 8px;
  padding: 8px;
  background-color: ${props => props.isUser ? '#0084ff' : '#e0e0e0'};
  color: ${props => props.isUser ? 'white' : 'black'};
  border-radius: 20px;
  max-width: 70%;
  align-self: ${props => props.isUser ? 'flex-end' : 'flex-start'};
`;

const Chat = () => {
    const [contacts, setContacts] = useState([]);
    const [messages, setMessages] = useState([]);
    const [selectedContact, setSelectedContact] = useState(null);
    const [inputMessage, setInputMessage] = useState('');
    const userId = "674d484122d4184410ee7a45";

    // Llamada para obtener contactos
    useEffect(() => {
        const fetchContacts = async () => {
            try {
                const response = await fetch(`http://localhost:8080/api/contacts/get-contacts?userId=${userId}`);
                if (!response.ok) {
                    throw new Error('Error al obtener los contactos');
                }
                const data = await response.json();
                console.log('Contacts data:', data); // Verifica si los datos se reciben correctamente
                setContacts(data);
            } catch (error) {
                console.error('Error fetching contacts:', error);
            }
        };


        fetchContacts();
    }, [userId]);

    // Llamada para obtener mensajes de un chat seleccionado
    useEffect(() => {
        const fetchMessages = async () => {
            if (selectedContact) {
                try {
                    const response = await fetch(`http://localhost:8080/chats/${selectedContact.id}/messages`);
                    if (!response.ok) {
                        throw new Error('Error al obtener los mensajes');
                    }
                    const data = await response.json();
                    setMessages(data);
                } catch (error) {
                    console.error('Error fetching messages:', error);
                }
            }
        };

        fetchMessages();
    }, [selectedContact]);

    // Función para manejar el envío de mensajes
    const handleSendMessage = async (e) => {
        e.preventDefault();

        if (!inputMessage.trim()) return;

        const messageData = {
            content: inputMessage,
            sender: userId,
            receiver: selectedContact.name,
        };

        try {
            const response = await fetch('http://localhost:8080/sendMessage', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(messageData),
            });

            if (!response.ok) {
                throw new Error('Error al enviar el mensaje');
            }

            const newMessage = await response.json();
            setMessages((prevMessages) => [...prevMessages, newMessage]);
            setInputMessage('');
        } catch (error) {
            console.error('Error sending message:', error);
        }
    };

    return (
        <Container>
            <ContactList>
                {contacts.map((contact) => (
                    <ContactItem key={contact.id} onClick={() => setSelectedContact(contact)}>
                        <h3>{contact.name}</h3>
                        <p>{contact.lastMessage}</p>
                    </ContactItem>
                ))}
            </ContactList>

            <ChatArea>
                {selectedContact ? (
                    <>
                        <Header>
                            <h2>{selectedContact.name}</h2>
                            <FiPhone />
                            <FiVideo />
                        </Header>

                        <MessageList>
                            {messages.map((message) => (
                                <Message key={message.id} isUser={message.sender === userId}>
                                    {message.content}
                                </Message>
                            ))}
                        </MessageList>

                        <InputArea onSubmit={handleSendMessage}>
                            <Input
                                type="text"
                                value={inputMessage}
                                onChange={(e) => setInputMessage(e.target.value)}
                                placeholder="Escribe un mensaje..."
                            />
                            <Button type="submit">
                                <FiSend />
                            </Button>
                        </InputArea>
                    </>
                ) : (
                    <p>Selecciona un chat para comenzar</p>
                )}
            </ChatArea>
        </Container>
    );
};

export default Chat;