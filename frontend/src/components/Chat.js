import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { FiSend } from 'react-icons/fi';
import { useLocation,useNavigate } from 'react-router-dom'; // Usamos useLocation para obtener el estado de la sesión
import axios from "axios";
import { Client } from "@stomp/stompjs";

// Estilos para los componentes
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
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px;
    background-color: #f0f2f5;
    border-bottom: 1px solid #e0e0e0;
`;

const MessageList = styled.div`
    display: flex;
    flex-direction: column;
    flex: 1;
    padding: 16px;
    overflow-y: auto;
`;

const InputArea = styled.form`
    display: flex;
    padding: 16px;
    background-color: #f0f2f5;
    margin-bottom: 30px;
`;

const Input = styled.input`
    width: 100%; /* Cambia el ancho al 80% */
    padding: 8px;
    border: 1px solid #e0e0e0;
    border-radius: 20px;
`;

const Button = styled.button`
    width: 15%;
    margin-left: 8px;
    padding: 8px 16px;
    background-color: #0084ff;
    color: white;
    border: none;
    border-radius: 50px;
    cursor: pointer;
`;


const ContactItem = styled.div`
    display: flex;
    align-items: center;
    padding: 16px;
    border-bottom: 1px solid #e0e0e0;
    cursor: pointer;

    &:hover {
        background-color: #f0f2f5;
    }
`;

const ProfileImage = styled.img`
    width: 70px;
    height: 70px;
    border-radius: 50%;
    margin-right: 16px;
`;

const Message = styled.div`
    margin-bottom: 8px;
    font-size: 1.25rem;
    padding: 8px;
    background-color: ${(props) => (props.$isUser ? '#0084ff' : '#e0e0e0')};
    color: ${(props) => (props.$isUser ? 'white' : 'black')};
    border-radius: 20px;
    max-width: 70%;
    align-self: ${(props) => (props.$isUser ? 'flex-end' : 'flex-start')};
`;

const MessageInicio = styled.div`
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100vh; 
    text-align: center;
    
    background-color: #f9f9f9; 
`;

const Image = styled.img`
    width: 250px; =
    height: auto;
    border-radius: 50%;
    margin-bottom: 20px;=
`;

const Text = styled.p`
    font-size: 1.5rem;
    color: #333;
    margin: 0;
`;

const Chat = () => {
    const location = useLocation();
    const { username } = location.state || {}; // Obtén el username del estado de la ruta
    const [userId, setUserId] = useState(null);
    const [contacts, setContacts] = useState([]);
    const [messages, setMessages] = useState([]);
    const [selectedContact, setSelectedContact] = useState(null);
    const [inputMessage, setInputMessage] = useState("");
    const [chatId, setChatId] = useState(null);
    const navigate = useNavigate();
    const [client, setClient] = useState(null); // WebSocket client

    // Verifica si el username está disponible
    useEffect(() => {
        if (!username) {
            console.error("Username is missing. Redirecting to login.");
            navigate("/login");
        }
    }, [username, navigate]);

    // Obtener el userId usando el username
    useEffect(() => {
        if (username) {
            const fetchUserId = async () => {
                try {
                    const response = await axios.get(
                        `http://localhost:8080/api/users/getUserId?username=${username}`
                    );
                    if (response.data) {
                        setUserId(response.data); // Asume que el backend devuelve el userId
                    } else {
                        console.error("User ID not found.");
                    }
                } catch (error) {
                    console.error("Error fetching user ID:", error);
                }
            };

            fetchUserId();
        }
    }, [username]);

    // Fetch de contactos
    useEffect(() => {
        if (userId) {
            const fetchContacts = async () => {
                try {
                    const response = await fetch(
                        `http://localhost:8080/api/contacts/get-contacts?userId=${userId}`
                    );
                    if (!response.ok) {
                        throw new Error("Error al obtener los contactos");
                    }
                    const data = await response.json();
                    setContacts(data);
                } catch (error) {
                    console.error("Error fetching contacts:", error);
                }
            };

            fetchContacts();
        }
    }, [userId]);

    // Obtener o crear chat al seleccionar un contacto
    const getOrCreateChat = async (user1Username, user2Username) => {
        try {
            const response = await fetch(
                `http://localhost:8080/chats/get-or-create?user1=${user1Username}&user2=${user2Username}`
            );
            if (!response.ok) {
                throw new Error("Error al obtener o crear el chat");
            }
            const data = await response.json();
            setChatId(data.chatId);

            // Suscribirse a mensajes del chat
            if (client) {
                client.send(
                    JSON.stringify({
                        action: "subscribe",
                        chatId: data.chatId,
                    })
                );
            }
        } catch (error) {
            console.error("Error fetching chat:", error);
        }
    };


    useEffect(() => {
        if (chatId && !client) {
            console.log("Connecting to WebSocket...");
            const client = new Client({
                brokerURL: "ws://localhost:8080/chatWS",
                connectHeaders: {
                    userId: userId.toString(),
                },
                debug: function (str) {
                    console.log(str);
                },
                reconnectDelay: 5000,
                heartbeatIncoming: 4000,
                heartbeatOutgoing: 4000,
            });

            client.onConnect = () => {
                console.log("Connected to WebSocket");
                client.subscribe(`/topic/chat/${chatId}`, (message) => {
                    const data = JSON.parse(message.body);
                    setMessages((prevMessages) => [...prevMessages, data]);
                });
            };

            client.activate();
            setClient(client);
        }
    }, [chatId]);  // Esto se ejecuta cada vez que cambia el chatId

    // Fetch de mensajes al cargar el chat
    const fetchMessages = async (currentChatId) => {
        if (!currentChatId) {
            setMessages([]);
            return;
        }

        try {
            const response = await fetch(`http://localhost:8080/chats/${currentChatId}/messages`);
            if (!response.ok) {
                throw new Error("Error al cargar mensajes");
            }
            const data = await response.json();

            if (data && data.length > 0) {
                setMessages(data);
            } else {
                setMessages([]);
            }
        } catch (error) {
            console.error("Error fetching messages:", error);
            setMessages([]);
        }
    };

    useEffect(() => {
        if (chatId) {
            fetchMessages(chatId);
        } else {
            setMessages([]);
        }
    }, [chatId]);

    // Manejo del envío de mensajes
    const handleSendMessage = (e) => {
        e.preventDefault();

        if (!inputMessage.trim()) return;

        const messageData = {
            content: inputMessage,
            senderId: userId,
            chatId: chatId,
        };

        try {
            // Envía el mensaje al servidor mediante WebSocket
            client.publish({
                destination: `/app/chat/${chatId}`,
                body: JSON.stringify(messageData),
            });


            // Agrega temporalmente el mensaje al estado del cliente
            setMessages((prevMessages) => [
                ...prevMessages,
                { ...messageData, senderUsername: username }, // Ajusta esto según los datos que necesitas mostrar
            ]);

            setInputMessage(""); // Limpia el campo de entrada
        } catch (error) {
            console.error("Error sending message via WebSocket:", error);
        }
    };



    // Manejo de selección de contacto
    const handleContactSelect = (contact) => {
        setSelectedContact(contact);
        getOrCreateChat(username, contact.username); // Usa el username para el chat
    };


    return (
        <Container>
            <div>

            </div>
            <ContactList>
                <ContactItem key={userId}>
                    <h2>Logged as {username}</h2>
                </ContactItem>
                <h3 style={{ fontSize: '24px', marginBottom: '5px', padding:'10px'}}>
                    Contacts
                </h3>
                {contacts.map((contact) => (
                <ContactItem key={contact.id} onClick={() => handleContactSelect(contact)}>
                        <ProfileImage
                            src={contact.profilePicture || 'null'}
                            alt={`${contact.username} profile`}
                        />
                        <div>
                            <h3>{contact.username}</h3>
                        </div>
                    </ContactItem>
                ))}
            </ContactList>

            <ChatArea>
                {selectedContact ? (
                    <>
                        <Header>

                            <h2>{selectedContact.username}</h2>
                            <ProfileImage
                                src={selectedContact.profilePicture || 'null'}
                                alt={`${selectedContact.username} profile`}
                            />
                        </Header>

                        <MessageList>
                            {messages.map((message, index) => (
                                <Message key={message.id || index} $isUser={message.senderId === userId}>
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
                    <MessageInicio>
                        <Image
                            src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQlHGITJiW_D_3sb6Qb4SnJDym4rdp-9ip84Q&s"
                            alt="logo pimeva"
                        />
                        <Text>Selecciona un contacto para iniciar a chatear!</Text>
                    </MessageInicio>
                )}
            </ChatArea>
        </Container>
    );
};

export default Chat;

