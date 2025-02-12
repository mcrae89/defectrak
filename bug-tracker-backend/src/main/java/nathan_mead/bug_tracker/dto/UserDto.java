package nathan_mead.bug_tracker.dto;

public class UserDto {
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private Long userRoleId; // Only the ID, not the full Use Role object

    // Getters and setters

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public Long getUserRoleId() {
        return userRoleId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserRoleId(Long userRoleId) {
        this.userRoleId = userRoleId;
    }
}
