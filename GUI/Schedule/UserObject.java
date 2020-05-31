package GUI.Schedule;

public class UserObject {

    private int userId;
    private String username;
    private boolean active;

    public UserObject(int userId, String userName, int active) {
        this.userId = userId;
        this.username = userName;
        if (active == 1) {
            this.active = true;
        }
        else {
            this.active = false;
        }
    }

    @Override
    public String toString() {
        return this.username;
    }

    public int getUserId() {
        return this.userId;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isActive() {
        return this.active;
    }

}
