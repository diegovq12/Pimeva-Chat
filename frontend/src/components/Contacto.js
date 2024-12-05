import React from 'react';

function ContactCard({ profilePicture, username }) {
    return (
        <div className="contact-card">
            <img src={profilePicture} alt={username} width="50" height="50" />
            <p>{username}</p>
        </div>
    );
}

export default ContactCard;