package springboot.model;
/*
When you log in, backend checks creds, returns session token
Token needs to be in the header of requests, store in session table
 */

import javax.persistence.*;

@Table(name = "session", indexes = {
        @Index(name = "user_id", columnList = "user_id")
})
@Entity
public class Session {
    @Id
    @Column(name = "token", nullable = false, length = 100)
    private String id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}