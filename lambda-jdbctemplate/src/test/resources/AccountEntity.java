/*
 * KKP-iBranch, Copyright (c) 2023. All Rights Reserved
 *
 * This file is part of KKP-iBranch.
 *
 *  KKP-iBranch can not be copied and/or distributed without the express permission of BeID Corporation & Brainergy
 */

package cc.peerapat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * package_name=cc.peerapat.repos.generated
 * class_name=AccountGen
 * table_name=accounts
 * primary_keys=id, client_id
 */
@Builder
public record AccountEntity(
        Long id
        , @JsonProperty("client_id") Long clientId
        , @JsonProperty("is_active") Boolean isActive
        , @JsonProperty("is_verify") Boolean isVerify
        , @JsonProperty("is_changepass") Boolean isChangepass
        , @JsonProperty("account_type") Integer accountType
        , @JsonProperty("account_role") Integer accountRole
        , String username
        , @JsonProperty("password_hash") String passwordHash
        , String email
        , String firstname
        , String lastname
        , @JsonProperty("mobile_no") String mobileNo
        , @JsonProperty("meta_json") String metaJson
        , @JsonProperty("avatar_url") String avatarUrl
        , @JsonProperty("staff_code") String staffCode
        , String position
        , @JsonProperty("creator_id") Long creatorId
        , LocalDateTime created) {

}
