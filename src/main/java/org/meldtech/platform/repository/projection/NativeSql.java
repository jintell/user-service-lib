package org.meldtech.platform.repository.projection;

public interface NativeSql {
    String SELECT_USER_PROFILE_QUERY = "SELECT * FROM user_profile ";
    String USER_SEARCH =  SELECT_USER_PROFILE_QUERY +
            "WHERE lower(first_name) LIKE :firstName OR " +
            "lower(last_name) LIKE :lastName OR " +
            "lower(middle_name) LIKE :middleName OR " +
            "lower(email) LIKE :email OR " +
            "lower(phone_number) LIKE :phoneNumber ";
}
