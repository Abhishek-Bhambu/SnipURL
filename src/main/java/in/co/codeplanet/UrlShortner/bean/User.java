package in.co.codeplanet.UrlShortner.bean;

public class User
{
    private String userName;
    private  String email;
    private String password;

    private int otp;
    private String newPassword;

    public int getOtp() {
        return otp;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setOtp(int otp)
    {
        this.otp = otp;
    }

    public User(String userName, String email, String password)
    {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
