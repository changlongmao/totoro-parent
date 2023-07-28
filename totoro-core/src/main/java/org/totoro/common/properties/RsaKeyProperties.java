package org.totoro.common.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

/**
 * RsaKey管理
 * @author changlf 2023-05-24
 */
@Slf4j
@Data
@ConfigurationProperties("rsa")
@Component
public class RsaKeyProperties {

    /**
     * 公钥
     */
    @NotBlank
    private String publicKey;

    /**
     * 私钥
     */
    @NotBlank
    private String privateKey;

}