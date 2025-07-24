package member;

import java.time.LocalDate;

public class LibraryMember {
    private String id;
    private String password;
    private String name;
    private boolean isAdmin;
    private LocalDate penaltyEndDate;
    
    public LibraryMember(String id, String password, String name) {
        this(id, password, name, false);
    }

    public LibraryMember(String id, String password, String name, boolean isAdmin) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.isAdmin = isAdmin;
    }

    
    public boolean isBanned() {
        return penaltyEndDate != null && LocalDate.now().isBefore(penaltyEndDate);
    }

    public void banForDays(int days) {
        this.penaltyEndDate = LocalDate.now().plusDays(days);
    }

    public LocalDate getPenaltyEndDate() {
        return penaltyEndDate;
    }

    
    public String getId() { return id; }
    public boolean checkPassword(String inputPw) { return this.password.equals(inputPw); }
    public String getName() { return name; }
    public boolean isAdmin() { return isAdmin; }
    public String checkPassword() {return password;}
    public String getPassword() {return password;}
}