package com.pimeva.pimevachat.modelos;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class ChatDTO {
    private String id;
    private List<UserDTO> participants;

    public ChatDTO(Chat chat) {
        this.id = chat.getId();
        (this.participants) = chat.getParticipants().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }
}
