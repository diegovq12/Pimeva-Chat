package com.pimeva.pimevachat.modelos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ContactDTO {
    private String id;
    private String username;
    private String profilePicture;

}
