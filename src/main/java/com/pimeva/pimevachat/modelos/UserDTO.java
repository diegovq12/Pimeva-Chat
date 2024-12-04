package com.pimeva.pimevachat.modelos;

import lombok.Data;

@Data
public class UserDTO {
    private String id;
    private String username;
    private String profilePicture;
    private boolean online;


    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.profilePicture = user.getProfilePicture();
        this.online = user.isOnline();
    }
}
