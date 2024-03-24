package in.co.codeplanet.UrlShortner.controller;

import in.co.codeplanet.UrlShortner.bean.User;
import in.co.codeplanet.UrlShortner.utility.Otp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import in.co.codeplanet.UrlShortner.bean.User;
import in.co.codeplanet.UrlShortner.bean.EmailDetails;
import in.co.codeplanet.UrlShortner.service.EmailService;
import in.co.codeplanet.UrlShortner.bean.User;

import java.sql.*;
import java.util.HashMap;

@RestController
public class UrlController {

    @PostMapping("register")
    public String signUp(@RequestBody User user) {
        try (Connection con = jdbc.getDataSource().getConnection()) {
            String query1 = "select*from user where userName=? or email=?;";
            PreparedStatement stmt1 = con.prepareStatement(query1);
            stmt1.setString(1, user.getUserName());
            stmt1.setString(2, user.getEmail());
            ResultSet rs = stmt1.executeQuery();
            if (rs.next() == true)
                return "Username or email already exists";
            else {
                int otp = Integer.parseInt(Otp.generateOtp(4));
                EmailDetails emailDetails = new EmailDetails(user.getEmail(), "Otp Verification", "your otp is: " + otp);
                emailService.sendMail(emailDetails);


                String query = "insert into user(username,password,email,otp,is_verified) values(?,?,?,?,?);";

                PreparedStatement stmt = con.prepareStatement(query);

                stmt.setString(1, user.getUserName());
                stmt.setString(2, user.getPassword());
                stmt.setString(3, user.getEmail());
                stmt.setInt(4, otp);
                stmt.setInt(5, 0);

                int result = stmt.executeUpdate();
                return "user Form is successfully submitted";
            }
        } catch (Exception e) {
            return "Something went wrong...";
        }
    }

    @Autowired
    private EmailService emailService;

    @PostMapping("sendOtp")
    public String sendOtp(@RequestBody EmailDetails emailDetails) {
        String message = emailService.sendMail(emailDetails);
        return message;
    }

    @PostMapping("varification")
    public String emailVarification(@RequestBody User user) throws Exception {
        try (Connection con = jdbc.getDataSource().getConnection()) {
            String query1 = "select otp from user where email=?;";
            PreparedStatement stmt2 = con.prepareStatement(query1);
            stmt2.setString(1, user.getEmail());
            ResultSet rs = stmt2.executeQuery();
            if (rs.next() == true)
            {
                if (rs.getInt(1) == user.getOtp())
                {
                    String query2 = "update user set is_verified=1 where email=?";
                    PreparedStatement stmt3 = con.prepareStatement(query2);
                    stmt3.setString(1, user.getEmail());
                    stmt3.executeUpdate();
                    return "Your profile has been varified";
                } else {
                    return "Otp Dosn't match";
                }
            } else
            {
                return "email id dosen't Exists";
            }
        } catch (Exception e) {
            return "Something went wrong";
        }
    }

    @PostMapping("login")
    public String login(@RequestBody User user) {
        try (Connection con = jdbc.getDataSource().getConnection()) {
            String query = "select * from user where email=?and password=?and is_verified=1;";
            PreparedStatement smtp = con.prepareStatement(query);
            smtp.setString(1, user.getEmail());
            smtp.setString(2, user.getPassword());
            ResultSet rs = smtp.executeQuery();
            if (rs.next() == true)
                return "Login Successful";
            else
                return "Either login ,password or email not verified";
        } catch (Exception e) {
            return "Something went wrong";
        }
    }

    @GetMapping("forgotpassword")
    public String forgotPassword(@RequestParam String userName) {
        try (Connection con = jdbc.getDataSource().getConnection()) {
            String query = "select email from user where username=?;";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() == true) {
                String Email = rs.getString(1);

                String password = Otp.generateOtp(8);

                EmailDetails emailDetails = new EmailDetails(Email, "new password", "your new password  is: " + password);
                emailService.sendMail(emailDetails);

                String query1 = "update user set password=? where email=?";
                PreparedStatement stmt1 = con.prepareStatement(query1);
                stmt1.setString(1, password);
                stmt1.setString(2, Email);
                stmt1.executeUpdate();
                return "your password has been updated and send to your mail";

            } else
                return "Username dosn't exists";

        } catch (Exception e) {
            return "Something went wrong";
        }
    }

    @PostMapping("passwordchange")
    public String passwordchange(@RequestBody User user) {
        try (Connection con = jdbc.getDataSource().getConnection()) {
            String query = "select*from user where email=?and password=?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            ResultSet rs = stmt.executeQuery();
            if (rs.next() == true) {
                String query1="update user set password=? where email=?";
                PreparedStatement stmt1=con.prepareStatement(query1);
                stmt1.setString(1, user.getNewPassword());
                stmt1.setString(2, user.getEmail());
                stmt1.executeUpdate();
                return "your password has been successfully updated";
            }
            else
                return "your email or old Password is wrong";
        } catch (Exception e) {
            return "Something went wrong...";
        }
    }
    @GetMapping("urlshortner")
    public String urlShortner(@RequestParam String longUrl,String shortUrl,Integer userId)
    {
        try (Connection con = jdbc.getDataSource().getConnection()) {
            String query1="select*from url where short_url=?";
            PreparedStatement stmt1=con.prepareStatement(query1);
            stmt1.setString(1,"cpt.cc/"+shortUrl);
            ResultSet rs= stmt1.executeQuery();
            if(rs.next()==true)
                return "Short url already exists";

            else {
                if (userId == null)
                    userId = 0;
                String query = "insert into url values(?,?,?)";
                PreparedStatement stmt = con.prepareStatement(query);
                stmt.setString(1, longUrl);
                stmt.setString(2, "cpt.cc/"+shortUrl);
                stmt.setInt(3, userId);
                stmt.executeUpdate();
                return "short url is successfully created";
            }
        }
        catch (Exception e) {
            return "Something went wrong...";
        }
    }
    @GetMapping("longurl")
    public String longurl(@RequestParam String shortUrl)
    {
        try (Connection con = jdbc.getDataSource().getConnection()) {
            String query = "select long_url from url where short_url=?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setString(1, "cpt.cc/" + shortUrl);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()==true)
                return rs.getString(1);
            else
                return "long url is not present corresponding to this short url";
        }
        catch (Exception e) {
            return "Something went wrong...";
        }

    }
    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping("allurl")
    public HashMap<String, String> allUrl(@RequestParam int userId)
    {
        try (Connection con = jdbc.getDataSource().getConnection()) {
            String query = "select long_url,short_url from url where user_id=?";
            PreparedStatement stmt = con.prepareStatement(query);
            stmt.setInt(1,userId);
            ResultSet rs=stmt.executeQuery();
            HashMap<String,String> hm=new HashMap<>();
            while(rs.next()==true)
            {
                hm.put(rs.getString(2),rs.getString(1));
            }
            return hm;

        }
        catch (Exception e) {
           return null;
        }

    }

}



