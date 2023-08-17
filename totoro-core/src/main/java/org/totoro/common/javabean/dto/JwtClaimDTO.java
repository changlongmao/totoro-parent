package org.totoro.common.javabean.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Jwt参数
 * @author ChangLF 2023/08/15
 */
@Data
@Accessors(chain = true)
public class JwtClaimDTO {

    private String userId;

    private String rearName;

    private String username;

}
