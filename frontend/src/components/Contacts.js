import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';

const Contacts = () => {
    const [receiver, setReceiver] = useState('');
    const [contactsRequest, setContacts] = useState([]);
    const [error, setError] = useState('');
    const{username, userId} = useLocation().state;
    const handleAddContact = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post(`http://localhost:8080/api/contacts/send-request?sender=${username}&receiver=${receiver}`);
            alert(response.data);
            setReceiver('');
        } catch (err) {
            setError('Failed to send contact request');
        }
    };

    const acceptContact = async (contactUsername) => {
        try {
            const response = await axios.post(`http://localhost:8080/api/contacts/accept-request?sender=${username}&receiver=${contactUsername}`);
            alert(response.data);
            setReceiver('');
        } catch (err) {
            setError('Failed to send contact request');
        }
    }

    useEffect(() => {
        const fetchContacts = async () => {
            try {
                console.log(userId);
                const response = await axios.get(`http://localhost:8080/api/contacts/get-requests?userId=${userId}`);
                console.log(response.data);
                setContacts(response.data);
            } catch (err) {
                setError('Failed to fetch contacts');
            }
        };
        fetchContacts();
    }, [userId]);

    return (
        <Container>
            <h1>Contacts</h1>
            <h2 style={{marginLeft: '10px'}}>Add Contact</h2>
            <Form onSubmit={handleAddContact}>

                <Input
                    type="text"
                    value={receiver}
                    onChange={(e) => setReceiver(e.target.value)}
                    placeholder="Enter receiver username"
                />
                <Button type="submit">Add Contact</Button>
            </Form>
            {error && <Error>{error}</Error>}
            <h2 style={{marginLeft: '10px'}}>Received Contact Requests</h2>
            <ContactList>
                {contactsRequest.map((contact) => (
                    <ContactItem key={contact.id}>
                        <ProfileImage src={contact.profilePicture} alt="Contact request Image" />
                        {contact.username}
                        <Button style={{marginLeft: '10px'}} onClick={() => acceptContact(contact.username)}>Accept</Button>
                        <Button style={{marginLeft: '10px',backgroundColor:'#f00'}}>Reject</Button>
                    </ContactItem>
                ))}
            </ContactList>
        </Container>
    );
};

const Container = styled.div`
    display: flex;
    flex-direction: column;
    height: 100vh;
    background-color: #f0f2f5;
    //justify-content: center;
    align-items: center;
    padding: 20px;
`;

const Form = styled.form`
    display: flex;
    margin-bottom: 20px;
`;

const Input = styled.input`
    width: 300px;
    padding: 10px;
    margin-left: 10px;
    margin-bottom: 10px;
    font-size: 16px;
`;

const Button = styled.button`
    width: 150px;
    height: 40px;
    margin-left: 10px;
    padding: 10px;
    font-size: 16px;
    cursor: pointer;
`;

const Error = styled.div`
    color: red;
    margin-left: 15px;
    margin-top: -15px;
`;

const ContactList = styled.ul`
    list-style-type: none;
    margin: 0;
    padding: 0;    
    background-color: white;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    border-radius: 10px;
`;

const ContactItem = styled.li`
    padding: 10px;
    //margin-top: 10px;
    border-bottom: 1px solid #ccc;
`;

const ProfileImage = styled.img`
    
    width: 50px;
    height: 50px;
    border-radius: 50%;
    //margin-top: 10px;
    margin-right: 16px;
`;

export default Contacts;