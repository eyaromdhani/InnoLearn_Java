package org.example.Services;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.example.Entities.G_user;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.example.utils.MyDataBase;



public class UserService implements ICrud<G_user>{

    //atributes

    Connection con;

    //constracteur

    public UserService() {
        con = MyDataBase.getInstance().getConnection();

    }


    //methodes


    @Override
    public void create(G_user user) throws SQLException {
        //variables de la methode
        String sql = "INSERT INTO `user`(`name`, `username`, `email`, `password_hash`, `country_code`, " +
                "`phone_number`, `roles`, `is_active`, `avatar_url`, `verification_key`, `key_expires_at`, " +
                "`is_phone_verified`, `failed_login_attempts`, `last_failed_login_attempt`, `is_banned`, " +
                "`admin_hardware_key_hash`, `admin_face_signature_hash`) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        PreparedStatement ps = con.prepareStatement(sql);

        //tritements
        ps.setString(1, user.getName());
        ps.setString(2, user.getUsername());
        ps.setString(3, user.getEmail());
        ps.setString(4, user.getPasswordHash() != null ? BCrypt.withDefaults().hashToString(12, user.getPasswordHash().toCharArray()) : "");
        ps.setString(5, user.getCountryCode() != null ? user.getCountryCode() : "+216");
        ps.setString(6, user.getPhoneNumber());
        ps.setString(7, user.getRoles());
        ps.setBoolean(8, Boolean.TRUE.equals(user.getActive()));
        ps.setString(9, user.getAvatarUrl());
        ps.setString(10, user.getVerificationKey());
        ps.setString(11, user.getKeyExpiresAt() != null ? user.getKeyExpiresAt().toString() : null);
        ps.setBoolean(12, Boolean.TRUE.equals(user.getPhoneVerified()));
        ps.setInt(13, user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0);
        ps.setString(14, user.getLastFailedLoginAttempt() != null ? user.getLastFailedLoginAttempt().toString() : null);
        ps.setBoolean(15, Boolean.TRUE.equals(user.getBanned()));
        ps.setString(16, user.getAdminHardwareKeyHash());
        ps.setString(17, user.getAdminFaceSignatureHash());

        ps.executeUpdate();


    }

    @Override
    public List<G_user> read()  throws SQLException {

        //variables de la methode
        List<G_user> usersList = new ArrayList<>();
        String sql="SELECT * FROM `user`";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        //tritements
        while(rs.next()){
            G_user user = new G_user();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setUsername(rs.getString("username"));
            user.setEmail(rs.getString("email"));
            user.setPasswordHash(rs.getString("password_hash"));
            user.setCountryCode(rs.getString("country_code"));
            user.setPhoneNumber(rs.getString("phone_number"));
            user.setRoles(rs.getString("roles"));
            user.setActive(rs.getBoolean("is_active"));
            user.setAvatarUrl(rs.getString("avatar_url"));
            user.setVerificationKey(rs.getString("verification_key"));
            user.setPhoneVerified(rs.getBoolean("is_phone_verified"));
            user.setBanned(rs.getBoolean("is_banned"));
            user.setAdminHardwareKeyHash(rs.getString("admin_hardware_key_hash"));
            user.setAdminFaceSignatureHash(rs.getString("admin_face_signature_hash"));
            user.setKeyExpiresAt(rs.getTimestamp("key_expires_at") != null ?
                    rs.getTimestamp("key_expires_at").toLocalDateTime() : null);
            user.setLastFailedLoginAttempt(rs.getTimestamp("last_failed_login_attempt") != null ?
                    rs.getTimestamp("last_failed_login_attempt").toLocalDateTime() : null);
            usersList.add(user);
        }

        return usersList;
    }

    @Override
    public void update(G_user u) throws SQLException {
        Scanner sc = new Scanner(System.in);

        String selectSql = "SELECT * FROM `user` WHERE `id`=?";
        PreparedStatement selectPs = con.prepareStatement(selectSql);
        selectPs.setInt(1, u.getId());
        ResultSet rs = selectPs.executeQuery();

        if (!rs.next()) {
            System.out.println("Utilisateur introuvable.");
            return;
        }

        String currentName = rs.getString("name");
        String currentUsername = rs.getString("username");
        String currentEmail = rs.getString("email");
        String currentPasswordHash = rs.getString("password_hash");
        String currentCountryCode = rs.getString("country_code");
        String currentPhoneNumber = rs.getString("phone_number");
        String currentRoles = rs.getString("roles");
        boolean currentIsActive = rs.getBoolean("is_active");
        String currentAvatarUrl = rs.getString("avatar_url");
        String currentVerificationKey = rs.getString("verification_key");
        String currentKeyExpiresAt = rs.getTimestamp("key_expires_at") != null ? rs.getTimestamp("key_expires_at").toString() : null;
        boolean currentIsPhoneVerified = rs.getBoolean("is_phone_verified");
        int currentFailedLoginAttempts = rs.getInt("failed_login_attempts");
        String currentLastFailedLoginAttempt = rs.getTimestamp("last_failed_login_attempt") != null ? rs.getTimestamp("last_failed_login_attempt").toString() : null;
        boolean currentIsBanned = rs.getBoolean("is_banned");
        String currentAdminHardwareKeyHash = rs.getString("admin_hardware_key_hash");
        String currentAdminFaceSignatureHash = rs.getString("admin_face_signature_hash");

        String sql = "UPDATE `user` SET `name`=?,`username`=?,`email`=?,`password_hash`=?,`country_code`=?," +
                "`phone_number`=?,`roles`=?,`is_active`=?,`avatar_url`=?,`verification_key`=?,`key_expires_at`=?," +
                "`is_phone_verified`=?,`failed_login_attempts`=?,`last_failed_login_attempt`=?,`is_banned`=?," +
                "`admin_hardware_key_hash`=?,`admin_face_signature_hash`=? WHERE `id`=?";

        PreparedStatement ps = con.prepareStatement(sql);
/*
        System.out.println("donner le nom (" + currentName + "): ");
        String name = sc.nextLine();
        ps.setString(1, name.isEmpty() ? currentName : name);

        System.out.println("donner le username (" + currentUsername + "): ");
        String username = sc.nextLine();
        ps.setString(2, username.isEmpty() ? currentUsername : username);

        System.out.println("donner l'email (" + currentEmail + "): ");
        String email = sc.nextLine();
        ps.setString(3, email.isEmpty() ? currentEmail : email);

        System.out.println("donner le password_hash (" + currentPasswordHash + "): ");
        String passwordHash = sc.nextLine();
        ps.setString(4, passwordHash.isEmpty() ? currentPasswordHash : passwordHash);

        System.out.println("donner le country_code (" + currentCountryCode + "): ");
        String countryCode = sc.nextLine();
        ps.setString(5, countryCode.isEmpty() ? currentCountryCode : countryCode);

        System.out.println("donner le phone_number (" + currentPhoneNumber + "): ");
        String phoneNumber = sc.nextLine();
        ps.setString(6, phoneNumber.isEmpty() ? currentPhoneNumber : phoneNumber);

        System.out.println("donner les roles (" + currentRoles + "): ");
        String roles = sc.nextLine();
        ps.setString(7, roles.isEmpty() ? currentRoles : roles);

        System.out.println("is_active (o/n) (" + currentIsActive + "): ");
        String isActiveInput = sc.nextLine();
        boolean isActive = isActiveInput.isEmpty() ? currentIsActive : isActiveInput.equalsIgnoreCase("o");
        ps.setBoolean(8, isActive);

        System.out.println("donner l'avatar_url (" + currentAvatarUrl + "): ");
        String avatarUrl = sc.nextLine();
        ps.setString(9, avatarUrl.isEmpty() ? currentAvatarUrl : avatarUrl);

        System.out.println("donner le verification_key (" + currentVerificationKey + "): ");
        String verificationKey = sc.nextLine();
        ps.setString(10, verificationKey.isEmpty() ? currentVerificationKey : verificationKey);

        System.out.println("donner le key_expires_at yyyy-MM-dd HH:mm:ss (" + currentKeyExpiresAt + "): ");
        String keyExpiresAt = sc.nextLine();
        ps.setString(11, keyExpiresAt.isEmpty() ? currentKeyExpiresAt : keyExpiresAt);

        System.out.println("is_phone_verified (o/n) (" + currentIsPhoneVerified + "): ");
        String isPhoneVerifiedInput = sc.nextLine();
        boolean isPhoneVerified = isPhoneVerifiedInput.isEmpty() ? currentIsPhoneVerified : isPhoneVerifiedInput.equalsIgnoreCase("o");
        ps.setBoolean(12, isPhoneVerified);

        System.out.println("donner failed_login_attempts (" + currentFailedLoginAttempts + "): ");
        String failedLoginAttemptsInput = sc.nextLine();
        int failedLoginAttempts = failedLoginAttemptsInput.isEmpty() ? currentFailedLoginAttempts : Integer.parseInt(failedLoginAttemptsInput);
        ps.setInt(13, failedLoginAttempts);

        System.out.println("donner last_failed_login_attempt yyyy-MM-dd HH:mm:ss (" + currentLastFailedLoginAttempt + "): ");
        String lastFailedLoginAttempt = sc.nextLine();
        ps.setString(14, lastFailedLoginAttempt.isEmpty() ? currentLastFailedLoginAttempt : lastFailedLoginAttempt);

        System.out.println("is_banned (o/n) (" + currentIsBanned + "): ");
        String isBannedInput = sc.nextLine();
        boolean isBanned = isBannedInput.isEmpty() ? currentIsBanned : isBannedInput.equalsIgnoreCase("o");
        ps.setBoolean(15, isBanned);

        System.out.println("donner admin_hardware_key_hash (" + currentAdminHardwareKeyHash + "): ");
        String adminHardwareKeyHash = sc.nextLine();
        ps.setString(16, adminHardwareKeyHash.isEmpty() ? currentAdminHardwareKeyHash : adminHardwareKeyHash);

        System.out.println("donner admin_face_signature_hash (" + currentAdminFaceSignatureHash + "): ");
        String adminFaceSignatureHash = sc.nextLine();
        ps.setString(17, adminFaceSignatureHash.isEmpty() ? currentAdminFaceSignatureHash : adminFaceSignatureHash);

*/
        ps.setString(1, u.getName());
        ps.setString(2, u.getUsername());
        ps.setString(3, u.getEmail());
        ps.setString(4, u.getPasswordHash());
        ps.setString(5, u.getCountryCode());
        ps.setString(6, u.getPhoneNumber());
        ps.setString(7, u.getRoles());
        ps.setBoolean(8, u.getActive());
        ps.setString(9, u.getAvatarUrl());
        ps.setString(10, u.getVerificationKey());
        ps.setString(11, u.getKeyExpiresAt() != null ? u.getKeyExpiresAt().toString() : null);
        ps.setBoolean(12, u.getPhoneVerified());
        ps.setInt(13, u.getFailedLoginAttempts() != null ? u.getFailedLoginAttempts() : 0);
        ps.setString(14, u.getLastFailedLoginAttempt() != null ? u.getLastFailedLoginAttempt().toString() : null);
        ps.setBoolean(15, u.getBanned());
        ps.setString(16, u.getAdminHardwareKeyHash());
        ps.setString(17, u.getAdminFaceSignatureHash());
        ps.setInt(18, u.getId());

        ps.executeUpdate();
        System.out.println("Utilisateur mis a jour avec succes.");
    }

    @Override
    public void delete(int id) throws SQLException {
        //variables de la methode
        String sql = "DELETE FROM `user` WHERE `id` = ?";

        //tritements
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();

    }

    private G_user mapUser(ResultSet rs) throws SQLException {
        G_user u = new G_user();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setCountryCode(rs.getString("country_code"));
        u.setPhoneNumber(rs.getString("phone_number"));
        u.setRoles(rs.getString("roles"));
        u.setActive(rs.getBoolean("is_active"));
        u.setBanned(rs.getBoolean("is_banned"));
        return u;
    }


    public G_user findByUsername(String username) throws SQLException {
        // Query DB by username or email
        String sql = "SELECT * FROM user WHERE username=? OR email=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapUser(rs);
        }
        return null;
    }

    public void saveResetToken(int userId, String token) throws SQLException {
        String sql = "INSERT INTO password_reset_tokens(user_id, token, expires_at, used) VALUES(?,?,NOW()+INTERVAL 15 MINUTE,FALSE)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, userId);
        ps.setString(2, token);
        ps.executeUpdate();
    }

}
