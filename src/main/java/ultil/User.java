package ultil;

public class User {
    String name;
    String password;
    String username;
    int id;
    String email;
    int id_role;

    public int getId() {
        return id;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {

        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {

        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId_role(int id_role) {
        this.id_role = id_role;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getId_role() {
        return id_role;
    }
}
