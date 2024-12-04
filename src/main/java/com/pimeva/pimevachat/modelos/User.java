package com.pimeva.pimevachat.modelos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(collection = "users")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private String profilePicture;
    private boolean online;
    @DBRef //Marca una relacion con otros usuarios
    private List<User> contacts = new ArrayList<>();
    @DBRef
    private List<User> contactRequests = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username); // Usa el atributo único
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

}
