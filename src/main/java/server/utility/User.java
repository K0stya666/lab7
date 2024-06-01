package server.utility;

import global.tools.Validatable;

import java.io.Serial;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private final String userName;
    private final String password;
    private String salt;
    private Date lastLoginDate;

    public User(String userName, String password) throws NoSuchAlgorithmException {
        this.userName = userName;
        this.password = password;
        this.lastLoginDate = new Date();
    }

    public int getId() { return id; }
    public String getUsername() { return userName; }
    public String getPassword() { return password; }
    public String getSalt() { return salt; }
    public Date getlastLoginDate() { return lastLoginDate; }

    public void setId(int id) {
        this.id = id;
    }
    public void setSalt(String salt) { this.salt = salt; }
    public void setLastLoginDate(Date lastLoginDate) { this.lastLoginDate = lastLoginDate; }

    /**
     * Создание хэш-пароля
     * @param password пароль
     * @return хэшированный пароль
     * @throws NoSuchAlgorithmException ошибка алгоритма хэширования
     */
    public String createPasswordHash(String password) throws NoSuchAlgorithmException {
        var salt = generateSalt();
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt.getBytes());
        byte[] passHash = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(passHash);
    }
    /**
     * Создание соли для пароля
     * @return соль
     */
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        this.salt = Base64.getEncoder().encodeToString(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
