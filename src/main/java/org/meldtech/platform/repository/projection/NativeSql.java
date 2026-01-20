package org.meldtech.platform.repository.projection;

public interface NativeSql {
    String SELECT_USER_PROFILE_QUERY = "SELECT up.* FROM user_profile up ";
    String USER_SEARCH =  SELECT_USER_PROFILE_QUERY +
            "WHERE lower(first_name) LIKE :firstName OR " +
            "lower(last_name) LIKE :lastName OR " +
            "lower(middle_name) LIKE :middleName OR " +
            "lower(email) LIKE :email OR " +
            "lower(phone_number) LIKE :phoneNumber ";

    String USER_BY_APPID =  SELECT_USER_PROFILE_QUERY +
            "INNER JOIN public.user u " +
            "ON up.id = u.id " +
            "WHERE u.app_id = :appId ";

    String USER_BY_APPID_TENANT =  SELECT_USER_PROFILE_QUERY +
            "INNER JOIN public.user u " +
            "ON up.id = u.id " +
            "WHERE u.app_id = :appId " +
            "AND u.tenant_id = :tenantId";
}
