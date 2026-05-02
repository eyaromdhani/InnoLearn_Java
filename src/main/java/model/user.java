package model;

import java.time.LocalDateTime;

public class user {

//attributs

    private Integer id;

    private String name;

    private String username;

    private String email;

    private String passwordHash;

    private String countryCode;

    private String phoneNumber;

    private String roles;

    private Boolean isActive;

    private String avatarUrl;

    private String verificationKey;

    private LocalDateTime keyExpiresAt;

    private Boolean isPhoneVerified;

    private Integer failedLoginAttempts;

    private LocalDateTime lastFailedLoginAttempt;

    private Boolean isBanned;

    private String adminHardwareKeyHash;

    private String adminFaceSignatureHash;

//constructeurs

    public user() {}

    public user(String name, String username, String email, String passwordHash, String countryCode,
                String phoneNumber, String roles, Boolean isActive, String avatarUrl,
                String verificationKey, LocalDateTime keyExpiresAt, Boolean isPhoneVerified,
                Integer failedLoginAttempts, LocalDateTime lastFailedLoginAttempt, Boolean isBanned,
                String adminHardwareKeyHash, String adminFaceSignatureHash) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.roles = roles;
        this.isActive = isActive;
        this.avatarUrl = avatarUrl;
        this.verificationKey = verificationKey;
        this.keyExpiresAt = keyExpiresAt;
        this.isPhoneVerified = isPhoneVerified;
        this.failedLoginAttempts = failedLoginAttempts;
        this.lastFailedLoginAttempt = lastFailedLoginAttempt;
        this.isBanned = isBanned;
        this.adminHardwareKeyHash = adminHardwareKeyHash;
        this.adminFaceSignatureHash = adminFaceSignatureHash;
    }

    //methodes

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getVerificationKey() {
        return verificationKey;
    }

    public void setVerificationKey(String verificationKey) {
        this.verificationKey = verificationKey;
    }

    public LocalDateTime getKeyExpiresAt() {
        return keyExpiresAt;
    }

    public void setKeyExpiresAt(LocalDateTime keyExpiresAt) {
        this.keyExpiresAt = keyExpiresAt;
    }

    public Boolean getPhoneVerified() {
        return isPhoneVerified;
    }

    public void setPhoneVerified(Boolean phoneVerified) {
        isPhoneVerified = phoneVerified;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void setFailedLoginAttempts(Integer failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public LocalDateTime getLastFailedLoginAttempt() {
        return lastFailedLoginAttempt;
    }

    public void setLastFailedLoginAttempt(LocalDateTime lastFailedLoginAttempt) {
        this.lastFailedLoginAttempt = lastFailedLoginAttempt;
    }

    public Boolean getBanned() {
        return isBanned;
    }

    public void setBanned(Boolean banned) {
        isBanned = banned;
    }

    public String getAdminHardwareKeyHash() {
        return adminHardwareKeyHash;
    }

    public void setAdminHardwareKeyHash(String adminHardwareKeyHash) {
        this.adminHardwareKeyHash = adminHardwareKeyHash;
    }

    public String getAdminFaceSignatureHash() {
        return adminFaceSignatureHash;
    }

    public void setAdminFaceSignatureHash(String adminFaceSignatureHash) {
        this.adminFaceSignatureHash = adminFaceSignatureHash;
    }

    @Override
    public String toString() {
        return "user{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", roles='" + roles + '\'' +
                ", isActive=" + isActive +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", verificationKey='" + verificationKey + '\'' +
                ", keyExpiresAt=" + keyExpiresAt +
                ", isPhoneVerified=" + isPhoneVerified +
                ", failedLoginAttempts=" + failedLoginAttempts +
                ", lastFailedLoginAttempt=" + lastFailedLoginAttempt +
                ", isBanned=" + isBanned +
                ", adminHardwareKeyHash='" + adminHardwareKeyHash + '\'' +
                ", adminFaceSignatureHash='" + adminFaceSignatureHash + '\'' +
                '}';
    }
}
